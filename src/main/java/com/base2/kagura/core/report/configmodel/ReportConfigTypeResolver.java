package com.base2.kagura.core.report.configmodel;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Created by arran on 16/06/2016.
 */
public class ReportConfigTypeResolver implements TypeIdResolver {
	private JavaType mBaseType;

//	private static Map<String, Class> typeMap = new HashMap<String, Class>();
//	static {
//		AddReportConfigType("JDBC", JDBCReportConfig.class);
//		AddReportConfigType("JNDI", JNDIReportConfig.class);
//		AddReportConfigType("Groovy".toUpperCase(), GroovyReportConfig.class);
//		AddReportConfigType("Fake".toUpperCase(), FakeReportConfig.class);
//	}
//
//	public static Class AddReportConfigType(String name, Class clazz) {
//		if (typeMap.containsKey(name.toUpperCase())) {
//			throw new RuntimeException("Class already loaded: " + name);
//		}
//		return typeMap.put(name.toUpperCase(), clazz);
//	}

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
//		String name = clazz.getName();
//		for (Map.Entry<String, Class> each : typeMap.entrySet()) {
//			String eachName = each.getValue().getName();
//			if (eachName.equals(name)) {
//				return each.getKey();
//			}
//		}
	 	return clazz.getSimpleName().replace("ReportConfig", "");
//		throw new IllegalStateException("class " + clazz + " is not in ReportConfigTypeResolver ");
	}

	@Override
	public JavaType typeFromId(String type)
	{
//		if (typeMap.containsKey(type.toUpperCase())) {
//			return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, typeMap.get(type.toUpperCase()));
//		}
		Class result = ReportConfigTypeService.getInstance().getType(type);
		if (result != null) {
			return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, result);
		}

		throw new IllegalStateException("cannot find Report Config type '" + type + "'");
	}
}
