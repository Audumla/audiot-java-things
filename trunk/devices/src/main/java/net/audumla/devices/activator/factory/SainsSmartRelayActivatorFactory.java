package net.audumla.devices.activator.factory;

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

import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.activator.EventTransactionActivator;
import net.audumla.devices.activator.EventTransactionActivatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public class SainsSmartRelayActivatorFactory extends EventTransactionActivatorFactory<EventTransactionActivator> {
    private static final Logger logger = LoggerFactory.getLogger(SainsSmartRelayActivatorFactory.class);
    protected int relayCount = 8;

    protected SainsSmartRelayActivatorFactory(String id, int relayCount, Activator power, Activator[] relayActvators) {
        super(id);
        this.relayCount = relayCount;
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public EventTransactionActivator getActivator(Properties id) {
        return null;
    }

    @Override
    public Collection<? extends EventTransactionActivator> getActivators() {
        return null;
    }

    @Override
    public boolean setStates(Map<EventTransactionActivator, ActivatorState> newStates) throws Exception {
        return false;
    }

    @Override
    public boolean setState(EventTransactionActivator activator, ActivatorState newState) throws Exception {
        return false;
    }
}
