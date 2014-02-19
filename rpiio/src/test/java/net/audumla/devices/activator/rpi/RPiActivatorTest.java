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
import net.audumla.devices.io.channel.*;
import net.audumla.devices.io.channel.I2CDeviceChannel;
import net.audumla.devices.io.i2c.RPiI2CDeviceFactory;
import net.audumla.devices.io.i2c.jni.rpi.I2C;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class RPiActivatorTest {
    private static final Logger logger = LoggerFactory.getLogger(RPiActivatorTest.class);

    static RPIGPIOActivatorFactory rpi = new RPIGPIOActivatorFactory();
    static PCF8574GPIOActivatorFactory power5v;

    static {
        try {
            DeviceChannel d = new I2CDeviceChannel(new RPiI2CDeviceFactory()).createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(0x27));
            power5v = new PCF8574GPIOActivatorFactory(d);
            power5v.initialize();
//            for (Activator a : power5v.getActivators()) {
//                a.applyState(ActivatorState.ACTIVATED);
//            }

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

    //    @Test
    public void testGPIOPins() throws Exception {
        assert rpi.getActivators().size() > 0;
        for (RPIGPIOActivatorFactory.RPIGPIOActivator a : rpi.getActivators()) {
            RPIGPIOActivatorFactory.GPIOName.valueOf(a.getGpioName().name());
        }
    }


    //    @Test
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

//        Activator power = rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6);
        Activator power = getPower(2, 3, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6));
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


        for (int i = 0; i < 3; ++i) {
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

    protected SainsSmartRelayActivatorFactory getSSGPIO() throws Exception {
        Collection<Activator> pins = new ArrayList<>();
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_CE1));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_CE0));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_MISO));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_SCLK));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.SPI_MOSI));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO3));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO2));
        pins.add(rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO0));

//        Activator power = rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6);
        Activator power = getPower(2, 3, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO6));

        SainsSmartRelayActivatorFactory ss = new SainsSmartRelayActivatorFactory(pins, power);
        ss.initialize();
        return ss;

    }

    //    @Test
    public void testSainsSmartRelayFromRPIGPIO() throws Exception {

        SainsSmartRelayActivatorFactory ss = getSSGPIO();
        int v = 0;
        for (int i = 0; i < 3; ++i) {
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

    SainsSmartRelayActivatorFactory getSSPCF(int addr, int pwr1, int pwr2, RPIGPIOActivatorFactory.GPIOName gpioPower) throws Exception {

        DeviceChannel d = new I2CDeviceChannel(new RPiI2CDeviceFactory()).createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(addr));
        PCF8574GPIOActivatorFactory gpio = new PCF8574GPIOActivatorFactory(d);
        gpio.initialize();
//        Activator power = rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1);
        Activator power = getPower(pwr1, pwr2, rpi.getActivator(gpioPower));

        SainsSmartRelayActivatorFactory ss = new SainsSmartRelayActivatorFactory(gpio.getActivators(), power);
        ss.initialize();
        return ss;

    }

    //    @Test
    public void testSainsSmartRelayFromPCF8574() throws Exception {
        SainsSmartRelayActivatorFactory ss = getSSPCF(PCF8574GPIOActivatorFactory.PCF8574_0x21, 6, 7, RPIGPIOActivatorFactory.GPIOName.GPIO1);
        int v = 0;
        for (int i = 0; i < 3; ++i) {
            for (Activator a : ss.getActivators()) {
                a.setState(ActivatorState.ACTIVATED);
                synchronized (this) {
                    wait(10 + v);
                }
                a.setState(ActivatorState.DEACTIVATED);
                v += 2;
            }
        }
        ss.shutdown();

    }

//    @Test
    public void testSainsSmartRelayFromPCF8574Direct() throws Exception {
        synchronized (this) {
            int fd = I2C.open("/dev/i2c-1", PCF8574GPIOActivatorFactory.PCF8574_0x21);
            Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
            int repeat = 10;
            logger.debug("Speed test 5ms");
            power.setState(ActivatorState.ACTIVATED);
            wait(20);
            I2C.writeByteDirect(fd, (byte) 0xff);
            wait(20);
            byte[] bytes = new byte[8 * repeat];
            for (int c = 0; c < repeat; ++c) {
                byte val = (byte) 0x01;
                for (int i = 0; i < 8; ++i) {
                    bytes[(c * 8) + i] = (byte) ~val;
                    I2C.writeByteDirect(fd, (byte) ~val);
                    wait(5);
                    val = (byte) (val << 1);
                }
            }
            logger.debug("Speed test 2ms");
            wait(1000);
            I2C.writeByteDirect(fd, (byte) 0xff);
            wait(20);
            for (int c = 0; c < repeat; ++c) {
                byte val = (byte) 0x01;
                for (int i = 0; i < 8; ++i) {
                    I2C.writeByteDirect(fd, (byte) ~val);
                    wait(2);
                    val = (byte) (val << 1);
                }
            }

            wait(1000);
            I2C.writeByteDirect(fd, (byte) 0xff);
            wait(20);
            logger.debug("Speed test 0ms");
//            for (int i = 0; i < repeat; ++i) {
                int v = I2C.writeBytesDirect(fd, bytes.length,0, bytes);
                if (v < 0) {
                    throw new Exception("Failed Speed test - "+v);
                }
//            }
            I2C.close(fd);
            power.setState(ActivatorState.DEACTIVATED);
            logger.debug("Finshed Speed test");

        }
    }


//    @Test
    public void testSainsSmartRelayFromPCF8574Stream() throws Exception {
        DeviceChannel d = new I2CDeviceChannel(new RPiI2CDeviceFactory()).createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(PCF8574GPIOActivatorFactory.PCF8574_0x21));
        Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
        power.setState(ActivatorState.ACTIVATED);
        ByteBuffer bb = ByteBuffer.allocate(50);
        byte val = (byte) 0x01;
        for (int i = 0; i < 8; ++i) {
            bb.put((byte) ~val);
            d.setAttribute(bb, new FixedWaitAttr(10 * (i + 1)));
            val = (byte) (val << 1);
        }
        bb.put((byte) 0xff);
        bb.flip();
        for (int i = 0; i < 10; ++i) {
            bb.position(0);
            d.write(bb);
        }
        power.setState(ActivatorState.DEACTIVATED);
    }

