package com.base2.kagura.contribute.dynamodb.storage;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import com.amazonaws.services.dynamodbv2.model.*;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfigTests;
import com.base2.kagura.contribute.dynamodb.report.connector.MovieSampleDataObject;
import com.base2.kagura.core.report.configmodel.ReportsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by arran on 10/10/2016.
 */
public class DynamoDBStorageTests {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDbReportConfigTests.class);

	private AmazonDynamoDB setupDB() throws IOException {
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
		amazonDynamoDB.createTable(new CreateTableRequest()
			.withTableName("reports")
			.withKeySchema(new KeySchemaElement("reportId", "HASH"))
			.withAttributeDefinitions(new AttributeDefinition("reportId", "S"))
//			.withAttributeDefinitions(new AttributeDefinition("reportYaml", "S"))
			.withProvisionedThroughput(new ProvisionedThroughput(10l,10l))
		);
		ObjectMapper objectMapper = new ObjectMapper();
		List<MovieSampleDataObject> movies = Arrays.asList(objectMapper.readValue(this.getClass().getResourceAsStream("/moviedata.json"), MovieSampleDataObject[].class));
		List<WriteRequest> items = new ArrayList<>();
		for (MovieSampleDataObject movie : movies) {
//			LOG.debug("Movie: " + movie.getTitle() + " " + movie.getYear());
			amazonDynamoDB.putItem("Movies", new HashMap<String, AttributeValue>(){{
				put("year", new AttributeValue().withN(""+movie.getYear()));
				put("title", new AttributeValue().withS(movie.getTitle()));
				put("info", new AttributeValue().withM(new HashMap<>()));
			}});
		}
		List<ReportSampleDataObject> reports = Arrays.asList(objectMapper.readValue(this.getClass().getResourceAsStream("/reports.json"), ReportSampleDataObject[].class));
		items = new ArrayList<>();
		for (ReportSampleDataObject report : reports) {
//			LOG.debug("Movie: " + movie.getTitle() + " " + movie.getYear());
			amazonDynamoDB.putItem("reports", new HashMap<String, AttributeValue>(){{
				put("reportId", new AttributeValue().withS(report.getReportId()));
				put("reportYaml", new AttributeValue().withS(report.getReportYaml()));
			}});
		}
		return amazonDynamoDB;
	}

	@Test
	public void ListTablesTest() throws IOException {
		AmazonDynamoDB dynamoDB = setupDB();
		ListTablesResult tables = dynamoDB.listTables();

		LOG.info("Listing table names");

		for (String each : tables.getTableNames()) {
			LOG.info(each);
		}
		Assert.assertThat(tables.getTableNames(), IsCollectionContaining.hasItem("Movies"));
		Assert.assertThat(tables.getTableNames(), IsCollectionContaining.hasItem("reports"));
	}

	@Test
	public void LoadReportTest1() throws Exception {
		LOG.info("Loading report test1");
		AmazonDynamoDB dynamoDB = setupDB();
		DynamoDBStorage dynamoDBStorage = new DynamoDBStorage(dynamoDB, "reports");
		ReportsConfig reportsConfig = new ReportsConfig();
		String reportId = dynamoDBStorage.loadReport(reportsConfig, "test1");
		Assert.assertThat(reportId, IsNull.notNullValue());
		Assert.assertThat(reportsConfig.getReports().entrySet(), IsCollectionWithSize.hasSize(1));
		Assert.assertThat(reportsConfig.getReport(reportId), IsNull.notNullValue());
		Assert.assertThat(reportsConfig.getReport(reportId).getReportType(), IsEqual.equalTo("DynamoDB"));
	}

	@Test()
	public void LoadReportTestFail() throws Exception {
		LOG.info("Loading report testfail");
		AmazonDynamoDB dynamoDB = setupDB();
		DynamoDBStorage dynamoDBStorage = new DynamoDBStorage(dynamoDB, "reports");
		ReportsConfig reportsConfig = new ReportsConfig();
		String reportId = dynamoDBStorage.loadReport(reportsConfig, "testfail");
		Assert.assertThat(reportId, IsNull.nullValue());
	}


}
