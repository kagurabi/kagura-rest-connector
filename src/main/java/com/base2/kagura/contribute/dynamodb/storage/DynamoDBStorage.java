package com.base2.kagura.contribute.dynamodb.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.marshallers.StringToStringMarshaller;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.util.StringInputStream;
import com.base2.kagura.core.report.configmodel.ReportsConfig;
import com.base2.kagura.core.storage.ReportsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arran on 7/10/2016.
 */
public class DynamoDBStorage extends ReportsProvider<String> {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDBStorage.class);

	String reportsTable;
	private AmazonDynamoDB dynamoDB;

	public DynamoDBStorage(AWSCredentials credentials, String reportsTable) {
		AmazonDynamoDB client = new AmazonDynamoDBClient(credentials);
		dynamoDB = new AmazonDynamoDBClient(credentials);
		this.reportsTable = reportsTable;
	}

	public DynamoDBStorage(AmazonDynamoDB dynamoDB, String reportsTable) {
		this.dynamoDB = dynamoDB;
		this.reportsTable = reportsTable;
	}

	@Override
	protected String loadReport(ReportsConfig reportsConfig, String reportId) throws Exception {
		GetItemResult itemResult = dynamoDB.getItem(this.reportsTable, new HashMap<String, AttributeValue>() {{ put("reportId", StringToStringMarshaller.instance().marshall(reportId)); }});
		Map<String, AttributeValue> item = itemResult.getItem();

		if (item != null && reportId.equals(item.get("reportId").getS())) {
			String reportYaml = item.get("reportYaml").getS();
			LOG.debug("Got report " + reportId);
			if (this.loadReport(reportsConfig, new StringInputStream(reportYaml), reportId)) {
				return reportId;
			} else {
				LOG.debug("Report didn't load");
			}
		} else {
			LOG.debug("Report wasn't found");
		}

		return null;
	}

	@Override
	protected String[] getReportList() {
		return new String[0];
	} // Not necessary

	@Override
	protected String reportToName(String reportId) {
		return reportId; // Not necessary
	}

	@Override
	public ReportsConfig getReportsConfig() {
		return super.getReportsConfig(); // Not necessary
	}

	@Override
	public ReportsConfig getReportsConfig(Collection<String> restrictToNamed) {
		ReportsConfig reportsConfig = new ReportsConfig();
		if (restrictToNamed == null) {
			return reportsConfig;
		}
		for (String reportId : restrictToNamed) {
			try {
				loadReport(reportsConfig, reportId);
			} catch (Exception e) {
				LOG.warn("Couldn't load report " + reportId);
				e.printStackTrace();
			}
		}
		return reportsConfig;
	}
}
