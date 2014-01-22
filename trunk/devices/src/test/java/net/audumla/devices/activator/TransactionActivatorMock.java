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

import org.apache.log4j.Logger;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 4:27 PM
 */
public class TransactionActivatorMock extends EventTargetActivator<TransactionActivatorMockProvider, SetActivatorStateCommand> {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private boolean activate;
    private boolean deactivate;

    public TransactionActivatorMock(TransactionActivatorMockProvider provider) {
        super(provider);
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public void setDeactivate(boolean deactivate) {
        this.deactivate = deactivate;
    }

    @Override
    protected void executeStateChange(ActivatorState newstate) throws Exception {
        if (newstate.equals(ActivatorState.DEACTIVATED)) {
            logger.info(deactivate ? "Simulator Deactivated - " + getName() : "Simulator Failed Deactivation - " + getName());
            if (!deactivate) {
                throw new Exception("Unsupported Failure");
            }
        } else {
            logger.info(activate ? "Simulator Activated - " + getName() : "Simulator Failed Activation - " + getName());
            if (!activate) {
                throw new Exception("Unsupported Failure");
            }
        }
    }

    @Override
    public boolean handleEvent(SetActivatorStateCommand event) throws Exception {
        event.getEventTransaction().addTransactionListener(getProvider());
        return true;
    }
}
