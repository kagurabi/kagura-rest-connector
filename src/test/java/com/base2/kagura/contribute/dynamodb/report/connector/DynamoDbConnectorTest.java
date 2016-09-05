package com.base2.kagura.contribute.dynamodb.report.connector;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfig;
import com.base2.kagura.contribute.dynamodb.report.configmodel.DynamoDbReportConfigTests;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.connectors.ReportConnector;
import com.base2.kagura.core.report.parameterTypes.ParamConfig;
import com.base2.kagura.core.report.parameterTypes.SingleParamConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.Range;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIn;
import org.hamcrest.core.*;
import org.hamcrest.object.IsCompatibleType;
import org.junit.*;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.CharacterIterator;
import java.util.*;

/**
 * Created by arran on 8/08/2016.
 */
public class DynamoDbConnectorTest {
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

		reportConnector.setPage(0);
		for (ParamConfig each : reportConnector.getParameterConfig()) {
			if ("paramStartLetter".equals(each.getId())) {
				if (each instanceof SingleParamConfig) {
					LOG.debug("Set value on " + each.getName());
					((SingleParamConfig) each).setValue("C");
				}
			}
		}
		reportConnector.run(new HashMap<String, Object>());
		List<Map<String, Object>> rows = reportConnector.getRows();
		Assert.assertThat(rows, IsNot.not(IsNull.<List<Map<String,Object>>>nullValue()));
		LOG.info("Listing results:");
		int i = 0;
		Assert.assertThat(rows, Matchers.hasSize(Matchers.greaterThan(1)));
		for (Map<String, Object> each : rows) {
			i++;
			LOG.info(i + ". " + each.get("title"));
			Assert.assertThat(each.get("year"), Is.is(new BigDecimal(2001)));
			Assert.assertThat(new Character(((String)each.get("title")).charAt(0)), new BaseMatcher<Character>() {
				@Override
				public void describeTo(Description description) {
					description.appendText("between C and S ");
				}

				@Override
				public boolean matches(Object item) {
					if (item instanceof Character) {
						return Range.between('C', 'S').contains((Character)item);
					}
					throw new InvalidUseOfMatchersException("Item is not a character");
				}
			});
		}
	}

}
