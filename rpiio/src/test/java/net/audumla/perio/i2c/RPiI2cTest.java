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

import net.audumla.perio.ReadWritePeripheralChannel;
import net.audumla.perio.PeripheralChannelMessage;
import net.audumla.perio.PeripheralManager;
import net.audumla.perio.i2c.rpi.RPiI2CPeripheralProvider;
import net.audumla.perio.i2c.rpi.jni.RPiI2CNative;
import net.audumla.perio.impl.DefaultPeripheralChannelMessage;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class RPiI2cTest {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2cTest.class);


    public I2CDevice createI2CDevice() throws Exception {
        I2CDeviceConfig c = new I2CDeviceConfig(1, 0x21);
        RPiI2CPeripheralProvider provider = new RPiI2CPeripheralProvider();
        I2CDevice device = provider.open(c, null, PeripheralManager.SHARED);
        assert device != null;
        return device;
    }

    @Test
    public void testFrequency() throws Exception {
        int freq1 = RPiI2CNative.getClock(1);
        int freq12 = RPiI2CNative.setClock(1, 10000);
        assert freq12 == freq1;
        freq12 = RPiI2CNative.setClock(1, 150000);
        assert freq12 == 10000;
        int freq0 = RPiI2CNative.getClock(0);
        int freq02 = RPiI2CNative.setClock(0, 40000);
        assert freq02 == freq0;
        freq02 = RPiI2CNative.setClock(0, 200000);
        assert freq02 == 40000;

        freq1 = RPiI2CNative.getClock(1);
        Assert.assertEquals(150000, freq1, 1000);
        freq0 = RPiI2CNative.getClock(0);
        Assert.assertEquals(200000, freq0, 1000);
    }

    @Test
    public void PCF8574readwrite8() throws Exception {
        ReadWritePeripheralChannel d = createI2CDevice().getReadWriteChannel();
        byte val = (byte) 0x01;
        d.write(val);
        Assert.assertEquals(val, d.read());
        d.write((byte) ~val);
        Assert.assertEquals(~val, d.read());
    }

    @Test
    public void PCF8574RWMask8() throws Exception {
        ReadWritePeripheralChannel d = createI2CDevice().getReadWriteChannel();
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
    public void testMultiRead() throws Exception {
        I2CDevice dev = createI2CDevice();
        ReadWritePeripheralChannel d = dev.getReadWriteChannel();

        DefaultPeripheralChannelMessage message = new DefaultPeripheralChannelMessage();

        ByteBuffer rx1 = ByteBuffer.allocate(10);
        ByteBuffer rx2 = ByteBuffer.allocate(10);

        message.appendWrite(d, (byte) 0xfe);
        message.appendRead(d, rx1);
        message.appendRead(d, rx2);

        Collection<PeripheralChannelMessage.MessageChannelResult> results = message.transfer();
        assert rx1.position() == rx1.limit();
        assert rx2.position() == rx2.limit();

        rx1.rewind();
        rx2.rewind();

        assert results.size() == 3;
        Iterator<PeripheralChannelMessage.MessageChannelResult> it = results.iterator();
        assert it.next().getTransferSize() == 1;
        assert it.next().getTransferSize() == 10;
        assert it.next().getTransferSize() == 10;

        for (int i = 0; i < 10; ++i) {
            assert rx1.get(i) == (byte) 0xfe;
            assert rx2.get(i) == (byte) 0xfe;
        }

    }
}
