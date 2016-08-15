package com.base2.kagura.contribute.dynamodb.report.connector;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.internal.PageIterable;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfig;
import com.base2.kagura.core.authentication.AuthenticationProvider;
import com.base2.kagura.core.report.connectors.ReportConnector;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by arran on 16/06/2016.
 */
public class DynamoDbConnector extends ReportConnector {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDbConnector.class);

	AmazonDynamoDBClient client;
	String action;
	private List<Map<String, Object>> rows;
	private ScanSpec scanSpec;
	private QuerySpec querySpec;
	String table;

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
		if (StringUtils.isNotBlank(reportConfig.getRegion())) {
			if (RegionUtils.getRegion(reportConfig.getRegion()) != null) {
				client.setRegion(RegionUtils.getRegion(reportConfig.getRegion()));
			}
		}
		if (StringUtils.isNotBlank(reportConfig.getEndpoint())) {
			client.setEndpoint(reportConfig.getEndpoint());
			if (StringUtils.isNotBlank(reportConfig.getRegion())) {
				client.setSignerRegionOverride(reportConfig.getRegion());
			}
		}
		this.action = reportConfig.getAction();
		if (StringUtils.isBlank(this.action)) {
			this.action = "query";
		}
		if (reportConfig.getDynamo().getQuery() != null) {
			if (this.action.equals("scan")) {
				scanSpec = reportConfig.getDynamo().getQuery().toScanSpec();
			} else {
				querySpec = reportConfig.getDynamo().getQuery().toQuerySpec();
			}
			this.table = reportConfig.getDynamo().getQuery().getTable();
		} else {
			querySpec = null;
			scanSpec = null;
		}
	}

	@Override
	public List<Map<String, Object>> getRows() {
		return rows;
	}

	protected List<Map<String, Object>> scan(Map<String, Object> extra) {
		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(this.table);
		ItemCollection<ScanOutcome> results = table.scan(scanSpec);
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

	protected List<Map<String, Object>> query(Map<String, Object> extra) {
		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(this.table);
		ItemCollection<QueryOutcome> results = table.query(querySpec.withMaxPageSize(this.getPageLimit()));
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(getPageLimit());
		PageIterable<Item, QueryOutcome> e = results.pages();
		int i = 0;
		for (Page<Item, QueryOutcome> page : e) {
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
