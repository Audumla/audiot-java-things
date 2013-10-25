package net.audumla.automate.scheduler.quartz;

import net.audumla.Time;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class AstrologicalTest {
    private static Logger logger = Logger.getLogger("Test");

    @Before
    public void setUp() throws Exception {
        TestJob.count = 0;
        StdSchedulerFactory.getDefaultScheduler().clear();
    }

    public static class TestJob implements Job {

        public static int count = 0;

        public TestJob() {

        }

        public void execute(JobExecutionContext jec) throws JobExecutionException {
            try {
                logger.debug("Job is Starting - " + jec.getJobDetail().getKey().getName());
                synchronized (this) {
                    this.wait(500);
                }
                logger.debug("Job is ending - " + jec.getJobDetail().getKey().getName());
                ++count;
            } catch (InterruptedException e) {
                logger.warn("Job failed to execute - " + jec.getJobDetail().getKey().getName());
                throw new JobExecutionException(e);
            }

        }

    }

    protected void scheduleJob(Scheduler scheduler, String jobName, String jobGroup, int interval, int offset, int repeatCount) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(TestJob.class).withIdentity(jobName, jobGroup).build();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + jobName, jobGroup).startNow()
                .withSchedule(AstrologicalScheduleBuilder.astrologicalSchedule().offsetStartFromSunrise(offset).offsetEndFromSunset(60 * 60 * 5).withRepeatCount(repeatCount).withSecondInterval(interval).atLocation(-38, 145)).build();

        scheduler.scheduleJob(job, trigger);

    }


    @Test
    public void testExecution() throws Exception {
        Date now = new Date();
        Date time2 = Time.getSunrise(now, -38, 145);

        int expectedCount = 4;
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduleJob(scheduler, "job1", "group", 2, 5,1);
        scheduleJob(scheduler, "job2", "group", 1, 10,1);
        scheduleJob(scheduler, "job3", "group", 2, 5,1);
        scheduleJob(scheduler, "job4", "group", 1, 10,1);
        scheduler.start();
        int time = 0;
        synchronized (this) {
            while (time < 5) {
                this.wait(1000);
                ++time;
            }
        }
        scheduler.shutdown();
        assert expectedCount == TestJob.count;
    }


    @Test
    public void testAstrologicalExecution() throws Exception {
        Date now = new Date();
        Date time2 = Time.getSunrise(now, -38, 145);
        // get the time offset 1 second later than now relative to sunrise
        int offset = (int) ((now.getTime() - time2.getTime()+1000)/1000);


        int expectedCount = 4;
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduleJob(scheduler, "job1", "group", 1, offset, 1);
        scheduler.start();
        int time = 0;
        synchronized (this) {
            while (time < 5 && TestJob.count == 0) {
                this.wait(1000);
                ++time;
            }
        }
        scheduler.shutdown();
        assert TestJob.count == 1;
    }


    @Test
    public void testAstrologicalExecution2() throws Exception {
        Date now = new Date();
        Date time2 = Time.getSunrise(now, -38, 145);
        // get the time offset 1 second later than now relative to sunrise
        int offset = (int) ((now.getTime() - time2.getTime()+1000)/1000);


        int expectedCount = 4;
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduleJob(scheduler, "job1", "group", 1, offset,3);
        scheduler.start();
        int time = 0;
        synchronized (this) {
            while (time < 5 ) {
                this.wait(1000);
                ++time;
            }
        }
        scheduler.shutdown();
        assert TestJob.count == 3;
    }
}