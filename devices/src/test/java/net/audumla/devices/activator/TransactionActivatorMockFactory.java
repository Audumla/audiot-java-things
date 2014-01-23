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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

public class TransactionActivatorMockFactory extends EventTransactionActivatorFactory<EventTransactionActivator> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionActivatorMockFactory.class);

    Collection<EventTransactionActivator<TransactionActivatorMockFactory, ActivatorCommand>> activators = new HashSet<EventTransactionActivator<TransactionActivatorMockFactory, ActivatorCommand>>();

    public TransactionActivatorMockFactory() {
        super("Mock Provider");
    }

    @Override
    public EventTransactionActivator getActivator(Properties id) {
        EventTransactionActivator<TransactionActivatorMockFactory, ActivatorCommand> a = new EventTransactionActivator<>(this);
//        a.allowSetState(true);
        if (id != null) {
            a.getId().putAll(id);
        }
        a.getId().setProperty("ID",a.getName());
        activators.add(a);
        return a;
    }

    @Override
    public Collection<? extends EventTransactionActivator> getActivators() {
        return activators;
    }

    @Override
    public boolean setState(EventTransactionActivator activator, ActivatorState newState) throws Exception {
        logger.info("Simulator " + newState.getName() + " - " + activator.getId());
        return true;
    }

}
