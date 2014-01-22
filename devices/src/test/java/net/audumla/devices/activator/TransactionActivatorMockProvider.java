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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

public class TransactionActivatorMockProvider implements ActivatorProvider<TransactionActivatorMock>, EventTransactionListener<SetActivatorStateCommand,EventTargetActivator>{
    private static final Logger logger = LoggerFactory.getLogger(TransactionActivatorMockProvider.class);

    Collection<TransactionActivatorMock> activators= new HashSet<>();

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public String getId() {
        return "MockProvider";
    }

    @Override
    public TransactionActivatorMock getActivator(Properties id) {
        TransactionActivatorMock a = new TransactionActivatorMock(this);
        activators.add(a);
        return a;
    }

    @Override
    public Collection<TransactionActivatorMock> getActivators() {
        return activators;
    }

    @Override
    public boolean setCurrentStates(Map<Activator, ActivatorState> newStates) throws Exception {
        return false;
    }

    @Override
    public boolean onTransactionCommit(EventTransaction transaction, Map<SetActivatorStateCommand, EventTargetActivator> events) throws Exception {
        for (Map.Entry<SetActivatorStateCommand, EventTargetActivator> e : events.entrySet()) {
            e.getValue().setActiveState(e.getKey().newState);
        }
        return true;
    }

    @Override
    public boolean onTransactionBegin(EventTransaction transaction) throws Exception {
        return true;
    }
}
