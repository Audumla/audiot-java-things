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
import net.audumla.devices.activator.ActivatorCommand;
import net.audumla.devices.activator.EventTransactionActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPIGPIOActivator extends EventTransactionActivator<RPIGPIOActivatorFactory, ActivatorCommand> {
    private static final Logger logger = LoggerFactory.getLogger(RPIGPIOActivator.class);

    public static String GPIO_PIN = "gpio_pin";
    public static String GPIO_NAME = "gpio_name";

    protected int pin;
    protected int resistance = Gpio.PUD_OFF;

    public RPIGPIOActivator(int pin, RPIGPIOActivatorFactory.GPIOName name, RPIGPIOActivatorFactory rpigpioActivatorFactory) {
        super(rpigpioActivatorFactory);
        setPullResistance(resistance);
        this.pin = pin;
        setName(name.name());
        getId().setProperty(GPIO_PIN, String.valueOf(pin));
        getId().setProperty(GPIO_NAME, name.toString());
    }


    public void setPullResistance(int value) {
        resistance = value;
        com.pi4j.wiringpi.Gpio.pullUpDnControl(pin, value);
    }

    public int getPullResistance() {
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
                com.pi4j.wiringpi.Gpio.pinMode(pin, Gpio.PWM_OUTPUT);
            }
            else {
                com.pi4j.wiringpi.Gpio.pinMode(pin, Gpio.OUTPUT);
            }
        } else {
            com.pi4j.wiringpi.Gpio.pinMode(pin, Gpio.INPUT);
            // if this is an input pin, then configure edge detection
            com.pi4j.wiringpi.GpioUtil.setEdgeDetection(pin, PinEdge.BOTH.getValue());
        }
    }

    public int getPin() {
        return pin;
    }

}
