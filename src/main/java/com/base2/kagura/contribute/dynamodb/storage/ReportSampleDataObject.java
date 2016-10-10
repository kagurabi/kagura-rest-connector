package com.base2.kagura.contribute.dynamodb.storage;

/**
 * Created by arran on 10/10/2016.
 */
public class ReportSampleDataObject {
	private String reportId;
	private String reportYaml;

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportYaml() {
		return reportYaml;
	}

	public void setReportYaml(String reportYaml) {
		this.reportYaml = reportYaml;
	}
}
