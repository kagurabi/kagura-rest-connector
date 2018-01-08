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
package com.base2.kagura.core.report.configmodel;


import com.base2.kagura.core.KaguraUtil;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.Matchers.*;

/**
 * Created with IntelliJ IDEA.
 * User: aubels
 * Date: 25/07/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyParamConfigTest {
    @Test
    public void getReports1Test() throws URISyntaxException, IOException {
        URL reportDirectory = this.getClass().getResource("/reportTest1/");
        ReportConfig groovy1 = KaguraUtil.LoadYamlFromRootUrl(reportDirectory, "groovy1");
        Assert.assertThat(groovy1, Matchers.notNullValue());
        Assert.assertThat(groovy1, IsInstanceOf.instanceOf(FakeReportConfig.class));
        Assert.assertThat(groovy1.getParamConfig().get(0).getValues(), contains((Object)"1","2","3","4","5"));
    }
}
