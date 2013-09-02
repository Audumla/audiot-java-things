package net.audumla.scheduler.quartz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class SequentialListenerTest {
	private static Logger logger = LogManager.getLogger("Test");

	public static class TestJob implements Job {

		public static int count = 0;
		public static boolean executing = false;

		public TestJob() {

		}

		public void execute(JobExecutionContext jec) throws JobExecutionException {
			try {
				executing = true;
				logger.debug("Job is Starting - " + jec.getJobDetail().getKey().getName());
				synchronized (this) {
					this.wait(500);
				}
				logger.debug("Job is ending - " + jec.getJobDetail().getKey().getName());
				++count;
				if (executing) {
					logger.debug("Job is currently executing - " + jec.getJobDetail().getKey().getName());
				}
				executing = false;
			} catch (InterruptedException e) {
				logger.warn("Job failed to execute - " + jec.getJobDetail().getKey().getName());
				throw new JobExecutionException(e);
			}

		}

	}

	protected int scheduleJob(Scheduler scheduler, String jobName, String jobGroup, int interval, int count) throws SchedulerException {
		JobDetail job = JobBuilder.newJob(TestJob.class).withIdentity(jobName, jobGroup).build();

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + jobName, jobGroup).startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).withRepeatCount(count-1)).build();

		scheduler.scheduleJob(job, trigger);
		return count;

	}

	@Test
	public void testSequentialExecution() throws Exception {

		int expectedCount = 0;
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		expectedCount += scheduleJob(scheduler, "job1", "group", 2, 5);
		expectedCount += scheduleJob(scheduler, "job2", "group", 1, 10);
		expectedCount += scheduleJob(scheduler, "job3", "group", 2, 5);
		expectedCount += scheduleJob(scheduler, "job4", "group", 1, 10);
		scheduler.getListenerManager().addTriggerListener(new SequentialTriggerListener(), GroupMatcher.anyTriggerGroup());
		scheduler.start();
		int time = 0;
		synchronized (this) {
			while (time < 30) {
				this.wait(1000);
				++time;
				logger.debug("Executing for " + time + " seconds - [" + expectedCount + ":" + TestJob.count + "]");
			}
		}
		System.out.println(TestJob.count);
		scheduler.shutdown();
	}
}