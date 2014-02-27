package net.audumla.perio.i2c;

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

import net.audumla.perio.PeripheralChannel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayTest {
    private static final Logger logger = LoggerFactory.getLogger(RelayTest.class);
//    static PCF8574GPIOActivatorFactory power5v;
//    static RPIGPIOActivatorFactory rpi = new RPIGPIOActivatorFactory();
//
//    static {
//        try {
//            rpi.initialize();
//        } catch (Exception e) {
//            logger.error("Cannot initialize PI", e);
//        }
//    }
//
//    static {
//        try {
//            DeviceChannel d = new I2CDeviceChannel(new RPiI2CDeviceFactory()).createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(0x27));
//            power5v = new PCF8574GPIOActivatorFactory(d);
//            power5v.initialize();
//        } catch (Exception e) {
//            logger.error("Failed to initialize power5v", e);
//        }
//    }
//
//    Activator getPower(int pin1_5v, int pin2_5v, Activator pin3_33v) throws Exception {
//        AggregateActivator aa = new AggregateActivator();
//        Properties id = new Properties();
//        id.setProperty(PCF8574GPIOActivatorFactory.PCF8547GPIOActivator.GPIO_PIN, String.valueOf(pin1_5v));
//        aa.addActivator(power5v.getActivator(id));
//        id.setProperty(PCF8574GPIOActivatorFactory.PCF8547GPIOActivator.GPIO_PIN, String.valueOf(pin2_5v));
//        aa.addActivator(power5v.getActivator(id));
//        aa.addActivator(pin3_33v);
//        return aa;
//    }
//    @Test
//    public void testPCF8574Direct() throws Exception {
//        synchronized (this) {
//            Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
//            I2CDevice dev = createI2CDevice();
//            PeripheralChannel d = dev.getChannel();
//            int repeat = 2;
//            d.write(0xff);
//            power.setState(ActivatorState.ACTIVATED);
//            byte[] bytes = new byte[8 * repeat];
//            for (int c = 0; c < repeat; ++c) {
//                byte val = (byte) 0x01;
//                for (int i = 0; i < 8; ++i) {
//                    bytes[(c * 8) + i] = (byte) ~val;
//                    d.write((byte) ~val);
//                    wait(10);
//                    val = (byte) (val << 1);
//                }
//            }
//            ByteBuffer b = ByteBuffer.wrap(bytes);
//            d.write((byte) 0xff);
//            dev.setDeviceWidth(4);
//            for (int n = 0; n < 4; ++n) {
//                b.rewind();
//                for (int i = 0; i < bytes.length/dev.getDeviceWidth(); ++i) {
//                    int v = d.write(b,i*dev.getDeviceWidth(),1);
//                    assert v == 4;
//                    wait(15);
//                }
//            }
//            dev.setDeviceWidth(1);
//            d.write((byte) 0xff);
//            for (int i = 0; i < repeat; ++i) {
//                b.rewind();
//                int v = d.write(b);
//                assert v == b.limit();
//            }
//            d.write((byte) 0xff);
//            power.setState(ActivatorState.DEACTIVATED);
//            logger.debug("Finshed Speed test");
//        }
//    }
//

//    @Test
//    public void testPCF8574ChannelMessageWrite() throws Exception {
//        synchronized (this) {
//            Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
//            I2CDevice dev = createI2CDevice();
//            PeripheralChannel d = dev.getChannel();
//            DefaultPeripheralChannelMessage message = new DefaultPeripheralChannelMessage();
//            int repeat = 2;
//            d.write(0xff);
//            byte[] bytes = new byte[8 * repeat];
//            power.setState(ActivatorState.ACTIVATED);
//            for (int c = 0; c < repeat; ++c) {
//                byte val = (byte) 0x01;
//                for (int i = 0; i < 8; ++i) {
//                    bytes[(c * 8) + i] = (byte) ~val;
//                    message.appendWrite(d, (byte)~val);
//                    message.appendWait(Duration.ofMillis(20));
//                    val = (byte) (val << 1);
//                }
//            }
//            Collection<PeripheralChannelMessage.MessageChannelResult> results = message.transfer();
//            assert results.size() == 8 * repeat;
//            ByteBuffer b = ByteBuffer.wrap(bytes);
//            results = message.transfer(b,null);
//            assert results.size() == 8 * repeat;
//            results.forEach(r -> {assert r.getTransferSize() == 1;});
//            b.rewind();
//            int len = message.write(b);
//            assert len == 8 * repeat;
//        }
//    }
//
//
//    @Test
//    public void testPCF8574ChannelMessageReadWrite() throws Exception {
//        synchronized (this) {
//            Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
//            I2CDevice dev = createI2CDevice();
//            PeripheralChannel d = dev.getChannel();
//            DefaultPeripheralChannelMessage message = new DefaultPeripheralChannelMessage();
//            d.write(0xff);
//            byte[] bytes = new byte[9];
//            ByteBuffer rx = ByteBuffer.allocate(1024);
//            power.setState(ActivatorState.ACTIVATED);
//            byte val = (byte) 0x01;
//            for (int i = 0; i < 8; ++i) {
//                bytes[i] = (byte) ~val;
//                message.appendWrite(d, (byte)~val);
//                message.appendSizedRead(d,1);
//                message.appendWait(Duration.ofMillis(10));
//                val = (byte) (val << 1);
//            }
//            message.appendWrite(d, (byte)0xff);
//            bytes[8] = (byte) 0xff;
//            Collection<PeripheralChannelMessage.MessageChannelResult> results = message.transfer();
//            assert results.size() == 17;
//            ByteBuffer b = ByteBuffer.wrap(bytes);
//            results = message.transfer(b,rx);
//            assert results.size() == 17;
//            rx.flip();
//            assert rx.limit() == 8;
//            for (int i =0; i < rx.limit(); ++i) {
//                assert rx.get(i) == b.get(i);
//            }
//            results.forEach(r -> {assert r.getTransferSize() == 1;});
//            b.rewind();
//            int len = message.write(b);
//            assert len == 9;
//        }
//    }
//
//@Test
//public void testPCF8574StreamMask() throws Exception {
//    synchronized (this) {
//        PeripheralChannel d = createI2CDevice().getChannel();
//        Activator power = getPower(6, 7, rpi.getActivator(RPIGPIOActivatorFactory.GPIOName.GPIO1));
//        byte onval = (byte) 0x01;
//        d.write(~onval);
//        power.setState(ActivatorState.ACTIVATED);
//        d.setMask(0xf0);
//        for (int n = 0; n < 4; ++n) {
//            byte val = (byte) 0x01;
//            for (int i = 0; i < 8; ++i) {
//                d.write((byte) ~val);
//                wait(10);
//                val = (byte) (val << 1);
//            }
//            d.setMask(~0xf0);
//            onval = (byte) (onval << 1);
//            d.write(~onval);
//            d.setMask(0xf0);
//        }
//        power.setState(ActivatorState.DEACTIVATED);
//    }
//}
//

}
