package com.base2.kagura.contribute.rest.report.configmodel.restmodel;

import java.util.List;
import java.util.Map;

public class RestConfig {
    String tokenSource;
    String endPointUrl;
    String endPointMethod;
    Map<String, String> endPointHeaders;
    String endPointData;
    List<ErrorDetection> errorDetection;
    String pathTool;
    String rowVariablePath;
    List<ColumnSelect> selects;
    List<RowFilter> filters;

    public String getTokenSource() {
        return tokenSource;
    }

    public void setTokenSource(String tokenSource) {
        this.tokenSource = tokenSource;
    }

    public String getEndPointUrl() {
        return endPointUrl;
    }

    public void setEndPointUrl(String endPointUrl) {
        this.endPointUrl = endPointUrl;
    }

    public String getEndPointMethod() {
        return endPointMethod;
    }

    public void setEndPointMethod(String endPointMethod) {
        this.endPointMethod = endPointMethod;
    }

    public String getEndPointData() {
        return endPointData;
    }

    public void setEndPointData(String endPointData) {
        this.endPointData = endPointData;
    }

    public List<ErrorDetection> getErrorDetection() {
        return errorDetection;
    }

    public void setErrorDetection(List<ErrorDetection> errorDetection) {
        this.errorDetection = errorDetection;
    }

    public String getRowVariablePath() {
        return rowVariablePath;
    }

    public void setRowVariablePath(String rowVariablePath) {
        this.rowVariablePath = rowVariablePath;
    }

    public List<ColumnSelect> getSelects() {
        return selects;
    }

    public void setSelects(List<ColumnSelect> selects) {
        this.selects = selects;
    }

    public List<RowFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<RowFilter> filters) {
        this.filters = filters;
    }

    public String getPathTool() {
        return pathTool;
    }

    public void setPathTool(String pathTool) {
        this.pathTool = pathTool;
    }

    public Map<String, String> getEndPointHeaders() {
        return endPointHeaders;
    }

    public void setEndPointHeaders(Map<String, String> endPointHeaders) {
        this.endPointHeaders = endPointHeaders;
    }
}
