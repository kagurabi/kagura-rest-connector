package com.base2.kagura.contribute.dynamodb.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.marshallers.StringToStringMarshaller;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.util.StringInputStream;
import com.base2.kagura.core.KaguraUtil;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.ReportProvider;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by arran on 7/10/2016.
 */
public class DynamoDBStorage implements ReportProvider {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDBStorage.class);
	private List<String> errors;

	String reportsTable;
	private AmazonDynamoDB dynamoDB;

	public DynamoDBStorage(AWSCredentials credentials, String region, String reportsTable) {
		AmazonDynamoDB client = new AmazonDynamoDBClient(credentials);
		dynamoDB = new AmazonDynamoDBClient(credentials);
		this.reportsTable = reportsTable;
		dynamoDB.setRegion(RegionUtils.getRegion(region));
	}

	public DynamoDBStorage(AmazonDynamoDB dynamoDB, String reportsTable) {
		this.dynamoDB = dynamoDB;
		this.reportsTable = reportsTable;
	}

	public ReportConfig LoadReport(String reportId) throws IOException {
		GetItemResult itemResult = dynamoDB.getItem(this.reportsTable, new HashMap<String, AttributeValue>() {{ put("reportId", StringToStringMarshaller.instance().marshall(reportId)); }});
		Map<String, AttributeValue> item = itemResult.getItem();
		resetErrors();
		if (item != null && reportId.equals(item.get("reportId").getS())) {
			String reportYaml = item.get("reportYaml").getS();
			LOG.debug("Got report " + reportId);
			ReportConfig report = null;
			try {
				report = KaguraUtil.LoadYaml(new StringInputStream(reportYaml), reportId);
			} catch (InvalidTypeIdException e) {
				getErrors().add("Error with type: " + e.getTypeId());
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			if (report != null) {
				return report;
			} else {
				String errors = "unknown error";
				getErrors().add(errors);
			}
		} else {
			LOG.debug("Report wasn't found");
		}

		return null;
	}

	public List<String> getErrors() {
		return errors;
	}

	@Override
	public void resetErrors() {
		this.errors = new ArrayList<>();
	}
}
