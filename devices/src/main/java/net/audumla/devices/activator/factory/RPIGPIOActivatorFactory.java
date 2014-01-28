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

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.wiringpi.Gpio;
import net.audumla.devices.activator.DefaultActivator;
import net.audumla.devices.activator.ActivatorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class RPIGPIOActivatorFactory implements ActivatorFactory<RPIGPIOActivatorFactory.RPIGPIOActivator> {
    private static final Logger logger = LoggerFactory.getLogger(RPIGPIOActivatorFactory.class);
    private final String id;

    protected ArrayList<RPIGPIOActivator> activators = new ArrayList<>();

    // the pin names and assignments are based on
    // http://wiringpi.com/wp-content/uploads/2013/03/gpio1.png
    // http://wiringpi.com/wp-content/uploads/2013/03/gpio21.png
    public enum GPIOName {
        I2C_SDA, I2C_SCL, GPIO7, SPI_CE1, SPI_CE0, SPI_MISO, SPI_MOSI, SPI_SCLK, TxD, RxD, GPIO0, GPIO1, GPIO2, GPIO3, GPIO4, GPIO5, GPIO6, GPIO8, GPIO9, GPIO10, GPIO11
    }

    protected static int[][] GPIOPinRevisions = {
            {0, 1, 4, 7, 8, 9, 10, 11, 14, 15, 17, 18, 21, 22, 23, 24, 25},
            {2, 3, 4, 7, 8, 9, 10, 11, 14, 15, 17, 18, 27, 22, 23, 24, 25, 28, 29, 30, 31}};

    public RPIGPIOActivatorFactory() {
        id = "RaspberryPI BCM2835 GPIO";
    }

    @Override
    public void initialize() throws Exception {
        com.pi4j.wiringpi.Gpio.wiringPiSetupGpio();


        int revisionIndex = Gpio.piBoardRev() - 1;
        if (revisionIndex < 2) {
            logger.info("Identified RaspberryPI revision - " + Gpio.piBoardRev());
            for (int i = 0; i < GPIOPinRevisions[revisionIndex].length; ++i) {
                RPIGPIOActivator a = new RPIGPIOActivator(GPIOPinRevisions[revisionIndex][i], GPIOName.values()[i], this);
                logger.debug("Found "+a.getName());
                if (a.getName().startsWith("GPIO")) {
                    //set all the GPIO pins to output and disasble.
                    a.allowSetState(true);
                    a.allowVariableState(false);
                    a.setState(ActivatorState.DEACTIVATED);
                }
                activators.add(a);
            }

        } else {
            logger.error("Unknown RaspberryPI revision - " + Gpio.piBoardRev());
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

    @Override
    public RPIGPIOActivator getActivator(Properties id) {
        int index = Integer.parseInt(id.getProperty(RPIGPIOActivator.GPIO_PIN));
        return activators.get(index);
    }

    @Override
    public Collection<? extends RPIGPIOActivator> getActivators() {
        return activators;
    }

    @Override
    public boolean setState(RPIGPIOActivator activator, ActivatorState newState) throws Exception {
        com.pi4j.wiringpi.Gpio.digitalWrite(activator.getPin(), newState.equals(ActivatorState.DEACTIVATED) ? Gpio.LOW : Gpio.HIGH);
        return true;
    }

    public RPIGPIOActivator getActivator(GPIOName name) {
        for (RPIGPIOActivator a : activators) {
            if (a.getId().getProperty(RPIGPIOActivator.GPIO_NAME).equals(name.toString())) {
                return a;
            }
        }
        return null;
    }

    public static class RPIGPIOActivator extends DefaultActivator<RPIGPIOActivatorFactory> {
        private static final Logger logger = LoggerFactory.getLogger(RPIGPIOActivator.class);

        public static String GPIO_PIN = "gpio_pin";
        public static String GPIO_NAME = "gpio_name";

        public enum PULL_RESISTANCE {PULL_UP, PULL_DOWN, PULL_OFF}

        protected int pin;
        protected PULL_RESISTANCE resistance = PULL_RESISTANCE.PULL_OFF;

        public RPIGPIOActivator(int pin, GPIOName name, RPIGPIOActivatorFactory factory) {
            super(factory,"GPIO : Pin#" + pin + " : " + name.name() + " : " +factory.getId());
            setPullResistance(resistance);
            this.pin = pin;
            getId().setProperty(GPIO_PIN, String.valueOf(pin));
            getId().setProperty(GPIO_NAME, name.toString());
        }


        public void setPullResistance(PULL_RESISTANCE value) {
            resistance = value;
            Gpio.pullUpDnControl(pin, value.ordinal());
        }

        public PULL_RESISTANCE getPullResistance() {
            return resistance;
        }

        @Override
        public void allowSetState(boolean set) {
            super.allowSetState(set);
            setMode();
        }

        @Override
        public void allowVariableState(boolean var) {
            super.allowVariableState(var);
            setMode();
        }

        protected void setMode() {
            if (canSetState()) {
                if (hasVariableState()) {
                    Gpio.pinMode(pin, Gpio.PWM_OUTPUT);
                } else {
                    Gpio.pinMode(pin, Gpio.OUTPUT);
                }
            } else {
                Gpio.pinMode(pin, Gpio.INPUT);
                // if this is an input pin, then configure edge detection
                com.pi4j.wiringpi.GpioUtil.setEdgeDetection(pin, PinEdge.BOTH.getValue());
            }
        }

        public int getPin() {
            return pin;
        }

    }
}
