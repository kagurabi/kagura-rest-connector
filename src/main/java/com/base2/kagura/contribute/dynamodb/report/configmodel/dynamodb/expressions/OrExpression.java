package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by arran on 1/08/2016.
 */
@JsonTypeName("or")
public class OrExpression extends ConditionalExpression {
	private static final Logger LOG = LoggerFactory.getLogger(OrExpression.class);

	@JsonCreator
	public OrExpression(ArrayList<DynamoExpression> expressions) {
		super("or", expressions);
	}
}
