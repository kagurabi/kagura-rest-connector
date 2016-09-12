package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions;

import bsh.EvalError;
import bsh.Interpreter;
import com.fasterxml.jackson.annotation.*;

/**
 * Created by arran on 1/08/2016.
 */
@JsonTypeName("expression")
public class StringExpression extends DynamoExpression {
	String expression;

	public StringExpression() {
	}

//	@JsonCreator
	public StringExpression(String expression) {
		this.expression = expression;
	}

	@JsonValue
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String Eval(Interpreter bsh) throws EvalError {
		return expression;
	}
}
