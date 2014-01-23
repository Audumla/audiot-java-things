package net.audumla.devices.activator;

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

import net.audumla.automate.event.EventTransaction;
import net.audumla.automate.event.EventTransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class EventTransactionActivatorProvider<TActivator extends EventTransactionActivator> implements ActivatorProvider<TActivator>, EventTransactionListener<ActivatorCommand, TActivator> {
    private static final Logger logger = LoggerFactory.getLogger(EventTransactionActivatorProvider.class);

    private String id;

    protected EventTransactionActivatorProvider(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean onTransactionBegin(EventTransaction transaction) throws Exception {
        return true;
    }

    @Override
    public boolean onTransactionCommit(EventTransaction transaction, Map<TActivator, ActivatorCommand> events) throws Exception {
        for (Map.Entry<TActivator, ActivatorCommand> e : events.entrySet()) {
            e.getKey().setActiveState(e.getValue().getNewState());
        }
        return true;
    }

    @Override
    public boolean setState(TActivator activator, ActivatorState newstate) throws Exception {
        if (!newstate.getValue().equals(activator.getState().getValue())) {
            try {
                activator.executeStateChange(newstate);
                ActivatorState oldState = activator.getState();
                activator.setActiveState(newstate);
                activator.getScheduler().publishEvent(new ActivatorStateChangeEvent(oldState, newstate, activator)).begin();
                return true;
            } catch (Exception ex) {
                // we should attempt to deactivate the activator if we were not already attempting to do so
                if (!newstate.equals(ActivatorState.DEACTIVATED)) {
                    // We will attempt to deactivate the activator to bring it into a known off state.
                    activator.setActiveState(ActivatorState.UNKNOWN);    // set the state to unknown as we are currently in an undefined state until we either successfully enable or disable.
                    activator.setState(ActivatorState.DEACTIVATED); // now we attempt to deactivate the activator by recursively calling this method.
                }
            }
        }
        logger.trace("Cannot set Activator [" + activator.getName() + "] to state [" + newstate.getName() + "] when state is already [" + activator.getState() + "]");
        return false;
    }
}
