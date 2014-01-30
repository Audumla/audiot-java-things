package net.audumla.devices.io.i2c;

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

import net.audumla.devices.io.DeviceByteChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class I2CByteChannelFactory implements DeviceByteChannelFactory {
    private static final Logger logger = LoggerFactory.getLogger(I2CByteChannelFactory.class);

    protected int i2cAddress;
    protected I2CBus i2cBus;

    public I2CByteChannelFactory(int i2cAddress, I2CBus i2cBus) {
        this.i2cAddress = i2cAddress;
        this.i2cBus = i2cBus;
    }

    public I2CBus getI2CBus() {
        return i2cBus;
    }


    public int getI2CAddress() {
        return i2cAddress;
    }

    @Override
    public ByteChannel openByteChannel(int register) {
        return new I2CByteChannel(register);
    }

    @Override
    public ByteChannel openByteChannel() {
        return new I2CByteChannel();
    }

    protected class I2CByteChannel implements ByteChannel {

        protected Integer register;

        public I2CByteChannel() {
        }

        public I2CByteChannel(Integer register) {
            this.register = register;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            int wCount = 0;
            for (int i = 0; i < dst.capacity(); i++) {
                if (register != null) {
                    dst.put(i, getI2CBus().read(getI2CAddress(), register));
                } else {
                    dst.put(i, getI2CBus().read(getI2CAddress()));
                }
                wCount++;
            }
            return wCount;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            int wCount = 0;
            for (Byte b : src.array()) {
                if (register != null) {
                    if (getI2CBus().write(getI2CAddress(), register, b) > 0) {
                        wCount++;
                    }
                } else {
                    if (getI2CBus().write(getI2CAddress(), b) > 0) {
                        wCount++;
                    }
                }
            }
            return wCount;
        }

        @Override
        public boolean isOpen() {
            return getI2CBus().isOpen();
        }

        @Override
        public void close() throws IOException {

        }
    }
}
