package com.base2.kagura.contribute.dynamodb.report.configmodel;

import bsh.EvalError;
import bsh.Interpreter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by arran on 15/08/2016.
 */
public class ScriptingTest {
	private static final Logger LOG = LoggerFactory.getLogger(DynamoDbReportConfigTests.class);
	@Test
	public void testScript() throws EvalError {
		Interpreter bsh = new Interpreter();
		LOG.debug(""+bsh.eval("1"));
	}
	@Test
	public void testCondScript() throws EvalError {
		Interpreter bsh = new Interpreter();
		LOG.debug(""+bsh.eval("if (null!=null) return 1; else return 0;"));
	}
	@Test
	public void testTriCondScript() throws EvalError {
		Interpreter bsh = new Interpreter();
		LOG.debug(""+bsh.eval("return null!=null ? 1 : 0"));
	}
	@Test
	public void testSingleQuoteStringScript() throws EvalError {
		Interpreter bsh = new Interpreter();
		LOG.debug(""+bsh.eval("return \"asdf\""));
	}
}
