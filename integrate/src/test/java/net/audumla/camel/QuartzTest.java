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

import net.audumla.camel.scheduler.CelestialScheduleEndpoint;
import net.audumla.camel.scheduler.CronSchedulerEndpoint;
import net.audumla.camel.scheduler.SchedulerComponent;
import net.audumla.camel.scheduler.SeasonalScheduleEndpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class QuartzTest extends CamelTestBase {
    protected static final Logger logger = Logger.getLogger(QuartzTest.class);

    @Before
    public void setUp() throws Exception {
        context = new DefaultCamelContext();
        SchedulerComponent schComponent = new SchedulerComponent(context);
        context.addComponent("scheduler", schComponent);
        context.start();
        schComponent.registerScheduler(CronSchedulerEndpoint.class);
        schComponent.registerScheduler(CelestialScheduleEndpoint.class);
        schComponent.registerScheduler(SeasonalScheduleEndpoint.class);

    }


    @Test
    public void testFailedAudumlaAstro() throws Exception {

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("scheduler://group/timer?celestial.object=sun&celestial.event=rise&celestial.repeatInterval=1&celestial.repeatCount=2").to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

//        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(0);
//
        resultEndpoint.assertIsSatisfied();

    }

    @Test
    public void testAudumlaCron() throws Exception {
       context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("scheduler://group/timer?cron=*/10+*+*+*+*+?").to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(1);

        resultEndpoint.assertIsSatisfied();

    }

    @Test
    public void testAudumlaSimple() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("scheduler://group/timer12?trigger.repeatInterval=1&trigger.repeatCount=1").to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(2);

        resultEndpoint.assertIsSatisfied();

    }

    @Test
    public void testQuartz2Simple() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz2://group/timer12?trigger.repeatInterval=1&trigger.repeatCount=3").to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(2);

        resultEndpoint.assertIsSatisfied();

    }


    @Test
    public void testStandardCron() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz2://group/timer?cron=*/10+*+*+*+*+?").to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(1);

        resultEndpoint.assertIsSatisfied();

    }

    @Test
    public void testStandardSimple() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz2://group/timer12?trigger.repeatInterval=1&trigger.repeatCount=1").to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(2);

        resultEndpoint.assertIsSatisfied();

    }
}
