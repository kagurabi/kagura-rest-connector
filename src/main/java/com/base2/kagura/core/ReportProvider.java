/*
   Copyright 2014 base2Services

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.base2.kagura.core;

import com.base2.kagura.core.report.configmodel.ReportConfig;
import com.base2.kagura.core.report.configmodel.ReportsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The base class for all ReportProviders. Allows a templated type, the "InternalType" type must be safe to pass
 * internally, and reversible, such as you can determine the report name from the report's instance of "InternalType"
 * @author aubels
 *         Date: 15/10/13
 */
public interface ReportProvider {

	ReportConfig LoadReport(String reportId) throws Exception;

    /**
     * Stores loading errors here. Once checked #resetErrors()
     * @return
     */
    public List<String> getErrors();

    /**
     * Clears reports.
     */
    public void resetErrors();
}
