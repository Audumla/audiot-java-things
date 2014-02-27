package net.audumla.perio;

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

import net.audumla.perio.impl.DefaultPeripheralChannelMessage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

public class MessageTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageTest.class);

    @Test
    public void testReadWriteBuffer() throws Exception {
        PeripheralChannel channel = new DummyPeripheral().getChannel();
        ByteBuffer bbw = ByteBuffer.allocate(1024);
        for (int i = 0; i < 250; ++i) {
            bbw.put((byte) i);
        }
        bbw.flip();
        channel.write(bbw);
        ByteBuffer bbr = ByteBuffer.allocate(1024);
        channel.read(bbr,0,250);
        bbr.flip();
        for (int i = 0; i < 250; ++i) {
            assert bbw.get(i) == bbr.get(i);
        }

    }

    @Test
    public void testMessageReadWrite() throws Exception {
        DefaultPeripheralChannelMessage message = new DefaultPeripheralChannelMessage();
        PeripheralChannel channel = new DummyPeripheral().getChannel();
        ByteBuffer bbw = ByteBuffer.allocate(1024);
        ByteBuffer bbr1 = ByteBuffer.allocate(100);
        ByteBuffer bbr2 = ByteBuffer.allocate(100);
        for (int i = 0; i < 250; ++i) {
            bbw.put((byte) i);
        }
        bbw.flip();
        message.appendWrite(channel, bbw);
        message.appendRead(channel, bbr1);
        message.appendRead(channel, bbr2);
        Collection<PeripheralChannelMessage.MessageChannelResult> results = message.transfer();
        assert results.size() == 3;
        bbr1.flip();
        bbr2.flip();
        bbw.rewind();
        for (int i = 0; i < 100; ++i) {
            assert bbw.get() == bbr1.get();
        }

        for (int i = 0; i < 100; ++i) {
            assert bbw.get() == bbr2.get();
        }
    }

    @Test
    public void testSizedMessageReadWrite() throws Exception {
        DefaultPeripheralChannelMessage message = new DefaultPeripheralChannelMessage();
        PeripheralChannel channel = new DummyPeripheral().getChannel();
        ByteBuffer bbw = ByteBuffer.allocate(1024);
        ByteBuffer bbr = ByteBuffer.allocate(1024);
        for (int i = 0; i < 250; ++i) {
            bbw.put((byte) i);
            message.appendSizedWrite(channel, 1);
            message.appendWait(Duration.ofMillis(1));
            message.appendSizedRead(channel, 1);
            message.appendWait(Duration.ofMillis(1));
        }
        bbw.flip();
        Instant now = Instant.now();
        Collection<PeripheralChannelMessage.MessageChannelResult> results = message.transfer(bbw, bbr);
        Instant after = Instant.now();
        long millis = Duration.between(now, after).toMillis();
        assert millis > 499;
        assert results.size() == 500;
        results.forEach(r -> {assert r.getTransferSize() == 1;});
        bbr.flip();
        for (int i = 0; i < 250; ++i) {
            assert bbw.get(i) == bbr.get(i);
        }

    }
}
