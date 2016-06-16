package com.base2.kagura.core.report.configmodel;

import java.util.*;

/**
 * Created by arran on 16/06/2016.
 */
public class ReportConfigTypeService {
	private static ReportConfigTypeService service;
	private ServiceLoader<ReportConfig> loader;

	private ReportConfigTypeService() {
		loader = ServiceLoader.load(ReportConfig.class);
	}

	public static synchronized ReportConfigTypeService getInstance() {
		if (service == null) {
			service = new ReportConfigTypeService();
		}
		return service;
	}


	public Map<String, Class> getMap() {
		Map<String, Class> result = new HashMap<String, Class>();
		try {
			Iterator<ReportConfig> reportConfigIterator = loader.iterator();
			while (reportConfigIterator.hasNext()) {
				ReportConfig d = reportConfigIterator.next();
				result.put(d.getReportType().toUpperCase(), d.getClass());
			}
		} catch (ServiceConfigurationError serviceError) {
			serviceError.printStackTrace();
		}
		return result;
	}
}
