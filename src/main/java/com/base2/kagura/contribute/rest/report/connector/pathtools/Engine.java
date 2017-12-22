package com.base2.kagura.contribute.rest.report.connector.pathtools;

import com.base2.kagura.contribute.rest.report.configmodel.restmodel.ColumnSelect;

import java.util.List;
import java.util.Map;

public interface Engine {
    boolean Match(String data, String errorPath);

    List<Object> GetRowVars(String data);

    String GetPath(String path);
}
