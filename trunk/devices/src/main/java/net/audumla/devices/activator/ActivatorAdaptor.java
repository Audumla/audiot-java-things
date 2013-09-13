package net.audumla.devices.activator;

import net.audumla.bean.BeanUtils;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import static org.quartz.DateBuilder.futureDate;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 */
public abstract class ActivatorAdaptor implements Activator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private ActivateState state = ActivateState.UNKNOWN;
    private Deque<ActivatorListener> registeredListeners = new LinkedList<ActivatorListener>();
    private String name = BeanUtils.generateName(Activator.class);

    protected ActivatorAdaptor() {
        registeredListeners.add(new ActivatorStateListener(this));
    }

    protected void setActiveState(ActivateState state) {
        this.state = state;
    }

    @Override
    public ActivateState getCurrentState() {
        return state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean activate(ActivatorListener... listeners) {
        if (getCurrentState() != ActivateState.ACTIVATED && getCurrentState() != ActivateState.ACTIVATING) {
            Deque<ActivatorListener> ls = new LinkedList<ActivatorListener>(registeredListeners);
            Arrays.asList(listeners).forEach(ls::addFirst);
            ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATING, this)));
            try {
                if (doActivate(ls)) {
                    ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this)));
                    return true;
                } else {
                    if (getCurrentState() != ActivateState.UNKNOWN) {
                        ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this), null, "Unknown Activator failure"));
                        deactivate(listeners);
                    }
                }
            } catch (Exception ex) {
                ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this), ex, "Unknown Activator failure"));
                deactivate(listeners);
            }

        }
        else {
            logger.warn("Cannot activate Activator ["+getName()+"] when state is ["+getCurrentState()+"]");
        }
        return false;
    }

    protected abstract boolean doActivate(Collection<ActivatorListener> listeners);

    @Override
    public boolean deactivate(ActivatorListener... listeners) {
        if (getCurrentState() != ActivateState.DEACTIVATED && getCurrentState() != ActivateState.DEACTIVATING) {
            Deque<ActivatorListener> ls = new LinkedList<ActivatorListener>(registeredListeners);
            Arrays.asList(listeners).forEach(l -> ls.addFirst(l));
            ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATING, this)));
            try {
                if (doDeactivate(ls)) {
                    ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this)));
                    return true;
                } else {
                    if (getCurrentState() != ActivateState.UNKNOWN) {
                        ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this), null, "Unknown Activator failure"));
                    }
                }
            } catch (Exception ex) {
                ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this), ex, "Unknown Activator failure"));
            }
        }
        else {
            logger.warn("Cannot deactivate Activator ["+getName()+"] when state is ["+getCurrentState()+"]");
        }
        return false;
    }

    protected abstract boolean doDeactivate(Collection<ActivatorListener> listeners);

    @Override
    public boolean activate(long seconds, boolean block, ActivatorListener... listeners) {
        if (getCurrentState() != ActivateState.ACTIVATED) {
            if (block) {
                try {
                    if (activate(listeners)) {
                        synchronized (this) {
                            this.wait(seconds * 1000);
                        }
                        deactivate(listeners);
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    logger.error(e);
                    return false;
                }
            } else {
                try {
                    if (activate(listeners)) {
                        addDeactivateTimeout(seconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) seconds, listeners);
                    }
                } catch (Exception e) {
                    logger.error(e);
                    deactivate(listeners);
                    return false;
                }
            }
        }
        return true;
    }

    protected void addDeactivateTimeout(int seconds, ActivatorListener... listeners) {
        try {
            JobDetail job = JobBuilder.newJob(ActivatorTimeoutJob.class).withIdentity(getName(), "Group:" + getName()).build();

            job.getJobDataMap().put(ActivatorTimeoutJob.ACTIVATOR_PROPERTY, this);
            job.getJobDataMap().put(ActivatorTimeoutJob.ACTIVATOR_LISTENER_PROPERTY, listeners);
            ScheduleBuilder builder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(1).withMisfireHandlingInstructionFireNow();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Deactivate -" + job.getKey().getName(), job.getKey().getGroup()).startAt(futureDate(seconds, DateBuilder.IntervalUnit.SECOND))
                    .withSchedule(builder).build();

            StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            deactivate();
            logger.error(e);
        }
    }

    @Override
    public void addListener(ActivatorListener listener) {
        registeredListeners.addFirst(listener);
    }

    @Override
    public void removeListener(ActivatorListener listener) {
        registeredListeners.remove(listener);
    }

    public static class ActivatorTimeoutJob implements Job {

        public static final String ACTIVATOR_PROPERTY = "activator";
        public static final String ACTIVATOR_LISTENER_PROPERTY = "listener";

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            Activator activator = (Activator) jobExecutionContext.getMergedJobDataMap().get(ACTIVATOR_PROPERTY);
            ActivatorListener listener[] = (ActivatorListener[]) jobExecutionContext.getMergedJobDataMap().get(ACTIVATOR_LISTENER_PROPERTY);
            activator.deactivate(listener);
        }
    }

    public static class ActivatorStateListener implements ActivatorListener {
        private static final Logger logger = Logger.getLogger(Activator.class);
        private final ActivatorAdaptor activator;

        public ActivatorStateListener(ActivatorAdaptor activatorAdaptor) {
            activator = activatorAdaptor;
        }

        @Override
        public void onStateChange(ActivatorStateChangeEvent event) {
            logger.trace("Changed [" + event.getActivator().getName() + "] from " + event.getOldState() + " to " + event.getNewState());
            activator.setActiveState(event.getNewState());
        }

        @Override
        public void onStateChangeFailure(ActivatorStateChangeEvent event, Exception ex, String message) {
            logger.trace("Failed changing [" + event.getActivator().getName() + "] from " + event.getOldState() + " to " + event.getNewState());
            logger.error(message, ex);
            activator.setActiveState(ActivateState.UNKNOWN);
        }
    }

}
