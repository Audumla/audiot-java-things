/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;


import net.audumla.automate.EventFactory;
import net.audumla.automate.EventHandler;
import net.audumla.automate.scheduler.ScheduleAdaptor;
import net.audumla.automate.scheduler.AutomateJob;
import net.audumla.climate.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.listeners.JobListenerSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * User: audumla
 * Date: 22/07/13
 * Time: 10:50 PM
 */
public class SimpleIrrigationTest {
    private static final Logger logger = Logger.getLogger("Test");
    static int actualCount = 0;

    protected void scheduleJob(Scheduler scheduler, String jobName, String jobGroup, int repeat, EventHandler handler, EventFactory factory) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(AutomateJob.class).withIdentity(jobName, jobGroup).build();
        ScheduleAdaptor schedule = new ScheduleAdaptor(new net.audumla.automate.scheduler.Scheduler()) {
            @Override
            protected ScheduleBuilder getScheduleBuilder() throws ParseException {
                return null;
            }
        };

        schedule.setFactory(factory);
        schedule.setHandler(handler);
        job.getJobDataMap().put(AutomateJob.SCHEDULE_PROPERTY, schedule);

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + jobName, jobGroup).startNow()
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(repeat))
                .build();

        scheduler.scheduleJob(job, trigger);


    }

    protected ClimateObserver createObserver() {
        ClimateDataSource source = new ClimateDataSourceFactory().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        ClimateObserver observer = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        return observer;
    }

    protected IrrigationZone buildZone(ClimateObserver observer) {
        IrrigationZone zone = new IrrigationZone(observer);
        zone.setFlowRate(0.1);
        zone.setSurfaceArea(4.0);
        return zone;
    }

    protected void initialize(int repeatCount, Zone zone, EventFactory factory) throws SchedulerException, InterruptedException {

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        actualCount = 0;

        scheduler.getListenerManager().addJobListener(new JobListenerSupport() {
            @Override
            public String getName() {
                return "Listener";
            }

            @Override
            public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
                ++actualCount;
            }
        });

        scheduleJob(scheduler, "Irrigation Job", "Zone1", repeatCount, zone, factory);
    }

    protected void execute(int repeatCount) throws SchedulerException, InterruptedException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();

        int time = 0;
        synchronized (this) {
            while (time < 10 && repeatCount != actualCount) {
                this.wait(1000);
                ++time;
            }
        }
        scheduler.deleteJobs(new ArrayList<JobKey>(scheduler.getJobKeys(GroupMatcher.anyJobGroup())));
        scheduler.shutdown();
        Assert.assertEquals(actualCount, repeatCount);
    }

    @Test
    public void testEnclosedIrrigation() throws SchedulerException, InterruptedException {
        ClimateObserver obs1 = createObserver();
        IrrigationZone zone = buildZone(createObserver());
        zone.setCoverRating(1);
        zone.setEnclosureRating(0.8);
        zone.setShadeRating(0.1);
        EToCalculator etc = new EToCalculator();
        etc.setZone(zone);

        ClimateObserver obs2 = zone.getClimateObserver();

        Date now = DateUtils.addDays(new Date(), -2);
        ClimateData data1 = obs1.getClimateData(now);
        ClimateData data2 = obs2.getClimateData(now);

        if (data2.getAverageWindSpeed() != 0) {
            Assert.assertNotEquals(data1.getAverageWindSpeed(), data2.getAverageWindSpeed());
        }
        if (data2.getSolarRadiation() != 0) {
            Assert.assertNotEquals(data1.getSolarRadiation(), data2.getSolarRadiation());
        }
        if (data2.getRainfall() != 0) {
            Assert.assertNotEquals(data1.getRainfall(), data2.getRainfall());
        }
        if (data2.getMaximumHumidity() != 0) {
            Assert.assertNotEquals(data1.getMaximumHumidity(), data2.getMaximumHumidity());
        }
        Assert.assertEquals(data1.getDaylightHours(), data2.getDaylightHours(), 0.1);
        Assert.assertEquals(data1.getSunrise(), data2.getSunrise());
    }

    @Test
    public void testSingleIrrigation() throws SchedulerException, InterruptedException {
        IrrigationZone zone = buildZone(createObserver());
        EToCalculator etc = new EToCalculator();
        etc.setZone(zone);
        EToIrrigationEventFactory factory = new EToIrrigationEventFactory(zone, etc, new EToIrrigationDurationFactory(zone, etc));
        factory.setThreshold(1.0);
        initialize(5, zone, factory);
        execute(5);
        Assert.assertNotEquals(0, zone.getIrrigationEventsForDay(new Date()));
    }
}
