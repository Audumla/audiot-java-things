package com.oracle.deviceaccess;

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

import com.oracle.deviceaccess.i2cbus.I2CDevice;
import com.oracle.deviceaccess.i2cbus.I2CDeviceConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class J2MEBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(J2MEBaseTest.class);

    @Test
    public void testBases() throws Exception {
        I2CDevice d = new I2CDevice() {
            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public int read(int i, int i2, int i3, ByteBuffer buffer) throws IOException {
                return 0;
            }

            @Override
            public int read(int i, int i2, ByteBuffer buffer) throws IOException {
                return 0;
            }

            @Override
            public int read(int i, ByteBuffer buffer) throws IOException {
                return 0;
            }

            @Override
            public int read(ByteBuffer buffer) throws IOException {
                return 0;
            }

            @Override
            public int write(int i, int i2, ByteBuffer buffer) throws IOException {
                logger.debug("Write " + buffer);
                return i;
            }

            @Override
            public int write(ByteBuffer buffer) throws IOException {
                logger.debug("Write " + buffer);
                return buffer.limit();
            }

            @Override
            public void begin() throws IOException {

            }

            @Override
            public void end() throws IOException {

            }

            @Override
            public void write(int i) throws IOException {
                logger.debug("Write " + i);
            }

            @Override
            public ByteBuffer getInputBuffer() throws IOException {
                return ByteBuffer.allocate(10);
            }

            @Override
            public ByteBuffer getOutputBuffer() throws IOException {
                return ByteBuffer.allocate(10);
            }

            @Override
            public PeripheralDescriptor getDescriptor() {
                return null;
            }

            @Override
            public boolean isOpen() {
                logger.debug("Open");
                return true;
            }

            @Override
            public void close() throws IOException {
                logger.debug("Close");
            }

            @Override
            public void tryLock(int i) throws IOException {

            }

            @Override
            public void unlock() throws IOException {

            }
        };
//        I2CDeviceConfig dc = new I2CDeviceConfig(1,2,3,1000);
//        ByteBuffer b = ByteBuffer.allocate(10);
//        b.put(new byte[] {1,2,3,4,5,6,7,8,9});
//        I2CCombinedMessage m = new I2CCombinedMessage();
//        m.appendRead(d, b);
//        PortListener
//        m.transfer();
//        assert dc.getDeviceNumber() == 1;
//        assert dc.getAddress() == 2;
//        assert dc.getAddressSize() == 3;
//        assert dc.getClockFrequency() == 1000;


        I2CDevice i1 = PeripheralManager.open(5, I2CDevice.class,PeripheralManager.EXCLUSIVE);
        I2CDevice i2 = PeripheralManager.open(5, I2CDevice.class);
        I2CDevice i3 = PeripheralManager.open(new I2CDeviceConfig(0,0,0,0));
        I2CDevice i4 = PeripheralManager.open(new I2CDeviceConfig(0,0,0,0),PeripheralManager.EXCLUSIVE);
        I2CDevice i5 = PeripheralManager.open();

    }
}
