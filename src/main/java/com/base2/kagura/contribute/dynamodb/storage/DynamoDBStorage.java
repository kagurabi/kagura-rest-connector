package com.base2.kagura.contribute.dynamodb.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
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

		NameMap nameMap = new NameMap();
		ValueMap valueMap = new ValueMap();

		nameMap.put("reportName", "reportName");
		nameMap.put("valueMap", "valueMap");

		valueMap.put("reportName", report.getName());

		QuerySpec spec = new QuerySpec()
			.withProjectionExpression("#reportName,#valueMap")
			.withKeyConditionExpression("#reportName = :reportName")
			.withValueMap(valueMap)
			.withNameMap(nameMap);

		AmazonDynamoDB client = new AmazonDynamoDBClient(credentials);

		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(this.reportsTable);

		table.getItem()

		ItemCollection<QueryOutcome> results = table.query(spec.withMaxPageSize(10));
		int i = 0;
		for (Page<Item, QueryOutcome> page : results.pages()) {
			LOG.debug("Page: " + i);
			LOG.debug("Page size: " + page.size());
			if (!page.hasNextPage()) {
				break;
			}
			LOG.debug("Getting items for page");
			for (Item each : page) {
				String reportName = each.getString("reportName");
				String reportYaml = each.getString("reportYaml");
				LOG.debug("Got report " + reportName);
				if (this.loadReport(reportsConfig, new StringInputStream(reportYaml), reportName)) {
					return reportName;
				} else {
					LOG.debug("Report didn't load");
				}
			}
			i++;
		}

		return null;
	}

	@Override
	protected PrimaryKey[] getReportList() {
		return new PrimaryKey[0];
	}

	@Override
	protected String reportToName(PrimaryKey report) {
		if (report == null) return "<null>";
		return ;
	}

	@Override
	public ReportsConfig getReportsConfig() {
		return super.getReportsConfig();
	}

	@Override
	public ReportsConfig getReportsConfig(Collection<String> restrictToNamed) {
		return super.getReportsConfig(restrictToNamed);
	}
}
