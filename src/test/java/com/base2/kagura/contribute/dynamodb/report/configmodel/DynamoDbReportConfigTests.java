package com.base2.kagura.contribute.dynamodb.report.configmodel;

import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.hamcrest.object.IsCompatibleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by arran on 1/08/2016.
 */
public class DynamoDbReportConfigTests {

	private static final Logger LOG = LoggerFactory.getLogger(DynamoDbReportConfigTests.class);

	@Test
	public void SyntaxTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ReportConfig config = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), ReportConfig.class);
//		DynamoDbReportConfig config = objectMapper.readValue(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), DynamoDbReportConfig.class);
		Assert.assertThat(config.getClass(), IsCompatibleType.typeCompatibleWith(DynamoDbReportConfig.class));
		LOG.debug(objectMapper.writeValueAsString(config));
	}

	@Test
	@Ignore
	public void shouldWork() throws Exception {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream("/reports/syntaxTest/reportConfig.yaml"), "UTF-8");
		LOG.warn("Begin ouput");
		LOG.warn(xml);
		LOG.warn("End ouput");
	}

}
