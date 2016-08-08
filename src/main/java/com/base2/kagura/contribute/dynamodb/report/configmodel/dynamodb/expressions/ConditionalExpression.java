package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arran on 1/08/2016.
 */
public class ConditionalExpression extends DynamoExpression {
	protected ArrayList<DynamoExpression> expressions;
	String condition;

	protected ConditionalExpression(String condition, ArrayList<DynamoExpression> expressions) {
		this.expressions = expressions;
		this.condition = condition;
	}

	protected ConditionalExpression(String condition) {
		this.condition = condition;
	}

	public ArrayList<DynamoExpression> getExpressions() {
		return expressions;
	}

	public void setExpressions(ArrayList<DynamoExpression> expressions) {
		this.expressions = expressions;
	}

	@Override
	public String toString() {
		if (expressions == null) return "";
		List<String> values = new ArrayList<String>(expressions.size());
		for (DynamoExpression expression : expressions) {
			if (expression == null) continue;
			String value = expression.toString();
			if (StringUtils.isBlank(value)) continue;
			if (expression instanceof ConditionalExpression) {
				value = "(" + value + ")";
			}
			values.add(value);
		}
		if (values.size() == 1 && values.get(0).startsWith("(") && values.get(0).endsWith(")")) {
			String value = values.get(0);
			value = value.substring(1,value.length()-1);
			values.set(0, value);
		}
		return StringUtils.join(values," " + condition + " ");
	}
}
