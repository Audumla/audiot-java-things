package net.audumla.devices.activator;

import net.audumla.automate.event.*;
import net.audumla.bean.BeanUtils;
import org.apache.log4j.Logger;

import java.util.*;

//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 */
public abstract class AbstractActivator<TProvider extends ActivatorProvider, TEvent extends CommandEvent> extends AbstractEventTarget<TEvent> implements Activator<TProvider,TEvent> {

    private static final Logger logger = Logger.getLogger(Activator.class);
    private ActivatorState state = ActivatorState.UNKNOWN;
    private Properties id = new Properties();
    private TProvider provider;

    protected AbstractActivator(TProvider provider) {
        this.provider = provider;
    }

    protected AbstractActivator() {
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
    public boolean setCurrentState(ActivatorState newstate) {
        // To allow the activator provider to set all states atomically if part of a transaction, we wrap the provider as a transaction listener
        // The listener will then be called once all the activators have been executed and pass the Event/EventTarget map within the transaction
        // can then be used to determine the target state of each activator.


        if (!newstate.getValue().equals(getCurrentState().getValue())) {
            try {
                if (executeStateChange(newstate)) {

                    ActivatorState oldState = getCurrentState();
                    setActiveState(newstate);
                    getScheduler().publishEvent(new ActivatorStateChangeEvent(oldState, newstate, this)).begin();
                    return true;
                } else {
                    throw new Exception("Unknown state change failure");
                }
            } catch (Throwable ex) {
                // we should attempt to deactivate the activator if we were not already attempting to do so
                if (!newstate.equals(ActivatorState.DEACTIVATED)) {
                    // We will attempt to deactivate the activator to bring it into a known off state.
                    setActiveState(ActivatorState.UNKNOWN);    // set the state to unknown as we are currently in an undefined state until we either successfully enable or disable.
                    setCurrentState(ActivatorState.DEACTIVATED); // now we attempt to deactivate the activator by recursively calling this method.
                }
            }
        } else {
            logger.trace("Cannot set Activator [" + getName() + "] to state [" + newstate.getName() + "] when state is already [" + getCurrentState() + "]");
        }
        return false;
    }

    protected abstract boolean executeStateChange(ActivatorState newstate);

    @Override
    public boolean rollbackEvent(RollbackEvent<Activator> event) throws Throwable {
        return event.rollback(this);
    }

    @Override
    public boolean handleEvent(TEvent event) throws Exception {
        return event.execute(this);
    }

    @Override
    public TProvider getProvider() {
        return provider;
    }

    public void setProvider(TProvider provider) {
        this.provider = provider;
    }
}
