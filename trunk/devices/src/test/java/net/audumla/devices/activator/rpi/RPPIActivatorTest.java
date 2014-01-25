package net.audumla.devices.activator.rpi;

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
import net.audumla.devices.activator.AggregateActivator;
import net.audumla.devices.activator.factory.PCF8574GPIOActivatorFactory;
import net.audumla.devices.activator.factory.RPIGPIOActivatorFactory;
import net.audumla.devices.activator.factory.SainsSmartRelayActivatorFactory;
import net.audumla.devices.i2c.I2CDevice;
import net.audumla.devices.i2c.rpi.RPII2CBusFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class RPPIActivatorTest {
    private static final Logger logger = LoggerFactory.getLogger(RPPIActivatorTest.class);

    static RPIGPIOActivatorFactory rpi = new RPIGPIOActivatorFactory();
    static PCF8574GPIOActivatorFactory power5v;

    static {
        try {
            I2CDevice d = new RPII2CBusFactory().getInstance(1).getDevice(0x27);
            power5v = new PCF8574GPIOActivatorFactory(d);
            power5v.initialize();
            for (Activator a : power5v.getActivators()) {
                a.setState(ActivatorState.ACTIVATED);
            }

        } catch (Exception e) {
            logger.error("Failed to initialize power5v", e);
        }
    }

    static {
        try {
            rpi.initialize();
        } catch (Exception e) {
            logger.error("Cannot initialize PI", e);
        }
    }


    Activator getPower(int pin1_5v, int pin2_5v, Activator pin3_33v) throws Exception {
        AggregateActivator aa = new AggregateActivator();
        Properties id = new Properties();
        id.setProperty(PCF8574GPIOActivatorFactory.PCF8547GPIOActivator.GPIO_PIN, String.valueOf(pin1_5v));
        aa.addActivator(power5v.getActivator(id));
        id.setProperty(PCF8574GPIOActivatorFactory.PCF8547GPIOActivator.GPIO_PIN, String.valueOf(pin2_5v));
        aa.addActivator(power5v.getActivator(id));
        aa.addActivator(pin3_33v);
        return aa;
    }

    @Test
    public void testGPIOPins() throws Exception {
        assert rpi.getActivators().size() > 0;
        for (RPIGPIOActivatorFactory.RPIGPIOActivator a : rpi.getActivators()) {
            RPIGPIOActivatorFactory.GPIOName.valueOf(a.getName());
        }
    }

    @Test
    public void testRawRelay() throws Exception {
        Collection<Activator> pins = new ArrayList<>();
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO0));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO2));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO3));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_MOSI));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_SCLK));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_MISO));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_CE0));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_CE1));

        Activator power = rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6);
//        Activator power5v = getPower(2, 3, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6));
        power.allowVariableState(false);
        power.allowSetState(true);
        power.setState(ActivatorState.DEACTIVATED);

        for (Activator a : pins) {
            assert a != null;
            a.allowVariableState(false);
            a.allowSetState(true);
            a.setState(ActivatorState.ACTIVATED);
        }

        power.setState(ActivatorState.ACTIVATED);


        for (int i = 0; i < 10; ++i) {
            for (Activator a : pins) {
                a.setState(ActivatorState.DEACTIVATED);
                synchronized (this) {
                    wait(120);
                }
                a.setState(ActivatorState.ACTIVATED);

            }
        }
        power.setState(ActivatorState.DEACTIVATED);

    }

    @Test
    public void testSainsSmartRelayFromRPIGPIO() throws Exception {
        Collection<Activator> pins = new ArrayList<>();
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO0));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO2));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO3));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_MOSI));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_SCLK));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_MISO));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_CE0));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_CE1));

        Activator power = rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6);
//        Activator power5v = getPower(2, 3, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6));

        SainsSmartRelayActivatorFactory ss = new SainsSmartRelayActivatorFactory(pins, power);
        ss.initialize();

        int v = 0;
        for (int i = 0; i < 10; ++i) {
            for (Activator a : ss.getActivators()) {
                a.setState(ActivatorState.ACTIVATED);
                synchronized (this) {
                    wait(20 + v);
                }
                a.setState(ActivatorState.DEACTIVATED);
                v += 2;
            }
        }
        ss.shutdown();

    }

    @Test
    public void testSainsSmartRelayFromPCF8574() throws Exception {

        I2CDevice d = new RPII2CBusFactory().getInstance(1).getDevice(PCF8574GPIOActivatorFactory.PCF8574_0x21);
        PCF8574GPIOActivatorFactory gpio = new PCF8574GPIOActivatorFactory(d);
        gpio.initialize();
        Activator power = rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1);
//        Activator power5v = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));

        SainsSmartRelayActivatorFactory ss = new SainsSmartRelayActivatorFactory(gpio.getActivators(), power);
        ss.initialize();

        int v = 0;
        for (int i = 0; i < 10; ++i) {
            for (Activator a : ss.getActivators()) {
                a.setState(ActivatorState.ACTIVATED);
                synchronized (this) {
                    wait(200 + v);
                }
                a.setState(ActivatorState.DEACTIVATED);
                v += 2;
            }
        }
        ss.shutdown();

    }
}
