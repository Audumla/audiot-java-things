package org.apache.camel.test.blueprint;

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

import net.audumla.camel.CamelTestBase;
import net.audumla.camel.StringToMapTypeConverter;
import org.apache.camel.Exchange;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.util.KeyValueHolder;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.*;

public class OSGICamelTest extends CamelBlueprintTestSupport {
    private static final Logger logger = Logger.getLogger(CamelTestBase.class);

    private boolean debugBeforeMethodCalled;
    private boolean debugAfterMethodCalled;

    // override this method, and return the location of our Blueprint XML file to be used for testing
    @Override
    protected String getBlueprintDescriptor() {
        return "org/apache/camel/test/blueprint/camelContext.xml";
    }

    // here we have regular JUnit @Test method
    @Test
    public void testRoute() throws Exception {
        context.getTypeConverterRegistry().addTypeConverter(Map.class, String.class, new StringToMapTypeConverter());
        getMockEndpoint("mock:a").expectedMessageCount(1);
        template.sendBody("direct:start", "World");
        assertMockEndpointsSatisfied();
        assertTrue(debugBeforeMethodCalled);
        assertTrue(debugAfterMethodCalled);
    }

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, org.apache.camel.Processor processor, ProcessorDefinition<?>
            definition, String id, String label) {
        log.info("Before " + definition + " with body " + exchange.getIn().getBody());
        debugBeforeMethodCalled = true;
    }

    @Override
    protected void debugAfter(Exchange exchange, org.apache.camel.Processor processor, ProcessorDefinition<?>
            definition, String id, String label, long timeTaken) {
        log.info("After " + definition + " with body " + exchange.getIn().getBody());
        debugAfterMethodCalled = true;
    }

    @Override
    protected void addServicesOnStartup(Map<String, KeyValueHolder<Object, Dictionary>> services) {

    }


}
