package com.base2.kagura.core.storage;

import com.base2.kagura.core.report.configmodel.ReportsConfig;
import com.google.common.io.PatternFilenameFilter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author aubels
 *         Date: 15/10/13
 */
public class FileReportsProvider extends ReportsProvider<File> {
    String reportDirectory;

    public FileReportsProvider(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    @Override
    protected String loadReport(ReportsConfig result, File report) throws Exception {
        final String reportName = report.getName();
        loadReport(result, report, reportName);
        return reportName;
    }

    protected void loadReport(ReportsConfig result, File report, String reportName) throws IOException {
        if (report.isDirectory())
        {
            FilenameFilter configYamlFilter = new PatternFilenameFilter("^reportconf.(yaml|json)$");
            File[] selectYaml = report.listFiles(configYamlFilter);
            if (selectYaml != null && selectYaml.length == 1)
            {
                File selectedYaml = selectYaml[0];
                loadReport(result, FileUtils.openInputStream(selectedYaml), reportName);
            }
        }
    }

    @Override
    protected File[] getReportList() {
        File file = new File(reportDirectory);
        if (!file.exists())
           file = new File(FileReportsProvider.class.getResource(reportDirectory).getFile());
        if (!file.exists()) {
            errors.add("Couldn't open report directory, doesn't exist.");
            return null;
        }
        return file.listFiles();
    }

    @Override
    protected String reportToName(File report) {
        if (report == null) return "<null>";
        return report.getName();
    }
}
