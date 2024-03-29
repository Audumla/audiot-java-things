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

import net.audumla.devices.activator.DefaultActivator;
import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class SainsSmartRelayActivatorFactory implements ActivatorFactory<SainsSmartRelayActivatorFactory.SainsSmartRelayActivator> {
    private static final Logger logger = LoggerFactory.getLogger(SainsSmartRelayActivatorFactory.class);
    private final Collection<SainsSmartRelayActivator> relays = new ArrayList<>();
    private final Activator power;
    private String id;

    public class SainsSmartRelayActivator extends DefaultActivator<SainsSmartRelayActivatorFactory> {

        public final static String RELAY_ID = "relayid";

        protected Activator sourcePin;

        public SainsSmartRelayActivator(SainsSmartRelayActivatorFactory factory, Activator sourcePin, int relayid) {
            super(factory, "Relay #" + relayid + " on " + factory.getId());
            this.sourcePin = sourcePin;
            super.allowVariableState(false);
            super.allowSetState(true);
            sourcePin.allowVariableState(false);
            sourcePin.allowSetState(true);
            getId().setProperty(RELAY_ID, String.valueOf(relayid));
        }

        @Override
        protected void executeStateChange(ActivatorState newstate) throws Exception {

//            setPower(false);
            // we need to set the underlying pin to the opposite of the relay state as the device is on when the pin is low, and off when the pin is high
            ActivatorState pinState = newstate.equals(ActivatorState.DEACTIVATED) ? ActivatorState.ACTIVATED : ActivatorState.DEACTIVATED;
            sourcePin.setState(pinState);
//            setPower(true);
        }

        @Override
        public void allowVariableState(boolean var) {
            // cannot handle variable state
        }

        @Override
        public void allowSetState(boolean set) {
            // is only output
        }

    }

    public SainsSmartRelayActivatorFactory(Collection<? extends Activator> sourcePins) {
        this(sourcePins, null);
    }

    public SainsSmartRelayActivatorFactory(Collection<? extends Activator> sourcePins, Activator power) {
        id = "SainsSmart " + sourcePins.size() + " port relay board";
        this.power = power;
        try {
            if (power != null) {
                power.allowVariableState(false);
                power.allowSetState(true);
                setPower(false);
            }
            int i = 0;
            for (Activator a : sourcePins) {
                logger.debug("Associating SainsSmart Relay #" + i + " to " + a.getName());
                relays.add(new SainsSmartRelayActivator(this, a, i++));
            }
        } catch (Exception e) {
            logger.error("Unable to configure source pins for SainsSmart Relay board", e);
        }
    }

    protected void setPower(boolean powerOn) throws Exception {
        if (power != null) {
            logger.debug("[Power " + (powerOn ? "ON" : "OFF") + "][" + getId() + "]");
            ActivatorState pinState = powerOn ? ActivatorState.ACTIVATED : ActivatorState.DEACTIVATED;
            power.setState(pinState);
        } else {
            logger.debug("Direct power connected to [" + getId() + "] cannot be turned on or off");
        }
    }

    @Override
    public void initialize() throws Exception {
        setPower(false);
        for (Activator a : relays) {
            a.setState(ActivatorState.DEACTIVATED);
        }
        setPower(true);
    }

    @Override
    public void shutdown() throws Exception {
        setPower(false);
        for (Activator a : relays) {
            a.setState(ActivatorState.DEACTIVATED);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SainsSmartRelayActivator getActivator(Properties id) {
        for (SainsSmartRelayActivator a : relays) {
            if (a.getId().get(SainsSmartRelayActivator.RELAY_ID).equals(id.get(SainsSmartRelayActivator.RELAY_ID))) {
                return a;
            }
        }
        return null;
    }

    @Override
    public Collection<? extends SainsSmartRelayActivator> getActivators() {
        return relays;
    }

    @Override
    public boolean setState(SainsSmartRelayActivator activator, ActivatorState newState) throws Exception {
        // as the sainssmart activator has overridden the executeStateChange method we can just call back to the activator and will propogate the
        // state to its underlying pin
        return activator.setState(newState);
    }

}
