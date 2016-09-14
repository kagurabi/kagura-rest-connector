package com.base2.kagura.core.report.configmodel;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Map;

/**
 * Created by arran on 16/06/2016.
 */
public class ReportConfigTypeResolver implements TypeIdResolver {
	private JavaType mBaseType;

	private Map<String, Class> typeMap = ReportConfigTypeService.getInstance().getMap();

	@Override
	public void init(JavaType baseType)
	{
		mBaseType = baseType;
	}

	@Override
	public JsonTypeInfo.Id getMechanism()
	{
		return JsonTypeInfo.Id.CUSTOM;
	}

	@Override
	public String idFromValue(Object obj)
	{
		return idFromValueAndType(obj, obj.getClass());
	}

	@Override
	public String idFromBaseType()
	{
		return idFromValueAndType(null, mBaseType.getRawClass());
	}

	@Override
	public String idFromValueAndType(Object obj, Class<?> clazz)
	{
		String name = clazz.getName();
		for (Map.Entry<String, Class> each : typeMap.entrySet()) {
			String eachName = each.getValue().getName();
			if (eachName.equals(name)) {
				return each.getKey();
			}
		}
		throw new IllegalStateException("class " + clazz + " is not in ReportConfigTypeResolver ");
	}

	@Override
	public JavaType typeFromId(String type)
	{
		if (typeMap.containsKey(type.toUpperCase())) {
			return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, typeMap.get(type.toUpperCase()));
		}

		throw new IllegalStateException("cannot find Report Config type '" + type + "'");
	}

	@Override
	public JavaType typeFromId(DatabindContext context, String id) {
		if (typeMap.containsKey(id.toUpperCase())) {
			return context.constructSpecializedType(mBaseType, typeMap.get(id.toUpperCase()));
		}

		throw new IllegalStateException("cannot find Report Config type '" + id + "'");
	}

	@Override
	public String getDescForKnownTypeIds() {
		return "";
	}
}
