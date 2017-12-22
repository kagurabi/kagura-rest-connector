package com.base2.kagura.contribute.rest.report.connector;

import com.base2.kagura.contribute.rest.report.configmodel.RestReportConfig;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.ColumnSelect;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.ErrorDetection;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.RestConfig;
import com.base2.kagura.contribute.rest.report.configmodel.restmodel.RowFilter;
import com.base2.kagura.contribute.rest.report.connector.pathtools.Engine;
import com.base2.kagura.contribute.rest.report.connector.pathtools.JsonPathV1Engine;
import com.base2.kagura.core.report.connectors.ReportConnector;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestConnector extends ReportConnector {
    private List<Map<String, Object>> rows;
    private RestConfig restConfig;
    /**
     * Does a shallow copy of the necessary restReportConfig values. Initializes the error structure.
     *
     * @param restReportConfig
     */
    protected RestConnector(RestReportConfig restReportConfig) {
        super(restReportConfig);
        this.restConfig = restReportConfig.getRestConfig();
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
            case "jsonv1":
            case "jsonpathv1":
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

        for (ErrorDetection errorDetection : restConfig.getErrorDetection()) {
            if (engine.Match(data, errorDetection.getErrorPath())) {
                getErrors().add(errorDetection.getErrorMessage());
            }
        }

        if (this.errors != null && this.errors.size() > 0) {
            return;
        }

        List<Object> rowVars = engine.GetRowVars(data);

        this.rows = new ArrayList<>();
        for (Object o : rowVars) {
            boolean skip = false;
            for (RowFilter filter : restConfig.getFilters()) {
                String result = engine.GetPath(filter.getPath());
                if (StringUtils.isBlank(result)) {
                    continue;
                }
                if (StringUtils.isBlank(filter.getMatchRule())) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
            Map<String, Object> row = new HashMap<>();
            for (ColumnSelect column : restConfig.getSelects()) {
                row.put(column.getColumnName(), engine.GetPath(column.getPath()));
            }
            this.rows.add(row);

            // TODO https://github.com/mvel/mvel
            // TODO offset
            // TODO pagination
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
