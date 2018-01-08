package com.base2.kagura.core;

import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.configmodel.ReportsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.PatternFilenameFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class KaguraUtil {
	private final static String reportConfFile = "reportconf.yaml";

	/**
	 * The main way you should easily load Kagura reports.
	 * @return
	 */
	public static ReportConfig LoadYaml(File file, String reportId) throws IOException {
		return LoadYaml(FileUtils.openInputStream(file), reportId);
	}
	/**
	 * The main way you should easily load Kagura reports.
	 * @return
	 */
	public static ReportConfig LoadYaml(InputStream reportStream, String reportId) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		ReportConfig reportConfig = null;
		reportConfig = mapper.readValue(reportStream, ReportConfig.class);
		reportConfig.setReportId(reportId);
		return reportConfig;
	}
	/**
	 * The main way you should easily load Kagura reports.
	 * @return
	 */
	public static ReportConfig LoadYamlFromDirectory(File directory, String reportId) throws Exception {
		FilenameFilter configYamlFilter = new PatternFilenameFilter("^reportconf.(yaml|json)$");
		File[] selectYaml = directory.listFiles(configYamlFilter);
		if (selectYaml != null && selectYaml.length == 1)
		{
			File selectedYaml = selectYaml[0];
			return LoadYaml(FileUtils.openInputStream(selectedYaml), reportId);
		}
		throw new Exception("Couldn't find file");
	}
	/**
	 * The main way you should easily load Kagura reports.
	 * @return
	 */
	public static ReportConfig LoadYamlFromDirectory(String directory, String reportId) throws Exception {
		return LoadYamlFromDirectory(new File(directory), reportId);
	}
	/**
	 * The main way you should easily load Kagura reports.
	 * @return
	 */
	public static ReportConfig LoadYamlFromRootDirectory(String directory, String reportId) throws Exception {
		return LoadYamlFromDirectory(new File(FilenameUtils.concat(directory, reportId)), reportId);
	}
	/**
	 * The main way you should easily load Kagura reports.
	 * @return
	 */
	public static ReportConfig LoadYamlFromRootDirectory(File directory, String reportId) throws Exception {
		return LoadYamlFromDirectory(new File(directory, reportId), reportId);
	}

	public static ReportConfig LoadYamlFromUrl(URL reportDirectory, String reportId) throws IOException {
		return LoadYaml(reportDirectory.openStream(), reportId);
	}

	public static ReportConfig LoadYamlFromRootUrl(URL rootPath, String reportId) throws URISyntaxException, IOException {
		return LoadYamlFromUrl(rootPath.toURI().resolve("./" + URLEncoder.encode(reportId) + "/" + reportConfFile).toURL(), reportId);
	}

	public static ReportsConfig PreloadYamlFromRootDirectory(String file) {
		return PreloadYamlFromRootDirectory(new File(file));
	}

	public static ReportsConfig PreloadYamlFromRootDirectory(File directory) {
		File[] reports = directory.listFiles();
		ReportsConfig result = new ReportsConfig();
		if (reports == null)
		{
			result.getErrors().add("No reports found.");
			return result;
		}
		for (File report : reports)
		{
			String named = report.getName();
			ReportConfig reportConf;
			try {
				reportConf = LoadYamlFromDirectory(report, named);
			} catch (Exception e) {
				result.getErrors().add("Error in report " + named + ": " + e.getMessage());
				e.printStackTrace();
				continue;
			}
			result.getReports().put(named, reportConf);
		}
		return result;
	}
}
