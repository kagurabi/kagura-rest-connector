package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by arran on 1/08/2016.
 */
@JsonTypeName("and")
public class AndExpression extends ConditionalExpression {
	private static final Logger LOG = LoggerFactory.getLogger(OrExpression.class);

	@JsonCreator
	public AndExpression(ArrayList<DynamoExpression> expressions) {
		super(expressions);
	}
}
