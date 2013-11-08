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

import net.audumla.astronomy.AstronomicEvent;
import net.audumla.astronomy.Location;
import net.audumla.astronomy.ObjectRiseEvent;
import net.audumla.astronomy.OrbitingObject;
import net.audumla.astronomy.algorithims.Sun;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class SunScheduleTest {
    private static Logger logger = Logger.getLogger("Test");

    @Before
    public void setUp() throws Exception {
        StdSchedulerFactory.getDefaultScheduler().clear();

    }

    @After
    public void tearDown() throws Exception {
        if (StdSchedulerFactory.getDefaultScheduler().isStarted()) {
            StdSchedulerFactory.getDefaultScheduler().shutdown();
        }

    }

    protected AtomicInteger scheduleJob(Trigger trigger, int expectedCount, int maxWait) throws SchedulerException {
        AtomicInteger ai = new AtomicInteger(expectedCount);
        JobDetail job = JobBuilder.newJob(TestJob.class).withIdentity("testJob", "testJobGroup").storeDurably().build();
        job.getJobDataMap().put(TestJob.EXECUTION_COUNT, ai);
        StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
        if (!StdSchedulerFactory.getDefaultScheduler().isStarted()) {
            StdSchedulerFactory.getDefaultScheduler().start();

        }
        synchronized (ai) {
            try {
                ai.wait(maxWait * 1000);
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
        Trigger trigger = AstronomicScheduleBuilder.astronomicalSchedule().startEvent(event).startEventOffset(getOffsetFromEvent(event) + 2).withEventCount(1).build();
        AtomicInteger ai = scheduleJob(trigger, 1, 5);
        assert ai.get() == 0;
        trigger = StdSchedulerFactory.getDefaultScheduler().getTrigger(trigger.getKey());
        if (trigger != null) {
            assert trigger.getNextFireTime() == null;
        }

    }

    @Test
    public void testEveryRise() throws Exception {
        AstronomicEvent event = getSunRiseEvent();
        long offset = getOffsetFromEvent(event) + 1;
        Trigger trigger = AstronomicScheduleBuilder.astronomicalSchedule().startEvent(event).startEventOffset(offset).forEveryEvent().build();
        AtomicInteger ai = scheduleJob(trigger, 1, 5);
        assert ai.get() == 0;
        Assert.assertEquals(StdSchedulerFactory.getDefaultScheduler().getTrigger(trigger.getKey()).getNextFireTime().getTime(), event.getNextEvent().getCalculatedEventTime().getTime() + (offset * 1000), 100);

    }

    @Test
    public void testRise3Times() throws Exception {
        AstronomicEvent event = getSunRiseEvent();
        Date now = new Date();
        if (event.getCalculatedEventTime().before(now)) {
            now = DateUtils.addDays(now, 1);
        }
        AstronomicTriggerImpl trigger = (AstronomicTriggerImpl) AstronomicScheduleBuilder.astronomicalSchedule().startEvent(event).startEventOffset(0).withEventCount(3).build();
        trigger.computeFirstFireTime(null);
        Date fire = trigger.getNextFireTime();
        assert DateUtils.isSameDay(fire, now);
        trigger.triggered(null);
        fire = trigger.getNextFireTime();
        assert DateUtils.isSameDay(fire, DateUtils.addDays(now, 1));
        trigger.triggered(null);
        fire = trigger.getNextFireTime();
        assert DateUtils.isSameDay(fire, DateUtils.addDays(now, 2));
        trigger.triggered(null);
        fire = trigger.getNextFireTime();
        assert fire == null;

    }

    @Test
    public void testStartAndEndEvent() throws Exception {
        AstronomicEvent startEvent = getSunRiseEvent();
        AstronomicEvent endEvent = getSunRiseEvent();
        Date now = new Date();
        if (startEvent.getCalculatedEventTime().before(now)) {
            now = DateUtils.addDays(now, 1);
        }
        AstronomicTriggerImpl trigger = (AstronomicTriggerImpl) AstronomicScheduleBuilder.astronomicalSchedule().startEvent(startEvent).startEventOffset(-3).endEvent(endEvent).forEveryEvent().withIntervalInSeconds(1).repeatForever().build();
        trigger.computeFirstFireTime(null);
        Date fire = trigger.getNextFireTime();
        assert DateUtils.isSameDay(fire, now);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime());
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 1000);
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 2000);
        trigger.triggered(null);
        if (DateUtils.isSameDay(trigger.getNextFireTime(),fire)) {
            Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 3000);
            trigger.triggered(null);
        }
        fire = trigger.getNextFireTime();
        assert DateUtils.isSameDay(fire, DateUtils.addDays(now, 1));
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime(), 10);
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 1000, 10);
    }

    @Test
    public void test2SecondInterval3TimesEveryRise() throws Exception {
        AstronomicEvent event = getSunRiseEvent();
        Date now = new Date();
        if (event.getCalculatedEventTime().before(now)) {
            now = DateUtils.addDays(now, 1);
        }
        AstronomicTriggerImpl trigger = (AstronomicTriggerImpl) AstronomicScheduleBuilder.astronomicalSchedule().startEvent(event).startEventOffset(0).forEveryEvent().withRepeatCount(3).withIntervalInSeconds(2).build();
        trigger.computeFirstFireTime(null);
        Date fire = trigger.getNextFireTime();
        assert DateUtils.isSameDay(fire, now);
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 2000, 10);
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 4000, 10);
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 6000, 10);

        trigger.triggered(null);
        fire = trigger.getNextFireTime();
        assert DateUtils.isSameDay(fire, DateUtils.addDays(now, 1));
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 2000, 10);
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 4000, 10);
        trigger.triggered(null);
        Assert.assertEquals(trigger.getNextFireTime().getTime(), fire.getTime() + 6000, 10);

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