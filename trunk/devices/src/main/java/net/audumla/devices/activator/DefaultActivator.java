package net.audumla.devices.activator;

import net.audumla.devices.activator.factory.ActivatorFactory;
import org.apache.log4j.Logger;

import java.util.Properties;

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

//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:13 PM
 */
public class DefaultActivator<TFactory extends ActivatorFactory> implements Activator {

    private static final Logger logger = Logger.getLogger(Activator.class);
    public static final String ACTIVATOR_NAME = "activator_name";

    private ActivatorState state = ActivatorState.UNKNOWN;
    private Properties id = new Properties();
    private String name;
    private TFactory factory;
    private boolean setVariable = false;
    private boolean setState = true;

    public DefaultActivator(String name) {
        getId().setProperty(ACTIVATOR_NAME, name);
        setName(name);
    }

    public DefaultActivator(TFactory factory, String name) {
        this.factory = factory;
        getId().setProperty(ActivatorFactory.FACTORY_ID, factory.getId());
        getId().setProperty(ACTIVATOR_NAME, name);
        setName(name);
    }

    protected DefaultActivator() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void setActiveState(ActivatorState state) {
        this.state = state;
        logger.debug("[" + state + "]" + "[" + getName() + "]");
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
                    return true;
                } catch (Exception ex) {
                    logger.trace("Failed to set Activator [" + getName() + "] to state [" + newstate.getName() + "]", ex);
                    // we should attempt to deactivate the activator if we were not already attempting to do so
                    if (!newstate.equals(ActivatorState.DEACTIVATED)) {
                        // We will attempt to deactivate the activator to bring it into a known off state.
                        setActiveState(ActivatorState.UNKNOWN);    // set the state to unknown as we are currently in an undefined state until we either successfully enable or disable.
                        setState(ActivatorState.DEACTIVATED); // now we attempt to deactivate the activator by recursively calling this method.
                    }
                    throw ex;
                }
            }
            logger.trace("Ignored - [" + getState() + "][" + getName() + "] to state [" + newstate.getName() + "]");
        } else {
            throw new Exception("Activator [" + getName() + "] is not settable");
        }
        return false;
    }

    protected void executeStateChange(ActivatorState newstate) throws Exception {
        if (getFactory() != null) {
            getFactory().setState(this, newstate);
        } else {
            throw new Exception("Cannot change state for activator [" + getName() + "]");
        }
    }


    public TFactory getFactory() {
        return factory;
    }

    public void setFactory(TFactory factory) {
        this.factory = factory;
        getId().setProperty(ActivatorFactory.FACTORY_ID, factory.getId());
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
