package net.audumla.deviceaccess.i2cbus;

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

import net.audumla.deviceaccess.PeripheralChannel;
import net.audumla.deviceaccess.PeripheralManager;
import net.audumla.deviceaccess.i2cbus.rpi.RPiI2CPeripheralProvider;
import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.activator.AggregateActivator;
import net.audumla.devices.activator.factory.PCF8574GPIOActivatorFactory;
import net.audumla.devices.activator.factory.RPIGPIOActivatorFactory;
import net.audumla.devices.io.channel.ChannelAddressAttr;
import net.audumla.devices.io.channel.DeviceAddressAttr;
import net.audumla.devices.io.channel.DeviceChannel;
import net.audumla.devices.io.channel.I2CDeviceChannel;
import net.audumla.devices.io.i2c.RPiI2CDeviceFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Properties;

public class RPiI2cTest {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2cTest.class);

    static PCF8574GPIOActivatorFactory power5v;
    static RPIGPIOActivatorFactory rpi = new RPIGPIOActivatorFactory();

    static {
        try {
            rpi.initialize();
        } catch (Exception e) {
            logger.error("Cannot initialize PI", e);
        }
    }

    static {
        try {
            DeviceChannel d = new I2CDeviceChannel(new RPiI2CDeviceFactory()).createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(0x27));
            power5v = new PCF8574GPIOActivatorFactory(d);
            power5v.initialize();
        } catch (Exception e) {
            logger.error("Failed to initialize power5v", e);
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

    public I2CDevice createI2CDevice() throws Exception {
        I2CDeviceConfig c = new I2CDeviceConfig(1, 0x21);
        RPiI2CPeripheralProvider provider = new RPiI2CPeripheralProvider();
        I2CDevice device = provider.open(c, null, PeripheralManager.SHARED);
        assert device != null;
        return device;
    }

    @Test
    public void PCF8574readwrite8() throws Exception {
        PeripheralChannel d = createI2CDevice().getChannel();
        byte val = (byte) 0x01;
        d.write(val);
        Assert.assertEquals(val, d.read());
        d.write((byte) ~val);
        Assert.assertEquals(~val, d.read());
    }

    @Test
    public void PCF8574RWMask8() throws Exception {
        PeripheralChannel d = createI2CDevice().getChannel();
        d.write((byte) 0x00);
        byte val = (byte) 0xff;
        d.setMask(0x0f);
        d.write(val);
        Assert.assertEquals(0x0f, d.read());
        d.setMask(0xff);
        d.write((byte) ~val);
        Assert.assertEquals(~val & 0x0f, d.read());
    }

    @Test
    public void testSainsSmartRelayFromPCF8574StreamMask() throws Exception {
        synchronized (this) {
            PeripheralChannel d = createI2CDevice().getChannel();
            Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
            byte onval = (byte) 0x01;
            d.write(~onval);
            power.setState(ActivatorState.ACTIVATED);
            d.setMask(0xf0);
            for (int n = 0; n < 4; ++n) {
                byte val = (byte) 0x01;
                for (int i = 0; i < 8; ++i) {
                    d.write((byte) ~val);
                    wait(500);
                    val = (byte) (val << 1);
                }
                d.setMask(~0xf0);
                onval = (byte) (onval << 1);
                d.write(~onval);
                d.setMask(0xf0);
            }
            power.setState(ActivatorState.DEACTIVATED);
        }
    }

    @Test
    public void testSainsSmartRelayFromPCF8574Direct() throws Exception {
        synchronized (this) {
            Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
            I2CDevice dev = createI2CDevice();
            PeripheralChannel d = dev.getChannel();
            int repeat = 20;
            d.write(0xff);
            power.setState(ActivatorState.ACTIVATED);
            logger.debug("Speed test 20ms");
            byte[] bytes = new byte[8 * repeat];
            for (int c = 0; c < repeat; ++c) {
                byte val = (byte) 0x01;
                for (int i = 0; i < 8; ++i) {
                    bytes[(c * 8) + i] = (byte) ~val;
                    d.write((byte) ~val);
                    wait(20);
                    val = (byte) (val << 1);
                }
            }
            ByteBuffer b = ByteBuffer.wrap(bytes);
            logger.debug("Speed test 5ms");
            d.write((byte) 0xff);
            dev.setDeviceWidth(4);
            for (int n = 0; n < 4; ++n) {
                for (int i = 0; i < bytes.length/dev.getDeviceWidth(); ++i) {
                    int v = d.write(b,i*dev.getDeviceWidth(),1);
                    wait(50);
                }
            }
            dev.setDeviceWidth(1);

            d.write((byte) 0xff);
            logger.debug("Speed test 0ms");
            for (int i = 0; i < repeat; ++i) {
                int v = d.write(b);
            }
            d.write((byte) 0xff);
            power.setState(ActivatorState.DEACTIVATED);
            logger.debug("Finshed Speed test");

        }
    }
}
