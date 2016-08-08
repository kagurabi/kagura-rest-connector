package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;

/**
 * Created by arran on 1/08/2016.
 */
public class ConditionalExpression extends DynamoExpression {
	protected ArrayList<DynamoExpression> expressions;

	protected ConditionalExpression(ArrayList<DynamoExpression> expressions) {
		this.expressions = expressions;
	}

	protected ConditionalExpression() {
	}

	public ArrayList<DynamoExpression> getExpressions() {
		return expressions;
	}

	public void setExpressions(ArrayList<DynamoExpression> expressions) {
		this.expressions = expressions;
	}
}
