package com.base2.kagura.contribute.rest.report.configmodel.restmodel;

public class ColumnSelect extends PathTypeBase {
    String columnName;
    private ScriptChoice transformer;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ScriptChoice getTransformer() {
        return transformer;
    }

    public void setTransformer(ScriptChoice transformer) {
        this.transformer = transformer;
    }
}
