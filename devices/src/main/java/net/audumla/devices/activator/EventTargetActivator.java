package net.audumla.devices.activator;

import net.audumla.automate.event.AbstractEventTarget;
import net.audumla.automate.event.RollbackEvent;
import net.audumla.automate.event.RollbackEventTarget;
import net.audumla.devices.activator.factory.ActivatorFactory;
import org.apache.log4j.Logger;

import java.util.Properties;

//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 */
public abstract class EventTargetActivator<TFactory extends ActivatorFactory, TEvent extends ActivatorCommand> extends AbstractEventTarget<TEvent> implements RollbackEventTarget<RollbackEvent<Activator>>, Activator {

    private static final Logger logger = Logger.getLogger(Activator.class);
    private ActivatorState state = ActivatorState.UNKNOWN;
    private Properties id = new Properties();
    private TFactory factory;
    private boolean setVariable = false;
    private boolean setState = true;

    protected EventTargetActivator(TFactory factory) {
        this.factory = factory;
        getId().setProperty(ActivatorFactory.FACTORY_ID, factory.getId());
    }

    protected EventTargetActivator() {
    }

    public void setActiveState(ActivatorState state) {
        this.state = state;
    }

    @Override
    public ActivatorState getState() {
        return state;
    }

    @Override
    public Properties getId() {
        return id;
    }

    @Override
    public boolean setState(ActivatorState newstate) throws Exception {
        if (canSetState()) {
            if (!newstate.getValue().equals(getState().getValue())) {
                try {
                    executeStateChange(newstate);
                    ActivatorState oldState = getState();
                    setActiveState(newstate);
                    if (getScheduler() != null) {
                        getScheduler().publishEvent(new ActivatorStateChangeEvent(oldState, newstate, this)).begin();
                    }
                    return true;
                } catch (Exception ex) {
                    // we should attempt to deactivate the activator if we were not already attempting to do so
                    if (!newstate.equals(ActivatorState.DEACTIVATED)) {
                        // We will attempt to deactivate the activator to bring it into a known off state.
                        setActiveState(ActivatorState.UNKNOWN);    // set the state to unknown as we are currently in an undefined state until we either successfully enable or disable.
                        setState(ActivatorState.DEACTIVATED); // now we attempt to deactivate the activator by recursively calling this method.
                    }
                    throw ex;
                }
            }
            logger.trace("Cannot set Activator [" + getName() + "] to state [" + newstate.getName() + "] when state is already [" + getState() + "]");
        } else {
            throw new Exception("Activator [" + getName() + "] is not settable");
        }
        return false;
    }

    protected abstract void executeStateChange(ActivatorState newstate) throws Exception;

    @Override
    public boolean rollbackEvent(RollbackEvent<Activator> event) throws Throwable {
        return event.rollback(this);
    }

    public TFactory getFactory() {
        return factory;
    }

    @Override
    public void handleEvent(TEvent event) throws Throwable {
        setState(event.getNewState());
    }

    public void setFactory(TFactory factory) {
        this.factory = factory;
        getId().setProperty(ActivatorFactory.FACTORY_ID,factory.getId());
    }

    @Override
    public void allowSetState(boolean set) {
        this.setState = set;
    }

    @Override
    public boolean canSetState() {
        return setState;
    }

    @Override
    public void allowVariableState(boolean var) {
        setVariable = var;
    }

    @Override
    public boolean hasVariableState() {
        return setVariable;
    }
}
