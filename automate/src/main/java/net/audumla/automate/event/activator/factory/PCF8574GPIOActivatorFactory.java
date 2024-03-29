package net.audumla.automate.event.activator.factory;

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

import net.audumla.automate.event.activator.ActivatorCommand;
import net.audumla.automate.event.activator.EventTransactionActivator;
import net.audumla.automate.event.activator.EventTransactionActivatorFactory;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.io.i2c.I2CDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Properties;

public class PCF8574GPIOActivatorFactory extends EventTransactionActivatorFactory<PCF8574GPIOActivatorFactory.PCF8547GPIOActivator> {
    private static final Logger logger = LoggerFactory.getLogger(PCF8574GPIOActivatorFactory.class);

    //these addresses belong to PCF8574(P)
    public static final int PCF8574_0x20 = 0x20; // 000
    public static final int PCF8574_0x21 = 0x21; // 001
    public static final int PCF8574_0x22 = 0x22; // 010
    public static final int PCF8574_0x23 = 0x23; // 011
    public static final int PCF8574_0x24 = 0x24; // 100
    public static final int PCF8574_0x25 = 0x25; // 101
    public static final int PCF8574_0x26 = 0x26; // 110
    public static final int PCF8574_0x27 = 0x27; // 111
    //these addresses belong to PCF8574A(P)
    public static final int PCF8574A_0x38 = 0x38; // 000
    public static final int PCF8574A_0x39 = 0x39; // 001
    public static final int PCF8574A_0x3A = 0x3A; // 010
    public static final int PCF8574A_0x3B = 0x3B; // 011
    public static final int PCF8574A_0x3C = 0x3C; // 100
    public static final int PCF8574A_0x3D = 0x3D; // 101
    public static final int PCF8574A_0x3E = 0x3E; // 110
    public static final int PCF8574A_0x3F = 0x3F; // 111

    protected static final int PCF8574_WRITE = 0x40;
    protected static final int PCF8574_READ = 0x41;

    public static final int PCF8574_MAX_IO_PINS = 8;

    private I2CDevice device;
    private BitSet currentStates = new BitSet(PCF8574_MAX_IO_PINS);
    private Collection<PCF8547GPIOActivator> pins = new ArrayList<>();

    public PCF8574GPIOActivatorFactory(I2CDevice device) throws IOException {
        super("PCF8674 GPIO Expander connected on "+device.toString());
        this.device = device;

        // set all default pin cache states to match documented chip power up states
        for (int i = 0; i < PCF8574_MAX_IO_PINS; ++i) {
            pins.add(new PCF8547GPIOActivator(i, this));
        }
    }

    @Override
    public PCF8547GPIOActivator getActivator(Properties id) {
        for (PCF8547GPIOActivator a : getActivators()) {
            if (a.getId().getProperty(PCF8547GPIOActivator.GPIO_PIN).equals(id.getProperty(PCF8547GPIOActivator.GPIO_PIN))) {
                return a;
            }
        }
        return null;
    }

    @Override
    public Collection<PCF8547GPIOActivator> getActivators() {
        return pins;
    }

    @Override
    public boolean setState(PCF8547GPIOActivator activator, ActivatorState newState) throws Exception {
        // set state value for pin bit
        if (currentStates.get(activator.getPin()) == newState.equals(ActivatorState.DEACTIVATED)) {
            currentStates.set(activator.getPin(), !newState.equals(ActivatorState.DEACTIVATED));
            if (currentStates.toByteArray().length == 0) {
                device.write(PCF8574_WRITE, (byte) 0x00);
            } else {
                device.write(PCF8574_WRITE, currentStates.toByteArray()[0]);
            }
        }
//        else {
//            logger.trace("PCF8547 GPIO pin #"+activator.getPin()+" is already "+ newState );
//        }
        return true;
    }

    @Override
    public void initialize() throws Exception {
        currentStates.set(0, PCF8574_MAX_IO_PINS, true);
        device.write(PCF8574_WRITE, (byte) 0xff);
        for (PCF8547GPIOActivator a : getActivators()) {
            a.setState(ActivatorState.DEACTIVATED);
        }
    }

    @Override
    public void shutdown() throws Exception {
        for (PCF8547GPIOActivator a : getActivators()) {
            a.setState(ActivatorState.DEACTIVATED);
        }
    }

    public class PCF8547GPIOActivator extends EventTransactionActivator<PCF8574GPIOActivatorFactory, ActivatorCommand> {

        public static final String GPIO_PIN = "gpio_pin";

        protected int pin;

        public PCF8547GPIOActivator(int pin, PCF8574GPIOActivatorFactory activatorFactory) {
            super(activatorFactory);
            this.pin = pin;
            getId().setProperty(GPIO_PIN, String.valueOf(pin));
            super.allowVariableState(false);
            setName("GPIO Pin#" + pin + " on " + PCF8574GPIOActivatorFactory.this.toString() );
        }

        @Override
        public void allowSetState(boolean set) {
            try {
                super.allowSetState(set);
                if (!set) {
                    setState(ActivatorState.DEACTIVATED);
                }
            } catch (Exception e) {
                logger.error("Cannot set activator as input", e);
            }

        }

        @Override
        public void allowVariableState(boolean var) {
            // cannot handle analog i/o
        }

        public int getPin() {
            return pin;
        }

    }

}