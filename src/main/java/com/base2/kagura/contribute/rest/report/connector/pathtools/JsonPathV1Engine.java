package com.base2.kagura.contribute.rest.report.connector.pathtools;

import com.jayway.jsonpath.JsonPath;

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
        // Not used, technically a match as it would be a NULL value.
//        if (o == null) {
//            return false;
//        }
        if (o instanceof List) {
            return ((List)o).size() != 0;
        }
        return true;
    }

    @Override
    public List<Object> GetRowVars(String data) {
        return null;
    }

    @Override
    public String GetPath(String path) {
        return null;
    }
}