//    @Test
    public void testSainsSmartRelayFromPCF8574StreamMask() throws Exception {
        DeviceChannel d = new I2CDeviceChannel(new RPiI2CDeviceFactory()).createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(PCF8574GPIOActivatorFactory.PCF8574_0x21));
        d.write((byte) ~0x01);
        Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
        power.setState(ActivatorState.ACTIVATED);
        d.setAttribute(new BitMaskAttr(0xf0));
        ByteBuffer bb = ByteBuffer.allocate(50);
        byte val = (byte) 0x01;
        for (int i = 0; i < 8; ++i) {
            bb.put((byte) ~val);
            d.setAttribute(bb, new FixedWaitAttr(10 * (i + 1)));
            val = (byte) (val << 1);
        }
        bb.put((byte) 0xff);
        bb.flip();
        for (int i = 0; i < 10; ++i) {
            d.write(bb);
            bb.position(0);
        }
        power.setState(ActivatorState.DEACTIVATED);
    }

    //    @Test
    public void testMultiSainsSmartRelay() throws Exception {
        SainsSmartRelayActivatorFactory ss1 = getSSGPIO();
        SainsSmartRelayActivatorFactory ss2 = getSSPCF(PCF8574GPIOActivatorFactory.PCF8574_0x21, 6, 7, RPIGPIOActivatorFactory.GPIOName.GPIO1);
        SainsSmartRelayActivatorFactory ss3 = getSSPCF(PCF8574GPIOActivatorFactory.PCF8574_0x24, 4, 5, RPIGPIOActivatorFactory.GPIOName.GPIO5);

        Collection<Activator> activators = new ArrayList<>();
        activators.addAll(ss1.getActivators());
        activators.addAll(ss2.getActivators());
        activators.addAll(ss3.getActivators());

        int v = 10;
        for (int i = 0; i < 3; ++i) {
            for (Activator a : activators) {
                a.setState(ActivatorState.ACTIVATED);
                synchronized (this) {
                    wait(v);
                }
                a.setState(ActivatorState.DEACTIVATED);
                v += 1;
            }
        }
        ss1.shutdown();
        ss2.shutdown();
        ss3.shutdown();

    }
}
