package net.audumla.integrate;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

public class T1Test {
    protected static final Logger logger = Logger.getLogger(T1Test.class);

    @Test
    public void simpleTest() throws Exception {
/*        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:in").to("stream:out");
            }
        });

        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:in", "Direct Stream");
  */
    }

    @Test
    public void testHeaderStream() throws Exception {
        StringBuilder sb = new StringBuilder();
        String text = "Header Stream";
        OutputStream os = new OutputStream() {

            public void write(int b) throws IOException {
                sb.append((char) b);
            }
        };
        /*
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:in").setHeader("stream", constant(os)).
                        to("stream:header");
            }
        });

        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:in", text);
        assert sb.toString().contains(text);                             */

    }

}
