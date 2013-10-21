package net.audumla.integrate.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

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
