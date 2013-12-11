package net.audumla.scheduler.quartz;

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

import org.apache.commons.lang.time.DateUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.*;

public class QuartzRunnableFuture<V> implements RunnableScheduledFuture<V> {
    private static final Logger logger = LoggerFactory.getLogger(QuartzRunnableFuture.class);
    private final Callable<V> callable;
    private final Scheduler scheduler;
    private final Trigger trigger;
    private final String name;
    private V result;
    private long delay;

    /**
     * Creates a {@code FutureTask} that will, upon running, execute the
     * given {@code Callable}.
     *
     * @param callable the callable task
     * @throws NullPointerException if the callable is null
     */
    public QuartzRunnableFuture(Callable<V> callable, Scheduler scheduler, String name) {
        if (callable == null)
            throw new NullPointerException();
        this.name = name;
        this.callable = callable;
        this.scheduler = scheduler;
        this.trigger = createTrigger(callable, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a {@code FutureTask} that will, upon running, execute the
     * given {@code Runnable}, and arrange that {@code get} will return the
     * given result on successful completion.
     *
     * @param runnable the runnable task
     * @param result   the result to return on successful completion. If
     *                 you don't need a particular result, consider using
     *                 constructions of the form:
     *                 {@code Future<?> f = new FutureTask<Void>(runnable, null)}
     * @throws NullPointerException if the runnable is null
     */
    public QuartzRunnableFuture(Runnable runnable, V result, Scheduler scheduler, String name) {
        this.name = name;
        this.callable = Executors.callable(runnable, result);
        this.trigger = createTrigger(callable, 0, TimeUnit.MILLISECONDS);
        this.scheduler= scheduler;
    }

    /**
     * Creates a {@code FutureTask} that will, upon running, execute the
     * given {@code Callable}.
     *
     * @param callable the callable task
     * @throws NullPointerException if the callable is null
     */
    public QuartzRunnableFuture(Callable<V> callable, Scheduler scheduler, long delay, TimeUnit timeUnit, String name) {
        if (callable == null)
            throw new NullPointerException();
        this.name = name;
        this.callable = callable;
        this.trigger = createTrigger(callable, delay, timeUnit);
        this.scheduler = scheduler;
    }

    /**
     * Creates a {@code FutureTask} that will, upon running, execute the
     * given {@code Runnable}, and arrange that {@code get} will return the
     * given result on successful completion.
     *
     * @param runnable the runnable task
     * @param result   the result to return on successful completion. If
     *                 you don't need a particular result, consider using
     *                 constructions of the form:
     *                 {@code Future<?> f = new FutureTask<Void>(runnable, null)}
     * @throws NullPointerException if the runnable is null
     */
    public QuartzRunnableFuture(Runnable runnable, V result, Scheduler scheduler, long delay, TimeUnit timeUnit, String name) {
        this.name = name;
        this.callable = Executors.callable(runnable, result);
        this.trigger = createTrigger(callable, delay, timeUnit);
        this.scheduler = scheduler;
    }

    protected Trigger createTrigger(Callable<V> callable, long delay, TimeUnit timeUnit) {
        try {
            this.delay = timeUnit.toSeconds(delay);
            Date start = DateUtils.addSeconds(new Date(), (int) this.delay);
            ScheduleBuilder<SimpleTrigger> sbuilder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(1).withMisfireHandlingInstructionFireNow();
            TriggerBuilder<SimpleTrigger> tbuilder = TriggerBuilder.newTrigger().withIdentity(getName(), "Group:" + getName()).startAt(start).withSchedule(sbuilder);
            return tbuilder.build();
        } catch (Exception e) {
            logger.error("Cannot create Trigger", e);
            return null;
        }
    }

    @Override
    public void run() {
        try {
            JobDetail job = JobBuilder.newJob(RunnableJob.class).withIdentity(trigger.getKey().getName(), trigger.getKey().getGroup()).storeDurably(false).build();
            job.getJobDataMap().put(RunnableJob.RUNNABLE_PROPERTY, callable);
            job.getJobDataMap().put(RunnableJob.RESULT_PROPERTY, this);
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.error("Error executing job {}", trigger.getKey(), e);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            return scheduler.unscheduleJob(trigger.getKey());
        } catch (SchedulerException e) {
            logger.warn("Unable to cancel job {}", trigger.getKey(), e);
        }
        return false;
    }

    public boolean isCancelled() {
        try {
            return scheduler.getTriggerState(trigger.getKey()).equals(Trigger.TriggerState.NONE);
        } catch (SchedulerException e) {
            logger.error("Cannot find trigger - {}", trigger.getKey());
        }
        return false;
    }

    public boolean isDone() {
        try {
            return scheduler.getTriggerState(trigger.getKey()).equals(Trigger.TriggerState.COMPLETE);
        } catch (SchedulerException e) {
            // if the trigger does not exist then we can assumer that it has been removed from the scheduler
            return true;
        }
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return result;
    }

    public String getName() {
        return name;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Callable<V> getCallable() {
        return callable;
    }

    public void setResult(V result) {
        this.result = result;
    }

    @Override
    public boolean isPeriodic() {
        return false;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delay,TimeUnit.SECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }

    public static class RunnableJob implements Job {

        public static String RUNNABLE_PROPERTY = "runnable";
        public static String RESULT_PROPERTY = "result";

        public RunnableJob() {
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Callable<?> rf = (Callable<?>) context.getMergedJobDataMap().get(RUNNABLE_PROPERTY);
            try {
                Object result = rf.call();
                QuartzRunnableFuture<Object> qrf = (QuartzRunnableFuture<Object>) context.getMergedJobDataMap().get(RESULT_PROPERTY);
                qrf.setResult(result);

            } catch (Exception e) {
                throw new JobExecutionException(e);
            }
        }
    }

}
