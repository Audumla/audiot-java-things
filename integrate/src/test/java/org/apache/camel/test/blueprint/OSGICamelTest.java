package org.apache.camel.test.blueprint;

import net.audumla.integrate.CamelTestBase;
import net.audumla.integrate.StringToMapTypeConverter;
import org.apache.camel.Exchange;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.camel.util.KeyValueHolder;
import org.apache.felix.eventadmin.impl.Activator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.*;
import java.util.stream.Collectors;

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
