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

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;
import net.audumla.automate.event.RollbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivatorCommand extends AbstractEvent implements CommandEvent<Activator>, RollbackEvent<Activator> {
    private static final Logger logger = LoggerFactory.getLogger(ActivatorCommand.class);
    protected ActivatorState previousState;
    protected ActivatorState newState;

    public ActivatorCommand() {
    }

    public void setPreviousState(ActivatorState previousState) {
        this.previousState = previousState;
    }

    public void setNewState(ActivatorState newState) {
        this.newState = newState;
    }

    public ActivatorCommand(ActivatorState newState) {
        this.newState = newState;
    }

    @Override
    public boolean execute(Activator activator) throws Exception {
        previousState = activator.getState();
        return activator.setState(getNewState());
    }

    @Override
    public boolean rollback(Activator activator) throws Exception {
        return activator.setState(getPreviousState());
    }

    public ActivatorState getPreviousState() {
        return previousState;
    }

    public ActivatorState getNewState() {
        return newState;
    }

}
