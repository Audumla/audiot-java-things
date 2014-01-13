package net.audumla.devices.activator;

import net.audumla.bean.BeanUtils;
import net.audumla.devices.event.CommandEvent;
import org.apache.log4j.Logger;

import java.util.*;

//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 */
public abstract class AbstractActivator implements Activator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private ActivatorState state = ActivatorState.UNKNOWN;
    private Deque<ActivatorListener> registeredListeners = new LinkedList<ActivatorListener>();
    private String name = BeanUtils.generateName(Activator.class);
    private Properties id = new Properties();


    protected AbstractActivator() {
        registeredListeners.add(new ActivatorStateListener(this));
    }

    protected void setActiveState(ActivatorState state) {
        this.state = state;
    }

    @Override
    public ActivatorState getCurrentState() {
        return state;
    }

    @Override
    public Properties getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean setCurrentState(ActivatorState newstate, ActivatorListener... listeners) {
        if (!newstate.getValue().equals(getCurrentState().getValue())) {
            Deque<ActivatorListener> ls = new LinkedList<ActivatorListener>(registeredListeners);
            if (listeners != null) {
                Arrays.asList(listeners).forEach(ls::addFirst);
            }
            try {
                if (executeStateChange(newstate, ls)) {
                    ls.forEach(l -> l.onStateChange(new ActivatorStateChangeEvent(state, newstate, this)));
                    return true;
                } else {
                    throw new Exception("Unknown state change failure");
                }
            } catch (Throwable ex) {
                ls.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(state, newstate, this), null, "Unknown failure setting Activator [" + getName() + "] to [" + newstate.getName() + "]"));
                // we should attempt to deactivate the activator if we were not already attempting to do so
                if (!newstate.equals(ActivatorState.DEACTIVATED)) {
                    // We will attempt to deactivate the activator to bring it into a known off state.
                    setActiveState(ActivatorState.UNKNOWN);
                    setCurrentState(ActivatorState.DEACTIVATED, listeners);
                }
            }
        } else {
            logger.debug("Cannot set Activator [" + getName() + "] to state [" + newstate.getName() + "] when state is already [" + getCurrentState() + "]");
        }
        return false;
    }

    protected abstract boolean executeStateChange(ActivatorState newstate, Collection<ActivatorListener> listeners);

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
            activator.setActiveState(ActivatorState.UNKNOWN);
        }
    }

}
