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
    public PeripheralChannel getAddressableChannel(byte readSubAddress, byte writeSubAddress) {
        return new AddressedChannel(readSubAddress, writeSubAddress);
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

    protected class I2CDirectChannel implements PeripheralChannel {

        private byte[] mask;

        @Override
        public int read() throws IOException {
            if (mask == null) {
                return I2C.readByteDirect(handle);
            } else {
                return I2C.readByteDirect(handle) & mask[3];
            }
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return read(dst, dst.position(), dst.remaining());
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            return 0;
        }

        @Override
        public int write(int value) throws IOException {
            if (mask == null) {
                return I2C.writeByteDirect(handle, (byte) value);
            } else {
                return I2C.writeByteDirectMask(handle, (byte) value, mask[3]);
            }
        }

        @Override
        public int write(ByteBuffer dst) throws IOException {
            return write(dst, 0, dst.limit());
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (mask == null) {
                return I2C.writeBytesDirect(handle, size, offset, dst.array());
            } else {
                return I2C.writeBytesDirectMask(handle, size, offset, dst.array(), mask[3]);
            }
        }

        @Override
        public void setMask(int mask) {
            ByteBuffer result = ByteBuffer.allocate(4);
            result.putInt(mask);
            this.mask = result.array();
        }

        @Override
        public void removeMask() {
            mask = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        }

        @Override
        public boolean isOpen() {
            return handle != 0;
        }

        @Override
        public void close() throws IOException {

        }
    }

    protected class I2CAddressedChannel implements PeripheralChannel {

        private byte readSubAddress;
        private byte writeSubAddress;
        private byte[] mask;

        public I2CAddressedChannel(byte readSubAddress, byte writeSubAddress) {
            this.readSubAddress = readSubAddress;
            this.writeSubAddress = writeSubAddress;
        }

        @Override
        public int read() throws IOException {
            if (mask == null) {
                return I2C.readByte(handle,writeSubAddress);
            } else {
                return I2C.readByte(handle,writeSubAddress) & mask[3];
            }
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return read(dst, dst.position(), dst.remaining());
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            return 0;
        }

        @Override
        public int write(int value) throws IOException {
            if (mask == null) {
                return I2C.writeByte(handle, writeSubAddress, (byte) value);
            } else {
                return I2C.writeByteMask(handle, writeSubAddress, (byte) value, mask[0]);
            }
        }

        @Override
        public int write(ByteBuffer dst) throws IOException {
            return write(dst, 0, dst.limit());
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (mask == null) {
                return I2C.writeBytes(handle, writeSubAddress, size, offset, dst.array());
            } else {
                return I2C.writeBytesMask(handle, writeSubAddress, size, offset, dst.array(), mask[0]);
            }
        }

        @Override
        public void setMask(int mask) {
            ByteBuffer result = ByteBuffer.allocate(getDeviceWidth());
            result.putInt(mask);
            this.mask = result.array();
        }

        @Override
        public void removeMask() {
            mask = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        }

        @Override
        public boolean isOpen() {
            return handle != 0;
        }

        @Override
        public void close() throws IOException {

        }
    }

    protected class DirectChannel implements PeripheralChannel {

        private byte[] mask;

        @Override
        public int read() throws IOException {
            if (getDeviceWidth() > 1) {
                ByteBuffer result = ByteBuffer.allocate(getDeviceWidth());
                RPiI2CNative.read(handle, address, 0, getDeviceWidth(), 1, result.array(), mask);
                return result.getInt();
            } else {
                int value = RPiI2CNative.read(handle, address);
                if (mask != null) {
                    return value & mask[3];
                } else {
                    return value;
                }
            }
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return read(dst, dst.position(), dst.remaining());
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                return RPiI2CNative.read(handle, address, offset, getDeviceWidth(), size, dst.array(), mask);
            } else {
                throw new IOException("Cannot operate on non array backed ByteBuffer");
            }
        }

        @Override
        public int write(int value) throws IOException {
            if (getDeviceWidth() > 1) {
                ByteBuffer bbValue = ByteBuffer.allocate(getDeviceWidth());
                bbValue.putInt(value);
                return RPiI2CNative.write(handle, address, 0, getDeviceWidth(), 1, bbValue.array(), mask);
            } else {
                return RPiI2CNative.write(handle, address, (byte) value, mask == null ? (byte) 0xff : mask[3]);
            }
        }

        @Override
        public int write(ByteBuffer dst) throws IOException {
            return write(dst, 0, dst.limit());
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                return RPiI2CNative.write(handle, address, 0, getDeviceWidth(), 1, dst.array(), mask);
            } else {
                throw new IOException("Cannot operate on non array backed ByteBuffer");
            }
        }

        @Override
        public void setMask(int mask) {
            ByteBuffer result = ByteBuffer.allocate(getDeviceWidth());
            result.putInt(mask);
            this.mask = result.array();
        }

        @Override
        public void removeMask() {
            mask = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        }

        @Override
        public boolean isOpen() {
            return handle != 0;
        }

        @Override
        public void close() throws IOException {

        }
    }

    protected class AddressedChannel implements PeripheralChannel {

        private byte readSubAddress;
        private byte writeSubAddress;
        private byte[] mask;

        public AddressedChannel(byte readSubAddress, byte writeSubAddress) {
            this.readSubAddress = readSubAddress;
            this.writeSubAddress = writeSubAddress;
        }

        @Override
        public int read() throws IOException {
            if (getDeviceWidth() > 1) {
                ByteBuffer result = ByteBuffer.allocate(getDeviceWidth());
                RPiI2CNative.read(handle, address, readSubAddress, 0, getDeviceWidth(), 1, result.array(), mask);
                return result.getInt();
            } else {
                int value = RPiI2CNative.read(handle, address, readSubAddress);
                if (mask != null) {
                    return value & mask[3];
                } else {
                    return value;
                }
            }
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return read(dst, dst.position(), dst.remaining());
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                return RPiI2CNative.read(handle, address, readSubAddress, offset, getDeviceWidth(), size, dst.array(), mask);
            } else {
                throw new IOException("Cannot operate on non array backed ByteBuffer");
            }
        }

        @Override
        public int write(int value) throws IOException {
            if (getDeviceWidth() > 1) {
                ByteBuffer bbValue = ByteBuffer.allocate(getDeviceWidth());
                bbValue.putInt(value);
                return RPiI2CNative.write(handle, address, writeSubAddress, 0, getDeviceWidth(), 1, bbValue.array(), mask);
            } else {
                return RPiI2CNative.write(handle, address, writeSubAddress, (byte) value, mask == null ? (byte) 0xff : mask[3]);
            }
        }

        @Override
        public int write(ByteBuffer dst) throws IOException {
            return write(dst, 0, dst.limit());
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                return RPiI2CNative.write(handle, address, writeSubAddress, 0, getDeviceWidth(), 1, dst.array(), mask);
            } else {
                throw new IOException("Cannot operate on non array backed ByteBuffer");
            }
        }

        @Override
        public void setMask(int mask) {
            ByteBuffer result = ByteBuffer.allocate(getDeviceWidth());
            result.putInt(mask);
            this.mask = result.array();
        }

        @Override
        public void removeMask() {
            mask = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        }

        @Override
        public boolean isOpen() {
            return handle != 0;
        }

        @Override
        public void close() throws IOException {

        }
    }
}
