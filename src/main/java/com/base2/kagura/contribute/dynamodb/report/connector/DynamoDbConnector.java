package com.base2.kagura.contribute.dynamodb.report.connector;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfig;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.connectors.ReportConnector;

import java.util.List;
import java.util.Map;

/**
 * Created by arran on 16/06/2016.
 */
public class DynamoDbConnector extends ReportConnector {
	AmazonDynamoDBClient client;
	String action;
	private List<Map<String, Object>> rows;
	/**
	 * Does a shallow copy of the necessary reportConfig values. Initializes the error structure.
	 *
	 * @param reportConfig
	 */
	public DynamoDbConnector(DynamoDbReportConfig reportConfig) {
		super(reportConfig);
		client = new AmazonDynamoDBClient(new BasicAWSCredentials(reportConfig.getAwsAccessKeyId(), reportConfig.getAwsSecretAccessKey()));
		client.setRegion(RegionUtils.getRegion(reportConfig.getRegion()));
		this.action = reportConfig.getAction();
	}

	@Override
	public List<Map<String, Object>> getRows() {
		return rows;
	}

	protected void scan(Map<String, Object> extra) {
//		client.scan();
	}

	protected void query(Map<String, Object> extra) {
//		client.query();
	}

	@Override
	protected void runReport(Map<String, Object> extra) {
//		switch (this.action) {
//			case "scan":
//				scan(extra);
//				break;
//			case "query":
//			default:
//				query(extra);
//				break;
//		}
	}
}
