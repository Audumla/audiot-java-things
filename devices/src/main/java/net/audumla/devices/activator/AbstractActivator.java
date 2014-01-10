package net.audumla.devices.activator;

import net.audumla.bean.BeanUtils;
import net.audumla.devices.event.CommandEvent;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 */
public abstract class AbstractActivator implements Activator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private ActivateState state = ActivateState.UNKNOWN;
    private Deque<ActivatorListener> registeredListeners = new LinkedList<ActivatorListener>();
    private String name = BeanUtils.generateName(Activator.class);

    protected AbstractActivator() {
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
            if (listeners != null) {
                Arrays.asList(listeners).forEach(ls::addFirst);
            }
            ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATING, this)));
            try {
                if (doActivate(ls)) {
                    ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this)));
                    return true;
                } else {
                    if (getCurrentState() != ActivateState.UNKNOWN) {
                        ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this), null, "Unknown failure activating Activator [" + getName() + "]"));
                        deactivate(listeners);
                    }
                }
            } catch (Throwable ex) {
                ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.ACTIVATED, this), ex, "Unknown failure activating Activator [" + getName() + "]"));
                deactivate(listeners);
            }

        } else {
            logger.warn("Cannot activate Activator [" + getName() + "] when state is [" + getCurrentState() + "]");
        }
        return false;
    }

    protected abstract boolean doActivate(Collection<ActivatorListener> listeners);

    @Override
    public boolean deactivate(ActivatorListener... listeners) {

        if (getCurrentState() != ActivateState.DEACTIVATED && getCurrentState() != ActivateState.DEACTIVATING) {
            Deque<ActivatorListener> ls = new LinkedList<ActivatorListener>(registeredListeners);
            if (listeners != null) {
                Arrays.asList(listeners).forEach(ls::addFirst);
            }
            ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATING, this)));
            try {
                if (doDeactivate(ls)) {
                    ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this)));
                    return true;
                } else {
                    if (getCurrentState() != ActivateState.UNKNOWN) {
                        ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this), null, "Unknown failure deactivating Activator [" + getName() + "]"));
                    }
                }
            } catch (Throwable ex) {
                ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, ActivateState.DEACTIVATED, this), ex, "Unknown failure deactivating Activator [" + getName() + "]"));
            }
        } else {
            logger.warn("Cannot deactivate Activator [" + getName() + "] when state is [" + getCurrentState() + "]");
        }
        return false;
    }

    protected abstract boolean doDeactivate(Collection<ActivatorListener> listeners);


    @Override
    public void addListener(ActivatorListener listener) {
        registeredListeners.addFirst(listener);
    }

    @Override
    public void removeListener(ActivatorListener listener) {
        registeredListeners.remove(listener);
    }

    @Override
    public boolean handleEvent(CommandEvent<Activator> event) {
        return event.execute(this);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public static class ActivatorStateListener implements ActivatorListener {
        private static final Logger logger = Logger.getLogger(Activator.class);
        private final AbstractActivator activator;

        public ActivatorStateListener(AbstractActivator activatorAdaptor) {
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
