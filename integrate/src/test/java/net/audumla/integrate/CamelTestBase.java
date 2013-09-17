package net.audumla.integrate;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

public class CamelTestBase {
    private static final Logger logger = Logger.getLogger(CamelTestBase.class);
    protected CamelContext context;


    @Before
    public void setUp() throws Exception {
        context = new DefaultCamelContext();
        context.start();
    }

    @After
    public void tearDown() throws Exception {
        context.stop();

    }

}
