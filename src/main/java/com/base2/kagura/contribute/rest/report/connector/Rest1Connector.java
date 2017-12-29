package com.base2.kagura.contribute.rest.report.connector;

import com.base2.kagura.contribute.rest.report.configmodel.Rest1ReportConfig;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.ColumnSelect;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.ErrorDetection;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.RestConfig;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.RowFilter;
import com.base2.kagura.contribute.rest.report.connector.pathtools.Engine;
import com.base2.kagura.contribute.rest.report.connector.pathtools.JsonPathV1Engine;
import com.base2.kagura.core.report.connectors.ReportConnector;
import com.mashape.unirest.http.Unirest;
import net.minidev.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rest1Connector extends ReportConnector {
    private List<Map<String, Object>> rows;
    private RestConfig restConfig;
    /**
     * Does a shallow copy of the necessary rest1ReportConfig values. Initializes the error structure.
     *
     * @param rest1ReportConfig
     */
    protected Rest1Connector(Rest1ReportConfig rest1ReportConfig) {
        super(rest1ReportConfig);
        this.restConfig = rest1ReportConfig.getConfig();
    }

    @Override
    public List<Map<String, Object>> getRows() {
        return rows;
    }

    @Override
    protected void runReport(Map<String, Object> extra) {
        Engine engine = null;
        this.rows = null;
        switch (restConfig.getPathTool()) {
            case "json1":
                engine = new JsonPathV1Engine();
                break;
            default:
                getErrors().add("Unknown path tool");
                return;
        }
        String data = null;
        try {
            data = getData(restConfig.getEndPointUrl(), restConfig.getEndPointMethod(), restConfig.getEndPointData());
        } catch (Exception e) {
            getErrors().add(e.getMessage());
            return;
        }

        if (restConfig.getErrorDetection() != null) {
            for (ErrorDetection errorDetection : restConfig.getErrorDetection()) {
                if (engine.Match(data, errorDetection.getErrorPath())) {
                    getErrors().add(errorDetection.getErrorMessage());
                }
            }
        }

        if (this.errors != null && this.errors.size() > 0) {
            return;
        }

        List<Object> rowVars = engine.GetArray(data, this.restConfig.getRowVariablePath());

        // TODO offset
        // TODO pagination

        this.rows = new ArrayList<>();
        for (Object o : rowVars) {
            boolean skip = false;
            if (restConfig.getFilters() != null) {
                for (RowFilter filter : restConfig.getFilters()) {
                    Object result;// = engine.GetPath(data, column.getPath());
                    String type = "";
                    if (filter.getType() != null) {
                        type = filter.getType().toLowerCase();
                    }
                    switch (type) {
                        case "root":
                            result = engine.GetPath(data, filter.getPath());
                            break;
                        case "row":
                        case "":
                        default:
                            result = engine.GetPath(o, filter.getPath());
                    }
                    if (result == null) {
                        continue;
                    }
                    if ("".equals(result)) {
                        continue;
                    }
                    if (result instanceof JSONArray && ((JSONArray)result).size() >= 1) {
                        continue;
                    }
                    if (StringUtils.isBlank(filter.getMatchRule())) {
                        skip = true;
                        break;
                    } else {
                        // TODO add scripting here
                    }
                }
            }
            if (skip) continue;
            Map<String, Object> row = new HashMap<>();
            for (ColumnSelect column : restConfig.getSelects()) {
                Object result;// = engine.GetPath(data, column.getPath());
                String type = "";
                if (column.getType() != null) {
                    type = column.getType().toLowerCase();
                }
                switch (type) {
                    case "root":
                        result = engine.GetPath(data, column.getPath());
                        break;
                    case "row":
                    case "":
                    default:
                        result = engine.GetPath(o, column.getPath());
                }
                row.put(column.getColumnName(), result);
            }
            this.rows.add(row);

        }
    }

    private String getData(String endPointUrl, String endPointMethod, String endPointData) throws Exception {
        if (endPointMethod == null) throw new Exception("Uknown method");
        // TODO headers etc in MVEL
        switch (endPointMethod.toLowerCase()) {
            case "get":
                return Unirest.get(endPointUrl).asString().getBody();
            case "post":
                return Unirest.post(endPointUrl).body(endPointData).asString().getBody();
            case "put":
                return Unirest.post(endPointUrl).body(endPointData).asString().getBody();
            default:
                throw new Exception("Uknown method");
        }
    }
}
