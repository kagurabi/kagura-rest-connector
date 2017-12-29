package com.base2.kagura.contribute.rest.report.configmodel;

import com.base2.kagura.contribute.rest.report.configmodel.restmodel.RestConfig;
import com.base2.kagura.contribute.rest.report.connector.Rest1Connector;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.connectors.ReportConnector;

public class Rest1ReportConfig extends ReportConfig {


    RestConfig config;

    @Override
    public ReportConnector getReportConnector() {
        return new Rest1Connector(this) {{

        }};
    }

    @Override
    public String getReportType() {
        return "Rest1";
    }

    public RestConfig getConfig() {
        return config;
    }

    public void setConfig(RestConfig config) {
        this.config = config;
    }
}
