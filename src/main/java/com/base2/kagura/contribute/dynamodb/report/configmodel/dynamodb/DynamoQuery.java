package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb;

import bsh.EvalError;
import bsh.Interpreter;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions.DynamoExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by arran on 1/08/2016.
 */
public class DynamoQuery {
	String table;
	ArrayList<String> projection;
	DynamoExpression conditions;
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

	public DynamoExpression getConditions() {
		return conditions;
	}

	public void setConditions(DynamoExpression conditions) {
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

	public ScanSpec toScanSpec(Interpreter bsh) throws EvalError {
		ScanSpec spec = new ScanSpec();
		if (this.getProjection() != null) {
			spec = spec.withProjectionExpression(StringUtils.join(this.getProjection(), ", "));
		}
		if (this.getConditions() != null) {
			String value = this.getConditions().Eval(bsh);
			if (StringUtils.isNotBlank(value)) {
				spec = spec.withFilterExpression(value);
			}
		}
		return spec;

	}

	public QuerySpec toQuerySpec(Interpreter bsh) throws EvalError {
		QuerySpec spec = new QuerySpec();
		if (this.getProjection() != null) {
			spec = spec.withProjectionExpression(StringUtils.join(this.getProjection(), ", "));
		}
		if (this.getConditions() != null) {
			String value = this.getConditions().Eval(bsh);
			if (StringUtils.isNotBlank(value)) {
				spec = spec.withKeyConditionExpression(value);
			}
		}
		return spec;
	}
}
