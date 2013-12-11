package net.audumla.devices.activator;

import net.audumla.bean.BeanUtils;
import org.apache.log4j.Logger;
//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
                        ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this), null, "Unknown failure activating Activator ["+getName()+"]"));
                        deactivate(listeners);
                    }
                }
            } catch (Throwable ex) {
                ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this), ex, "Unknown failure activating Activator ["+getName()+"]"));
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
            Arrays.asList(listeners).forEach(ls::addFirst);
            ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATING, this)));
            try {
                if (doDeactivate(ls)) {
                    ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this)));
                    return true;
                } else {
                    if (getCurrentState() != ActivateState.UNKNOWN) {
                        ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this), null, "Unknown failure deactivating Activator ["+getName()+"]"));
                    }
                }
            } catch (Throwable ex) {
                ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this), ex, "Unknown failure deactivating Activator ["+getName()+"]"));
            }
        }
        else {
            logger.warn("Cannot deactivate Activator ["+getName()+"] when state is ["+getCurrentState()+"]");
        }
        return false;
    }

    protected abstract boolean doDeactivate(Collection<ActivatorListener> listeners);

//    @Override
//    public boolean activate(long seconds, boolean block, ActivatorListener... listeners) {
//        if (getCurrentState() != ActivateState.ACTIVATED) {
//            if (block) {
//                try {
//                    if (activate(listeners)) {
//                        synchronized (this) {
//                            this.wait(seconds);
//                        }
//                        deactivate(listeners);
//                    } else {
//                        return false;
//                    }
//                } catch (Exception e) {
//                    logger.error(e);
//                    return false;
//                }
//            } else {
//                try {
//                    if (activate(listeners)) {
//                        addDeactivateTimeout(seconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) seconds, listeners);
//                    }
//                    else {
//                        return false;
//                    }
//                } catch (Exception e) {
//                    logger.error(e);
//                    deactivate(listeners);
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

//    protected void addDeactivateTimeout(int seconds, ScheduledExecutorService service, ActivatorListener... listeners) {
//        service.schedule(new DeactivateCall(this,listeners),seconds, TimeUnit.SECONDS);
//    }

    @Override
    public void addListener(ActivatorListener listener) {
        registeredListeners.addFirst(listener);
    }

    @Override
    public void removeListener(ActivatorListener listener) {
        registeredListeners.remove(listener);
    }

    public static class DeactivateCall implements Runnable {

        protected Activator activator;
        protected ActivatorListener listener[];

        public DeactivateCall(Activator activator, ActivatorListener[] listener) {
            this.activator = activator;
            this.listener = listener;
        }

        @Override
        public void run() {
            activator.deactivate(listener);
        }
    }

//    public static class ActivatorTimeoutJob implements Job {
//
//        public static final String ACTIVATOR_PROPERTY = "activator";
//        public static final String ACTIVATOR_LISTENER_PROPERTY = "listener";
//
//        @Override
//        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//            Activator activator = (Activator) jobExecutionContext.getMergedJobDataMap().get(ACTIVATOR_PROPERTY);
//            ActivatorListener listener[] = (ActivatorListener[]) jobExecutionContext.getMergedJobDataMap().get(ACTIVATOR_LISTENER_PROPERTY);
//            activator.deactivate(listener);
//        }
//    }

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
        public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
            logger.trace("Failed changing [" + event.getActivator().getName() + "] from " + event.getOldState() + " to " + event.getNewState());
            logger.error(message, ex);
            activator.setActiveState(ActivateState.UNKNOWN);
        }
    }

}
