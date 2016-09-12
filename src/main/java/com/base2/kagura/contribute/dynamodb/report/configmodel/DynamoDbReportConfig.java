package com.base2.kagura.contribute.dynamodb.report.configmodel;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.DynamoQueryContainer;
import com.base2.kagura.contribute.dynamodb.report.connector.DynamoDbConnector;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.connectors.ReportConnector;

/**
 * Created by arran on 16/06/2016.
 */
public class DynamoDbReportConfig extends ReportConfig {
	String awsAccessKeyId;
	String awsSecretAccessKey;
	String region;
	String action;
	String endpoint;
	String beanScript;
	DynamoQueryContainer dynamo;

	@Override
	public ReportConnector getReportConnector() {
		return new DynamoDbConnector(this);
	}

	public ReportConnector getReportConnector(AmazonDynamoDB client) {
		return new DynamoDbConnector(this, client);
	}

	@Override
	public String getReportType() {
		return "DynamoDB";
	}

	public String getAwsAccessKeyId() {
		return awsAccessKeyId;
	}

	public void setAwsAccessKeyId(String awsAccessKeyId) {
		this.awsAccessKeyId = awsAccessKeyId;
	}

	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}

	public void setAwsSecretAccessKey(String awsSecretAccessKey) {
		this.awsSecretAccessKey = awsSecretAccessKey;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public DynamoQueryContainer getDynamo() {
		return dynamo;
	}

	public void setDynamo(DynamoQueryContainer dynamo) {
		this.dynamo = dynamo;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getBeanScript() {
		return beanScript;
	}

	public void setBeanScript(String beanScript) {
		this.beanScript = beanScript;
	}
}
