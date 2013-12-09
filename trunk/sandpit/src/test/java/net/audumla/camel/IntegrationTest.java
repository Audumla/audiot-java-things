package net.audumla.camel;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.log4j.Logger;
import org.junit.Test;

public class IntegrationTest extends CamelTestBase{
    protected static final Logger logger = Logger.getLogger(IntegrationTest.class);

    @Test
    public void simpleTest() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:in").to("mock:out");
            }
        });

        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(2000);
        resultEndpoint.expectedMessageCount(1);
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:in", "Direct Stream");

        resultEndpoint.assertIsSatisfied();
    }



}
