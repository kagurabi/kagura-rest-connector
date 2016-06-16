package com.base2.kagura.core.report.configmodel;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

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


	public Class<? extends ReportConfig> getType(String type) {
		try {
			Iterator<ReportConfig> reportConfigIterator = loader.iterator();
			while (reportConfigIterator.hasNext()) {
				Class<? extends ReportConfig> d = reportConfigIterator.next().getClass();
				String matchString = type.toUpperCase() + "ReportConfig".toUpperCase();
				if (d.getSimpleName().toUpperCase().equals(matchString)) {
					return d;
				}
			}
		} catch (ServiceConfigurationError serviceError) {
			serviceError.printStackTrace();
		}
		return null;
	}
}
