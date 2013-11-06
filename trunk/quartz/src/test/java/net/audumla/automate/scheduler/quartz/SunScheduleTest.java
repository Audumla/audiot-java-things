package net.audumla.automate.scheduler.quartz;

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

import junit.framework.Assert;
import net.audumla.astronomical.AstronomicEvent;
import net.audumla.astronomical.Location;
import net.audumla.astronomical.ObjectRiseEvent;
import net.audumla.astronomical.OrbitingObject;
import net.audumla.astronomical.algorithims.Sun;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class SunScheduleTest {
    private static Logger logger = Logger.getLogger("Test");

    @Before
    public void setUp() throws Exception {
        StdSchedulerFactory.getDefaultScheduler().clear();
        StdSchedulerFactory.getDefaultScheduler().start();

    }

    @After
    public void tearDown() throws Exception {
        StdSchedulerFactory.getDefaultScheduler().shutdown();

    }

    protected AtomicInteger scheduleJob(Trigger trigger, int expectedCount, int maxWait) throws SchedulerException {
        AtomicInteger ai = new AtomicInteger(expectedCount);
        JobDetail job = JobBuilder.newJob(TestJob.class).withIdentity("testJob", "testJobGroup").storeDurably().build();
        job.getJobDataMap().put(TestJob.EXECUTION_COUNT, ai);
        StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
        synchronized (ai) {
            try {
                ai.wait(maxWait*1000);
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        return ai;

    }

    public AstronomicEvent getSunRiseEvent() {
        AstronomicEvent event = new ObjectRiseEvent(OrbitingObject.Sun, new Location(-38, 145, 0), Sun.CIVIL);
        if (event.getCalculatedEventTime().after(new Date())) {
            event = event.getPreviousEvent();
        }
        return event;
    }

    public long getOffsetFromEvent(AstronomicEvent event) {
        Date now = new Date();
        Date eTime = event.getCalculatedEventTime();
        return (now.getTime() - eTime.getTime()) / 1000;

    }

    @Test
    public void testSingleRiseOnly() throws Exception {
        AstronomicEvent event = getSunRiseEvent();
        Trigger trigger = AstronomicScheduleBuilder.astronomicalSchedule().startEvent(event).startEventOffset(getOffsetFromEvent(event)+1).withEventCount(1).build();
        AtomicInteger ai = scheduleJob(trigger,1,5);
        assert ai.get() == 0;
        assert StdSchedulerFactory.getDefaultScheduler().getTrigger(trigger.getKey()).getNextFireTime() == null;

    }

    @Test
    public void testEveryRise() throws Exception {
        AstronomicEvent event = getSunRiseEvent();
        long offset = getOffsetFromEvent(event) + 1;
        Trigger trigger = AstronomicScheduleBuilder.astronomicalSchedule().startEvent(event).startEventOffset(offset).forEveryEvent().build();
        AtomicInteger ai = scheduleJob(trigger,1,5);
        assert ai.get() == 0;
        Assert.assertEquals(StdSchedulerFactory.getDefaultScheduler().getTrigger(trigger.getKey()).getNextFireTime().getTime() , event.getNextEvent().getCalculatedEventTime().getTime()+(offset*1000),100);

    }

    @Test
    public void testRise3Times() throws Exception {
        AstronomicScheduleBuilder.astronomicalSchedule().startEvent(new ObjectRiseEvent(OrbitingObject.Sun, new Location(-38, 145, 0), Sun.CIVIL)).startEventOffset(0).withEventCount(3);

    }

    @Test
    public void test1SecondInterval3TimesEveryRise() throws Exception {
        AstronomicScheduleBuilder.astronomicalSchedule().startEvent(new ObjectRiseEvent(OrbitingObject.Sun, new Location(-38, 145, 0), Sun.CIVIL)).startEventOffset(0).withCount(3).withSecondInterval(1).forEveryEvent();

    }

    public static class TestJob implements Job {

        static public String EXECUTION_COUNT = "execkey";
        public int count = 0;

        public TestJob() {

        }

        public void execute(JobExecutionContext jec) throws JobExecutionException {
            ++count;
            AtomicInteger ai = (AtomicInteger) jec.getMergedJobDataMap().get(EXECUTION_COUNT);
            logger.debug("Job executed - " + jec.getJobDetail().getKey().getName());
            ai.set(ai.get() - 1);
            if (ai.get() <= 0) {
                synchronized (ai) {
                    ai.notifyAll();
                }
            }
        }

    }

}