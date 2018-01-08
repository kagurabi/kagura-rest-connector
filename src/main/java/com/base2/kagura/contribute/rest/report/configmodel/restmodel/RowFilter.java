package com.base2.kagura.contribute.rest.report.configmodel.restmodel;

public class RowFilter extends PathTypeBase {
    ScriptChoice matchRule;

    public ScriptChoice getMatchRule() {
        return matchRule;
    }

    public void setMatchRule(ScriptChoice matchRule) {
        this.matchRule = matchRule;
    }
}
