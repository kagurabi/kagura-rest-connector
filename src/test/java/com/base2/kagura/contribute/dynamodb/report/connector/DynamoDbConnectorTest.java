package com.base2.kagura.contribute.dynamodb.report.connector;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AWSSessionCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfig;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfigTests;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.connectors.ReportConnector;
import com.base2.kagura.core.report.parameterTypes.ParamConfig;
import com.base2.kagura.core.report.parameterTypes.SingleParamConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.deploy.util.SessionState;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsArrayContaining;
import org.hamcrest.collection.IsIn;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.object.IsCompatibleType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by arran on 8/08/2016.
 */
public class DynamoDbConnectorTest {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDbReportConfigTests.class);

	@Test
	public void ListTablesTest() throws IOException {
		AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient();
		dynamoDB.setSignerRegionOverride("us-west-2");
		dynamoDB.setEndpoint("http://localhost:8000/");
		ListTablesResult tables = dynamoDB.listTables();

		LOG.info("Listing table names");

		for (String each : tables.getTableNames()) {
			LOG.info(each);
		}
		Assert.assertThat(tables.getTableNames(), IsCollectionContaining.hasItem("Movies"));
	}
	@Test
	public void ReportConnectorConvertTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ReportConfig config = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), ReportConfig.class);
		Assert.assertThat(config.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbReportConfig.class));
		ReportConnector reportConnector = config.getReportConnector();
		Assert.assertThat(reportConnector.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbConnector.class));
	}

	@Test
	public void ReportConnectorQueryToStringTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ReportConfig config = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), ReportConfig.class);
		Assert.assertThat(config.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbReportConfig.class));
		ReportConnector reportConnector = config.getReportConnector();
		Assert.assertThat(((DynamoDbReportConfig)config).getDynamo().getQuery().getConditions().toString(), IsIn.isIn(Arrays.asList(
			"#nameYear = :valueYear and title between :valueStartLetter and :valueEndLetter",
			"#nameYear = :valueYear or (#nameYear = :valueYear and title between :valueStartLetter and :valueEndLetter)"
		)));
	}
	@Test
	public void ReportConnectorConnectTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ReportConfig config = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), ReportConfig.class);
		Assert.assertThat(config.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbReportConfig.class));
		ReportConnector reportConnector = config.getReportConnector();
		reportConnector.setPage(1);
		for (ParamConfig each : reportConnector.getParameterConfig()) {
			if ("paramStartLetter".equals(each.getId())) {
				if (each instanceof SingleParamConfig) {
					LOG.debug("Set value on " + each.getName());
					((SingleParamConfig) each).setValue("C");
				}
			}
		}
		reportConnector.run(new HashMap<String, Object>());
		Assert.assertThat(reportConnector.getRows(), IsNot.not(IsNull.<List<Map<String,Object>>>nullValue()));
		LOG.info("Listing results:");
		int i = 0;
		for (Map<String, Object> each : reportConnector.getRows()) {
			i++;
			LOG.info(i + ". " + each.get("title"));
		}
	}

}
