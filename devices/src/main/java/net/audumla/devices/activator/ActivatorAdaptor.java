package net.audumla.devices.activator;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 */
public abstract class ActivatorAdaptor implements Activator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private static long count;
    private Collection<ActivatorListener> listeners = new HashSet<ActivatorListener>();
    private String name = generateName();

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
    public boolean activate() {
        return activate(null);
    }

    @Override
    public boolean activate(ActivatorListener listener) {
        listeners.forEach(l -> l.activating(this));
        try {
            Collection<ActivatorListener> ls = new HashSet<ActivatorListener>(listeners);
            ls.add(listener);
            if (doActivate(ls)) {
                listeners.forEach(l -> l.activated(this));
                return true;
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return false;
    }

    protected abstract boolean doActivate(Collection<ActivatorListener> listeners);

    @Override
    public boolean deactivate() {
        return deactivate(null);
    }

    @Override
    public boolean deactivate(ActivatorListener listener) {
        listeners.forEach(l -> l.deactivating(this));
        try {
            Collection<ActivatorListener> ls = new HashSet<ActivatorListener>(listeners);
            ls.add(listener);
            if (doDeactivate(ls)) {
                listeners.forEach(l -> l.deactivated(this));
                return true;
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return false;
    }

    protected abstract boolean doDeactivate(Collection<ActivatorListener> listeners);

    @Override
    public boolean activate(long seconds, boolean block) {
        return activate(seconds, block, null);
    }

    @Override
    public boolean activate(long seconds, boolean block, ActivatorListener listener) {
        if (block) {
            try {
                if (activate(listener)) {
                    synchronized (this) {
                        this.wait(seconds * 1000);
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                logger.error(e);
                return false;
            } finally {
                deactivate(listener);
            }
        } else {
            try {
                activate(listener);
                addDeactivateTimeout(seconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) seconds, listener);
            } catch (Exception e) {
                logger.error(e);
                deactivate(listener);
                return false;
            }
        }
        return true;
    }

    protected void addDeactivateTimeout(int seconds, ActivatorListener listener) {
        try {
            JobDetail job = JobBuilder.newJob().withIdentity(getName(), "Group:" + getName()).build();

            job.getJobDataMap().put(ActivatorTimeoutJob.ACTIVATOR_PROPERTY, this);
            job.getJobDataMap().put(ActivatorTimeoutJob.ACTIVATOR_LISTENER_PROPERTY, listener);
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

    private static class ActivatorTimeoutJob implements Job {

        public static final String ACTIVATOR_PROPERTY = "activator";
        public static final String ACTIVATOR_LISTENER_PROPERTY = "listener";

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            Activator activator = (Activator) jobExecutionContext.getMergedJobDataMap().get(ACTIVATOR_PROPERTY);
            ActivatorListener listener = (ActivatorListener) jobExecutionContext.getMergedJobDataMap().get(ACTIVATOR_LISTENER_PROPERTY);
            activator.deactivate(listener);
        }
    }
}
