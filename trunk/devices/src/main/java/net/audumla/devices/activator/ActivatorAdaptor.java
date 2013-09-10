package net.audumla.devices.activator;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ActivatorAdaptor implements Activator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private static long count;
    private Collection<ActivatorListener> listeners = new HashSet<ActivatorListener>();

    private String name = generateName();

    private static class ActivatorTimeoutJob implements Job {

        public static final String ACTIVATOR_PROPERTY = "activator";

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            Activator activator = (Activator) jobExecutionContext.getMergedJobDataMap().get(ACTIVATOR_PROPERTY);
            activator.deactivate();
        }
    }

    private static String generateName() {
        if (count > (Long.MAX_VALUE - 1)) {
            count = 0;
        }
        return "Activator" + (++count);
    }

    public String getName() {
        return name;
    }

    @Override
    public void activate() {
        listeners.forEach(l -> l.activating(this));
        doActivate();
        listeners.forEach(l -> l.activated(this));
    }

    protected abstract void doActivate();

    @Override
    public void deactivate() {
        listeners.forEach(l -> l.deactivating(this));
        doDeactivate();
        listeners.forEach(l -> l.deactivated(this));
    }

    protected abstract void doDeactivate();

    @Override
    public void activate(int seconds, boolean block) {
        if (block) {
            try {
                activate();
                synchronized (this) {
                    this.wait(seconds * 1000);
                }
            } catch (InterruptedException e) {
                logger.error(e);
            } finally {
                deactivate();
            }
        } else {
            try {
                activate();
                addDeactivateTimeout(seconds);

            } catch (Exception e) {
                deactivate();
                logger.error(e);
            }

        }
    }

    protected void addDeactivateTimeout(int seconds) {
        try {
            JobDetail job = JobBuilder.newJob().withIdentity(getName(), "Group:" + getName()).build();

            job.getJobDataMap().put(ActivatorTimeoutJob.ACTIVATOR_PROPERTY, this);
            ScheduleBuilder builder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(1, seconds).withMisfireHandlingInstructionFireNow();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Deactivate -" + job.getKey().getName(), job.getKey().getGroup()).startNow()
                    .withSchedule(builder).build();

            StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            deactivate();
            logger.error(e);
        }
    }

    @Override
    public void addListener(ActivatorListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ActivatorListener listener) {
        listeners.remove(listener);
    }
}
