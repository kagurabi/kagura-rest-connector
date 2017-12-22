package com.base2.kagura.contribute.rest.report.configmodel;

import com.base2.kagura.contribute.rest.report.configmodel.restmodel.RestConfig;
import com.base2.kagura.contribute.rest.report.connector.RestConnector;
import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.connectors.ReportConnector;

public class RestReportConfig extends ReportConfig {

    RestConfig restConfig;

    @Override
    public ReportConnector getReportConnector() {
        return new RestConnector(this) {{

        }};
    }

    @Override
    public String getReportType() {
        return "Rest";
    }

    public RestConfig getRestConfig() {
        return restConfig;
    }

    public void setRestConfig(RestConfig restConfig) {
        this.restConfig = restConfig;
    }
}
