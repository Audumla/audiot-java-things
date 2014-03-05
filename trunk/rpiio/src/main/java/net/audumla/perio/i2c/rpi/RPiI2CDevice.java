package net.audumla.perio.i2c.rpi;

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

import net.audumla.perio.*;
import net.audumla.perio.i2c.I2CDevice;
import net.audumla.perio.i2c.I2CDeviceConfig;
import net.audumla.perio.i2c.rpi.jni.RPiI2CNative;
import net.audumla.perio.jni.DefaultErrorHandler;
import net.audumla.perio.jni.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RPiI2CDevice implements I2CDevice {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CDevice.class);

    private final int handle;
    private final int address;
    private final PeripheralDescriptor<I2CDevice, I2CDeviceConfig> descriptor;
    private final int width;

    public RPiI2CDevice(final int handle, final PeripheralDescriptor<I2CDevice, I2CDeviceConfig> descriptor) {
        width = descriptor.getConfiguration().getWidth();
        this.handle = handle;
        address = descriptor.getConfiguration().getAddress();
        this.descriptor = descriptor;
    }

    @Override
    public PeripheralDescriptor<I2CDevice, I2CDeviceConfig> getDescriptor() {
        return descriptor;
    }

    @Override
    public ReadWritePeripheralChannel getReadWriteChannel(final Long read, final Long write) {
        return new AddressedChannel(write.byteValue(), read.byteValue());
    }

    @Override
    public ReadWritePeripheralChannel getReadWriteChannel(final Long readWrite) {
        return getReadWriteChannel(readWrite, readWrite);
    }

    @Override
    public WritablePeripheralChannel getWriteChannel(final Long write) {
        return new AddressedChannel(null, write.byteValue());
    }

    @Override
    public ReadablePeripheralChannel getReadChannel(final Long read) {
        return new AddressedChannel(read.byteValue(), null);
    }

    @Override
    public ReadWritePeripheralChannel getReadWriteChannel() {
        return new DirectChannel();
    }

    @Override
    public ReadablePeripheralChannel getReadChannel() {
        return getReadWriteChannel();
    }

    @Override
    public WritablePeripheralChannel getWriteChannel() {
        return getReadWriteChannel();
    }

    protected abstract class AbstractI2CChannel extends DefaultErrorHandler implements ReadWritePeripheralChannel, ErrorHandler {

        protected byte[] mask;
        protected boolean failOnBufferCapacity = true;
        protected NativePeripheralException exception;


        @Override
        public int getDeviceWidth() {
            return width;
        }

        @Override
        public int getBitWidth() {
            return getDeviceWidth() * 8;
        }

        protected ByteBuffer toByteBuffer(final long value) {
            ByteBuffer result = ByteBuffer.allocate(getDeviceWidth());
            for (int i = 0; i < getDeviceWidth(); ++i) {
                result.put(getDeviceWidth() - (i + 1), (byte) (value >> i * 8));
            }
            return result;
        }

        @Override
        public void setMask(final long mask) {
            this.mask = toByteBuffer(mask).array();
        }

        @Override
        public void removeMask() {
            mask = null;
        }

        @Override
        public boolean isOpen() {
            return handle != 0;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public int read(final ByteBuffer dst) throws IOException {
            return read(dst, dst.position(), dst.remaining());
        }

        @Override
        public int write(final ByteBuffer dst) throws IOException {
            return write(dst, 0, dst.limit());
        }

        protected void adjustBufferPosition(final ByteBuffer bb, final int adjust) throws IOException {
            try {
                bb.position(bb.position() + adjust);
            } catch (IllegalArgumentException ex) {
                throw new PeripheralChannelMessageException("Attempt to position buffer beyond limit [limit:" + bb.limit() + "] [new position:" + (bb.position() + adjust) + "]", ex);
            }
        }

        @Override
        public int read() throws IOException {
            // if the device width is greater than 1 then we need to do a multibyte read, otherwise we can use the optimized single byte read
            if (getDeviceWidth() > 1) {
                ByteBuffer result = ByteBuffer.allocate(getDeviceWidth());
                read(handle, address, 0, getDeviceWidth(), 1, result.array(), mask);
                failOnError();
                return result.getInt();
            } else {
                int value = read(handle, address);
                if (mask != null) {
                    return value & mask[0];
                } else {
                    return value;
                }
            }
        }

        @Override
        public int read(final ByteBuffer dst, final int offset, final int size) throws IOException {
            int len;
            if (dst.hasArray()) {
                if (!validateBufferCapacity(dst, getDeviceWidth() * size) && failOnBufferCapacity) {
                    throw new PeripheralChannelMessageException("Cannot perform read - Buffer has no capacity");
                }
                len = read(handle, address, offset, getDeviceWidth(), size, dst.array(), mask);
                adjustBufferPosition(dst, len);
                failOnError();
            } else {
                throw new PeripheralChannelMessageException("Cannot operate on non array backed ByteBuffer ");
            }
            return len;
        }

        @Override
        public int write(final int value) throws IOException {
            // if the device width is greater than 1 then we need to do a multibyte write, otherwise we can use the optimized single byte write
            int len;
            if (getDeviceWidth() > 1) {
                ByteBuffer bbValue = toByteBuffer(value);
                len = write(handle, address, 0, getDeviceWidth(), 1, bbValue.array(), mask);

            } else {
                len = write(handle, address, (byte) value, mask == null ? (byte) 0xff : mask[0]);
            }
            failOnError();
            return len;
        }

        @Override
        public int write(final ByteBuffer dst, final int offset, final int size) throws IOException {
            int len;
            if (dst.hasArray()) {
                if (!validateBufferCapacity(dst, getDeviceWidth() * size) && failOnBufferCapacity) {
                    throw new PeripheralChannelMessageException("Cannot perform write - Buffer has no capacity");
                }
                len = write(handle, address, offset, getDeviceWidth(), size, dst.array(), mask);
                adjustBufferPosition(dst, len);
            } else {
                throw new PeripheralChannelMessageException("Cannot operate on non array backed ByteBuffer ");
            }
            return len;
        }

        protected boolean validateBufferCapacity(final ByteBuffer dst, final int i) {
            return dst.remaining() >= i;
        }

        protected abstract int read(final int handle, final int address);

        protected abstract int read(final int handle, final int address, final int offset, final int deviceWidth, final int size, final byte[] array, final byte[] mask);

        protected abstract int write(final int handle, final int address, final byte value, final byte mask);

        protected abstract int write(final int handle, final int address, final int offset, final int deviceWidth, final int size, final byte[] array, final byte[] mask);

    }

    protected class DirectChannel extends AbstractI2CChannel {

        @Override
        protected int read(final int handle, final int address) {
            return RPiI2CNative.read(handle, address, this);
        }

        @Override
        protected int read(final int handle, final int address, final int offset, final int deviceWidth, final int size, byte[] array, byte[] mask) {
            return RPiI2CNative.read(handle, address, offset, deviceWidth * size, array, mask == null ? (byte) 0xff : mask[0], this);
        }

        @Override
        protected int write(final int handle, final int address, final byte value, final byte mask) {
            return RPiI2CNative.write(handle, address, value, mask, this);
        }

        @Override
        protected int write(final int handle, final int address, final int offset, final int deviceWidth, final int size, byte[] array, byte[] mask) {
            return RPiI2CNative.write(handle, address, offset, deviceWidth * size, array, mask == null ? (byte) 0xff : mask[0], this);
        }

    }

    protected class AddressedChannel extends AbstractI2CChannel {

        private final Byte readSubAddress;
        private final Byte writeSubAddress;

        public AddressedChannel(final Byte readSubAddress, final Byte writeSubAddress) {
            this.readSubAddress = readSubAddress;
            this.writeSubAddress = writeSubAddress;
        }


        @Override
        protected int read(final int handle, final int address) {
            return RPiI2CNative.read(handle, address, readSubAddress, this);
        }

        @Override
        protected int read(final int handle, final int address, final int offset, final int deviceWidth, final int size, final byte[] array, final byte[] mask) {
            return RPiI2CNative.read(handle, address, readSubAddress, offset, deviceWidth, size, array, mask, this);
        }

        @Override
        protected int write(final int handle, final int address, byte value, byte mask) {
            return RPiI2CNative.write(handle, address, writeSubAddress, value, mask, this);
        }

        @Override
        protected int write(final int handle, final int address, final int offset, final int deviceWidth, final int size, final byte[] array, final byte[] mask) {
            return RPiI2CNative.write(handle, address, writeSubAddress, offset, deviceWidth, size, array, mask, this);
        }

    }
}
