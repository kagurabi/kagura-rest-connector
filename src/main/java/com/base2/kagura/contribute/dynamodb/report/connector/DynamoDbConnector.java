package com.base2.kagura.contribute.dynamodb.report.connector;

import bsh.EvalError;
import bsh.Interpreter;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfig;
import com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.DynamoQuery;
import com.base2.kagura.core.report.connectors.ReportConnector;
import com.base2.kagura.core.report.parameterTypes.ParamConfig;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by arran on 16/06/2016.
 */
public class DynamoDbConnector extends ReportConnector {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDbConnector.class);

	AmazonDynamoDB client;
	String action;
	String beanScript;
	private List<Map<String, Object>> rows;
	private Map<String, String> names;
	private Map<String, Object> values;
	private DynamoQuery dynamoQuery
		;

	/**
	 * Does a shallow copy of the necessary reportConfig values. Initializes the error structure.
	 *
	 * @param reportConfig
	 */
	public DynamoDbConnector(DynamoDbReportConfig reportConfig) {
		super(reportConfig);
		if (StringUtils.isNotBlank(reportConfig.getAwsAccessKeyId())) {
			client = new AmazonDynamoDBClient(new BasicAWSCredentials(reportConfig.getAwsAccessKeyId(), reportConfig.getAwsSecretAccessKey()));
		} else {
			client = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain());
		}
		setup(reportConfig);
	}

	/**
	 * Does a shallow copy of the necessary reportConfig values. Initializes the error structure.
	 *
	 * @param reportConfig
	 */
	public DynamoDbConnector(DynamoDbReportConfig reportConfig, AmazonDynamoDB client) {
		super(reportConfig);
		this.client = client;
		setup(reportConfig);
	}

	private void setup(DynamoDbReportConfig reportConfig) {
		if (StringUtils.isNotBlank(reportConfig.getRegion())) {
			Region region = RegionUtils.getRegion(reportConfig.getRegion());
			if (region != null) {
				client.setRegion(region);
			}
		}
		if (StringUtils.isNotBlank(reportConfig.getEndpoint())) {
			client.setEndpoint(reportConfig.getEndpoint());
			if (StringUtils.isNotBlank(reportConfig.getRegion()) && client instanceof AmazonDynamoDBClient) {
				((AmazonDynamoDBClient)client).setSignerRegionOverride(reportConfig.getRegion());
			}
		}
		this.action = reportConfig.getAction();
		if (StringUtils.isBlank(this.action)) {
			this.action = "query";
		}
		if (reportConfig.getDynamo().getQuery() != null) {
			this.dynamoQuery = reportConfig.getDynamo().getQuery();
		}
		this.names = reportConfig.getDynamo().getQuery().getNames();
		this.values = reportConfig.getDynamo().getQuery().getValues();
		this.beanScript = reportConfig.getBeanScript();
	}

	public Map<String, Object> createValueMap(Interpreter bsh) {
		Map<String, Object> result = new ValueMap();
		for (Map.Entry<String, Object> each : this.values.entrySet()) {
			Object value = each.getValue();
			if (value instanceof String) {
				try {
					value = bsh.eval((String)each.getValue());
					bsh.set(each.getKey(), value);
				} catch (EvalError ex) {
					errors.add(ex.getMessage());
					ex.printStackTrace();
				}
			}
			result.put(":" + each.getKey(), value);
		}
		return result;
	}

	public Map<String, String> createNameMap() {
		Map<String, String> result = new NameMap();
		for (Map.Entry<String, String> each : this.names.entrySet()) {
			String value = each.getValue();
			result.put("#" + each.getKey(), value);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getRows() {
		return rows;
	}

	protected List<Map<String, Object>> scan(Map<String, Object> extra) {
		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(dynamoQuery.getTable());
		Interpreter bsh = null;
		try {
			bsh = getInterpreter(extra);
		} catch (EvalError ex) {
			ex.printStackTrace();
			this.errors.add(ex.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		ScanSpec scanSpec;
		try {
			scanSpec = this.dynamoQuery.toScanSpec(bsh).withNameMap(createNameMap());
		} catch (EvalError evalError) {
			this.errors.add(evalError.getMessage());
			evalError.printStackTrace();
			return null;
		}
		scanSpec = scanSpec.withValueMap(createValueMap(bsh));
		ItemCollection<ScanOutcome> results = table.scan(scanSpec.withMaxPageSize(this.getPageLimit()));
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(getPageLimit());
		int i = 0;
		for (Page<Item, ScanOutcome> page : results.pages()) {
			LOG.debug("Page: " + i);
			LOG.debug("Page size: " + page.size());
			if (!page.hasNextPage()) {
				break;
			}
			if (i == getPage()) {
				LOG.debug("Getting items for page");
				for (Item each : page) {
					result.add(each.asMap());
				}
			}
			if (i >= getPage()) {
				break;
			}
			i++;
		}
		return result;
	}

	public Interpreter getInterpreter(Map<String, Object> extra) throws EvalError, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Interpreter bsh = new Interpreter();
		if (StringUtils.isNotBlank(this.beanScript)) {
			bsh.eval(this.beanScript);
		}
		bsh.set("extra", extra);
		if (extra != null) {
			for (Map.Entry<String, Object> each : extra.entrySet()) {
				bsh.set(each.getKey(), each.getValue());
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		if (parameterConfig != null)
		{
			for (ParamConfig paramConfig : parameterConfig)
			{
				bsh.set(paramConfig.getId(), PropertyUtils.getProperty(paramConfig, "value"));
			}
		}
		return bsh;
	}

	protected List<Map<String, Object>> query(Map<String, Object> extra) {
		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(this.dynamoQuery.getTable());
		Interpreter bsh = null;
		QuerySpec querySpec;
		try {
			bsh = getInterpreter(extra);
		} catch (EvalError ex) {
			ex.printStackTrace();
			this.errors.add(ex.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		try {
			querySpec = this.dynamoQuery.toQuerySpec(bsh).withNameMap(createNameMap());
		} catch (EvalError evalError) {
			this.errors.add(evalError.getMessage());
			evalError.printStackTrace();
			return null;
		}
		querySpec = querySpec.withValueMap(createValueMap(bsh));
		ItemCollection<QueryOutcome> results = table.query(querySpec.withMaxPageSize(this.getPageLimit()));
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(getPageLimit());
		int i = 0;
		for (Page<Item, QueryOutcome> page : results.pages()) {
			LOG.debug("Page: " + i);
			LOG.debug("Page size: " + page.size());
			if (!page.hasNextPage()) {
				break;
			}
			if (i == getPage()) {
				LOG.debug("Getting items for page");
				for (Item each : page) {
					result.add(each.asMap());
				}
			}
			if (i >= getPage()) {
				break;
			}
			i++;
		}
		return result;
	}

	@Override
	protected void runReport(Map<String, Object> extra) {
		rows = new ArrayList<Map<String, Object>>();
		if (this.action.equals("scan")) {
			rows = scan(extra);
		} else {
			rows = query(extra);
		}
	}

}
