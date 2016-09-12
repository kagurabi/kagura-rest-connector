package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.InterpreterError;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by arran on 1/08/2016.
 */
@JsonTypeName("beanExpression")
public class BeanExpression extends DynamoExpression {
	String beanExpression;

	public BeanExpression() {
	}

//	@JsonCreator
	public BeanExpression(String beanExpression) {
		this.beanExpression = beanExpression;
	}

	@JsonValue
	public String getBeanExpression() {
		return beanExpression;
	}

	public void setBeanExpression(String beanExpression) {
		this.beanExpression = beanExpression;
	}

	@Override
	public String Eval(Interpreter bsh) throws EvalError {
		Object value = bsh.eval(beanExpression);
		if (value == null) return "";
		return String.valueOf(value);
	}
}
