package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb;

import com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions.DynamoExpression;

import java.util.*;

/**
 * Created by arran on 1/08/2016.
 */
public class DynamoQuery {
	String table;
	ArrayList<String> projection;
	ArrayList<DynamoExpression> conditions;
	Map<String, String> names;
	Map<String, Object> values;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public ArrayList<String> getProjection() {
		return projection;
	}

	public void setProjection(ArrayList<String> projection) {
		this.projection = projection;
	}

	public ArrayList<DynamoExpression> getConditions() {
		return conditions;
	}

	public void setConditions(ArrayList<DynamoExpression> conditions) {
		this.conditions = conditions;
	}

	public Map<String, String> getNames() {
		return names;
	}

	public void setNames(Map<String, String> names) {
		this.names = names;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
}
