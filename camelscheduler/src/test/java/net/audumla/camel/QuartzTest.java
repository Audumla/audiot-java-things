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

import net.audumla.astronomy.*;
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

import java.util.Date;

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
                from("scheduler://group/timer?trigger.object=sun&trigger.event=rise&trigger.repeatInterval=1&trigger.repeatCount=2").to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

//        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(0);
//
        resultEndpoint.assertIsSatisfied();

    }

    @Test
    public void testAudumlaSunriseTrigger3() throws Exception {
        Geolocation loc = Geolocation.newGeoLocation(37.7, 145.1, 0);
        CelestialObjectRiseEvent ev = new CelestialObjectRiseEvent(CelestialObject.SUN, loc, CelestialObject.Inclination.CIVIL.getAngle());
        Date rise = ev.calculateEventFrom(new Date());
        long offset = (new Date().getTime() - rise.getTime()+2000)/1000;

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("scheduler://group/timer?trigger.object=sun&trigger.event=rise&trigger.repeatInterval=1&trigger.repeatCount=2&location="+loc.toString()+"&trigger.eventOffset="+offset).to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(8000);
        resultEndpoint.expectedMessageCount(3);
//
        resultEndpoint.assertIsSatisfied();

    }

    @Test
    public void testAudumlaSunsetTrigger1() throws Exception {
        Geolocation loc = Geolocation.newGeoLocation(37.7, 145.1, 0);
        AstronomicEvent ev = new CelestialObjectSetEvent(CelestialObject.SUN, loc, CelestialObject.Inclination.CIVIL.getAngle());
        Date set = ev.calculateEventFrom(new Date());
        long offset = (new Date().getTime() - set.getTime()+2000)/1000;
        assert set.getTime()+(offset*1000) > new Date().getTime();
        assert new Date(set.getTime()+(offset*1000)).before(new Date(new Date().getTime()+3000));
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("scheduler://group/timer?trigger.object=sun&trigger.event=set&trigger.repeatCount=2&location="+loc.toString()+"&trigger.eventOffset="+offset).to("mock:out");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);

        resultEndpoint.setAssertPeriod(4000);
        resultEndpoint.expectedMessageCount(1);
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

        resultEndpoint.setAssertPeriod(4500);
        resultEndpoint.expectedMessageCount(4);

        resultEndpoint.assertIsSatisfied();

    }


    @Test
    public void testStandardCron() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz2://group/timer?trigger.cron=*/10+*+*+*+*+?").to("mock:out");
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
