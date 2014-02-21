package net.audumla.deviceaccess.i2cbus.rpi;

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
import net.audumla.deviceaccess.PeripheralDescriptor;
import net.audumla.deviceaccess.i2cbus.I2CDevice;
import net.audumla.deviceaccess.i2cbus.I2CDeviceConfig;
import net.audumla.deviceaccess.i2cbus.rpi.jni.RPiI2CNative;
import net.audumla.devices.io.i2c.jni.rpi.I2C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class RPiI2CDevice implements I2CDevice {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CDevice.class);

    private int handle;
    private int address;
    private PeripheralDescriptor<I2CDevice, I2CDeviceConfig> descriptor;
    private int width;

    public RPiI2CDevice(int handle, PeripheralDescriptor<I2CDevice, I2CDeviceConfig> descriptor) {
        this.handle = handle;
        address = descriptor.getConfiguration().getAddress();
        this.descriptor = descriptor;
    }

    @Override
    public PeripheralDescriptor<I2CDevice, I2CDeviceConfig> getDescriptor() {
        return descriptor;
    }

    @Override
    public PeripheralChannel getAddressableChannel(int readSubAddress, int writeSubAddress) {
        return null;
    }

    @Override
    public PeripheralChannel getChannel() {
        return new DirectChannel();
    }

    @Override
    public int getDeviceWidth() {
        return width;
    }

    @Override
    public void setDeviceWidth(int width) {
        this.width = width;
    }

    protected class DirectChannel implements PeripheralChannel {

        private int mask;

        @Override
        public int read() throws IOException {
            if (getDeviceWidth() > 1) {
                byte[] buffer = new byte[1];
                RPiI2CNative.read(handle, address, 0, 1, buffer, (byte) getMask());
                return buffer[0];
            }
            else {
                byte[] buffer = new byte[1];
                RPiI2CNative.read(handle, address, 0, 1, buffer, (byte) getMask());
                return buffer[0];
            }
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return 0;
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            return RPiI2CNative.read(handle, address, offset, size, dst.array(), (byte) getMask());
        }

        @Override
        public int write(int value) throws IOException {
            byte[] buffer = new byte[] {(byte) value};
            return RPiI2CNative.write(handle, address, 0, 1, buffer, (byte) getMask());
        }

        @Override
        public int write() throws IOException {
            return 0;
        }

        @Override
        public int write(ByteBuffer dst) throws IOException {
            return 0;
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                return RPiI2CNative.write(handle, 0, 1, dst.array(), getMask() == null ? (byte) 0xFF : getMask().byteValue());
            } else {
                for (int i = 0; i < size; ++i) {
                    write(dst.get(offset + i));
                }
            }
            return size;
        }

        @Override
        public void setMask(int mask) {
            this.mask = mask;
        }

        @Override
        public int getMask() {
            return mask;
        }

        @Override
        public int removeMask() {
            mask = Integer.MAX_VALUE;
            return mask;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() throws IOException {

        }
    }

    protected class AddressedByteIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            return I2C.readByte(handle, readAddressQueue.peek());
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read());
            }
            return size;
        }

        @Override
        public int write(int value) throws IOException {
            return I2C.writeByte(handle, writeAddressQueue.peek(), (byte) value);
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeBytes(handle, writeAddressQueue.peek(), size, offset, dst.array());
            } else {
                for (int i = 0; i < size; ++i) {
                    write(dst.get(offset + i));
                }
            }
            return size;
        }

    }

    protected class MaskedByteIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            return I2C.readByteDirect(handle) & getMask().byteValue();
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read());
            }
            return size;
        }

        @Override
        public int write(int value) throws IOException {
            return I2C.writeByteDirect(handle, (byte) (value & getMask().byteValue()));
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeBytesDirectMask(handle, size, offset, dst.array(), getMask().byteValue());
            } else {
                for (int i = 0; i < size; ++i) {
                    write(dst.get(offset + i));
                }
            }
            return size;
        }

    }

    protected class AddressedMaskedByteIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            return I2C.readByte(handle, readAddressQueue.peek()) & getMask().byteValue();
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read());
            }
            return size;
        }

        @Override
        public int write(int value) throws IOException {
            return I2C.writeByte(handle, writeAddressQueue.peek(), (byte) (value & getMask().byteValue()));
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeBytesMask(handle, writeAddressQueue.peek(), size, offset, dst.array(), getMask().byteValue());
            } else {
                for (int i = 0; i < size; ++i) {
                    write(dst.get(offset + i));
                }
            }
            return size;
        }

    }

    protected class AddressedWordIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            return I2C.readWord(handle,readAddressQueue.peek());
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read());
            }
            return size;
        }

        @Override
        public int write(int value) throws IOException {
            return I2C.writeWord(handle,writeAddressQueue.peek(), (char) value);
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                return I2C.writeWords(handle,writeAddressQueue.peek(),size/2,offset/2,dst.asCharBuffer().array());
            } else {
                for (int i = 0; i < size/2; ++i) {
                    write(dst.getChar(offset + i));
                }
            }
            return size;
        }
    }

    protected class AddressedMaskedWordIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            return I2C.readWord(handle,readAddressQueue.peek()) & getMask();
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read());
            }
            return size;
        }

        @Override
        public int write(int value) throws IOException {
            return I2C.writeWord(handle,writeAddressQueue.peek(), (char) (value & getMask()));
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                return I2C.writeWordsMask(handle, writeAddressQueue.peek(), size / 2, offset / 2, dst.asCharBuffer().array(), (char) getMask().intValue());
            } else {
                for (int i = 0; i < size/2; ++i) {
                    write(dst.getChar(offset + i));
                }
            }
            return size;
        }

    }
}
