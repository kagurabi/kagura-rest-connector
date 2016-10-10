package com.base2.kagura.contribute.dynamodb.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.util.StringInputStream;
import com.base2.kagura.core.report.configmodel.ReportsConfig;
import com.base2.kagura.core.storage.ReportsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by arran on 7/10/2016.
 */
public class DynamoDBStorage extends ReportsProvider<PrimaryKey> {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDBStorage.class);

	AWSCredentials credentials;
	String reportsTable;

	public DynamoDBStorage(AWSCredentials credentials, String reportsTable) {
		this.credentials = credentials;
		this.reportsTable = reportsTable;
	}

	@Override
	protected String loadReport(ReportsConfig reportsConfig, PrimaryKey report) throws Exception {
		AmazonDynamoDB client = new AmazonDynamoDBClient(credentials);

		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(this.reportsTable);

		Item item = table.getItem(report);

		if (item != null) {
			String reportId = item.getString("reportId");
			String reportYaml = item.getString("reportYaml");
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
	protected PrimaryKey[] getReportList() {
		return new PrimaryKey[0];
	} // Not necessary

	@Override
	protected String reportToName(PrimaryKey report) {
		return (String)report.getComponents().iterator().next().getValue(); // Not necessary
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
			PrimaryKey primaryKey = new PrimaryKey("reportId", reportId);
			try {
				loadReport(reportsConfig, primaryKey);
			} catch (Exception e) {
				LOG.warn("Couldn't load report " + reportId);
				e.printStackTrace();
			}
		}
		return reportsConfig;
	}
}
