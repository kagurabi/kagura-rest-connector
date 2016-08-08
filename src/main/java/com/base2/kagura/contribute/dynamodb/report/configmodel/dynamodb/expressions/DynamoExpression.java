package com.base2.kagura.contribute.dynamodb.report.configmodel.dynamodb.expressions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by arran on 1/08/2016.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, defaultImpl=StringExpression.class, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
	@JsonSubTypes.Type(value=AndExpression.class, name = "and"),
	@JsonSubTypes.Type(value=OrExpression.class, name="or"),
	@JsonSubTypes.Type(value=StringExpression.class, name="expression")
})
public abstract class DynamoExpression {
}
