package com.base2.kagura.contribute.rest.report.connector.pathtools;

import java.io.InputStream;
import java.util.List;

public interface Engine {
    boolean Match(String data, String errorPath);

    List<Object> GetArray(String data, String rowVariablePath);

    Object GetPath(Object data, String path);
    Object GetPath(String data, String path);
}
