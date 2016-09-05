package com.base2.kagura.contribute.dynamodb.report.connector;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import com.amazonaws.services.dynamodbv2.model.*;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfig;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfigTests;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.connectors.ReportConnector;
import com.base2.kagura.core.report.parameterTypes.ParamConfig;
import com.base2.kagura.core.report.parameterTypes.SingleParamConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.hamcrest.collection.IsIn;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.object.IsCompatibleType;
import org.junit.*;
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
		AmazonDynamoDB dynamoDB = setupDB();
		ListTablesResult tables = dynamoDB.listTables();

		LOG.info("Listing table names");

		for (String each : tables.getTableNames()) {
			LOG.info(each);
		}
		Assert.assertThat(tables.getTableNames(), IsCollectionContaining.hasItem("Movies"));
	}

	private AmazonDynamoDB setupDB() {
		System.setProperty("sqlite4java.library.path", "native-libs");
		AmazonDynamoDBLocal dynamoDBserver = DynamoDBEmbedded.create();
		AmazonDynamoDB amazonDynamoDB = dynamoDBserver.amazonDynamoDB();
		amazonDynamoDB.createTable(new CreateTableRequest()
			.withTableName("Movies")
			.withKeySchema(new KeySchemaElement("year", "HASH"))
			.withKeySchema(new KeySchemaElement("title", "RANGE"))
			.withAttributeDefinitions(new AttributeDefinition("year", "N"))
			.withAttributeDefinitions(new AttributeDefinition("title", "S"))
			.withProvisionedThroughput(new ProvisionedThroughput(10l,10l))
		);
		return amazonDynamoDB;
	}

	@Test
	public void ReportConnectorConvertTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ReportConfig config = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), ReportConfig.class);
		Assert.assertThat(config.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbReportConfig.class));
		((DynamoDbReportConfig)config).setRegion("");
		ReportConnector reportConnector = config.getReportConnector();
		Assert.assertThat(reportConnector.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbConnector.class));
	}

	@Test
	public void ReportConnectorQueryToStringTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ReportConfig config = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), ReportConfig.class);
		Assert.assertThat(config.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbReportConfig.class));
		Assert.assertThat(((DynamoDbReportConfig)config).getDynamo().getQuery().getConditions().toString(), IsIn.isIn(Arrays.asList(
			"#nameYear = :valueYear and title between :valueStartLetter and :valueEndLetter",
			"#nameYear = :valueYear or (#nameYear = :valueYear and title between :valueStartLetter and :valueEndLetter)"
		)));
	}
	@Test
	public void ReportConnectorConnectTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ReportConfig configRaw = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), ReportConfig.class);
		Assert.assertThat(configRaw.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbReportConfig.class));
		DynamoDbReportConfig config = (DynamoDbReportConfig)configRaw;

		AmazonDynamoDB dynamoDB = setupDB();

		config.setRegion("");
		config.setEndpoint("");

		ReportConnector reportConnector = config.getReportConnector(dynamoDB);

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
