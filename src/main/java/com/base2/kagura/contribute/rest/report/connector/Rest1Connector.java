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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rest1Connector extends ReportConnector {
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
        this.rows = new ArrayList<>();
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
            data = getData(restConfig.getEndPointUrl(), restConfig.getEndPointMethod(), restConfig.getEndPointData(), restConfig.getEndPointHeaders());
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

        for (Object o : rowVars) {
            boolean skip = false;
            JtwigModel model = JtwigModel.newModel()
                .with("root", data)
                .with("row", o);
            if (restConfig.getFilters() != null) {
                for (RowFilter filter : restConfig.getFilters()) {
                    Object result;// = engine.GetPath(data, column.getPath());
                    String type = "";
                    if (filter.getType() != null) {
                        type = filter.getType().toLowerCase();
                    }
                    JtwigTemplate filterPathTemplate = JtwigTemplate.inlineTemplate(filter.getPath());
                    String filterPath = filterPathTemplate.render(model);
                    switch (type) {
                        case "root":
                            result = engine.GetPath(data, filterPath);
                            break;
                        case "row":
                        case "":
                        default:
                            result = engine.GetPath(o, filterPath);
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
                    if (filter.getMatchRule() == null) {
                        skip = true;
                        break;
                    } else {
                        if (StringUtils.isNotBlank(filter.getMatchRule().getJtwig())) {
                            JtwigTemplate template = JtwigTemplate.inlineTemplate(filter.getMatchRule().getJtwig());
                            String filterResult = template.render(model);
                            if (!BooleanUtils.toBoolean(filterResult)) {
                                skip = true;
                            }
                        } else {
                            logger.info("Unnecessary empty rule");
                        }
                    }
                }
            }
            if (skip) continue;
            Map<String, Object> row = new HashMap<>();
            for (ColumnSelect column : restConfig.getSelects()) {
                Object result;// = engine.GetPath(data, column.getPath());
                String type = "";
                JtwigTemplate columnPathTemplate = JtwigTemplate.inlineTemplate(column.getPath());
                String columnPath = columnPathTemplate.render(model);

                if (column.getType() != null) {
                    type = column.getType().toLowerCase();
                }
                switch (type) {
                    case "root":
                        result = engine.GetPath(data, columnPath);
                        break;
                    case "row":
                    case "":
                    default:
                        result = engine.GetPath(o, columnPath);
                }
                if (column.getTransformer() != null) {
                    if (StringUtils.isNotBlank(column.getTransformer().getJtwig())) {
                        JtwigModel transformModel = JtwigModel.newModel()
                        .with("result", result)
                        .with("root", data)
                        .with("row", o);
                        JtwigTemplate columnTransformTemplate = JtwigTemplate.inlineTemplate(column.getTransformer().getJtwig());
                        String transformedResult = columnTransformTemplate.render(transformModel);
                        result = transformedResult;
                    } else {
                        logger.info("Unnecessary empty transformer");
                    }
                }
                row.put(column.getColumnName(), result);
            }
            this.rows.add(row);

        }
    }

    private String getData(String endPointUrl, String endPointMethod, String endPointData, Map<String, String> headers) throws Exception {
        if (endPointMethod == null) throw new Exception("Uknown method");
        // TODO headers etc in MVEL
        switch (endPointMethod.toLowerCase()) {
            case "get":
                return Unirest.get(endPointUrl).headers(headers).asString().getBody();
            case "post":
                return Unirest.post(endPointUrl).headers(headers).body(endPointData).asString().getBody();
            case "put":
                return Unirest.post(endPointUrl).headers(headers).body(endPointData).asString().getBody();
            default:
                throw new Exception("Uknown method");
        }
    }
}
