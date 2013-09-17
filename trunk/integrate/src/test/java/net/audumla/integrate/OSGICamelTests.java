package net.audumla.integrate;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.junit.Test;

public class OSGICamelTests extends CamelTestBase {
    private static final Logger logger = Logger.getLogger(OSGICamelTests.class);

    @Test
    public void basicTest() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:in").to("stream:out");
            }
        });

        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:in", "Direct Stream");
    }
}
