package com.base2.kagura.contribute.rest.report.connector.pathtools;

import com.jayway.jsonpath.JsonPath;

import java.io.InputStream;
import java.util.List;

public class JsonPathV1Engine implements Engine {
    public JsonPathV1Engine() {
    }

    @Override
    public boolean Match(String data, String errorPath) {
        Object o = null;
        try {
            o = JsonPath.read(data, errorPath);
        } catch (com.jayway.jsonpath.PathNotFoundException ex) {
            return false;
        }
        if (o instanceof List) {
            return ((List)o).size() != 0;
        }
        return true;
    }

    @Override
    public List<Object> GetArray(String data, String rowVariablePath) {
        try {
            return JsonPath.read(data, rowVariablePath);
        } catch (com.jayway.jsonpath.PathNotFoundException ex) {
            return null;
        }
    }

    @Override
    public Object GetPath(String data, String path) {
        try {
            return JsonPath.read(data, path);
        } catch (com.jayway.jsonpath.PathNotFoundException ex) {
            return null;
        }
    }
    @Override
    public Object GetPath(Object data, String path) {
        try {
            return JsonPath.read(data, path);
        } catch (com.jayway.jsonpath.PathNotFoundException ex) {
            return null;
        }
    }
}
