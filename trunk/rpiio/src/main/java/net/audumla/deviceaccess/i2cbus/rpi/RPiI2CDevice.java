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

import net.audumla.deviceaccess.PeripheralDescriptor;
import net.audumla.deviceaccess.i2cbus.I2CDevice;
import net.audumla.deviceaccess.i2cbus.I2CDeviceConfig;
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
    private DeviceBusIO activeIO;
    private DeviceBusIO addressedActiveIO;
    private Queue<Integer> readAddressQueue = new LinkedList<>();
    private Queue<Integer> writeAddressQueue = new LinkedList<>();
    private Integer mask;
    private ChannelWidth width = ChannelWidth.WIDTH8;
    private Integer addressSize ;
    private PeripheralDescriptor<I2CDevice, I2CDeviceConfig> descriptor;


    public RPiI2CDevice(int handle, PeripheralDescriptor<I2CDevice, I2CDeviceConfig> descriptor) {
        this.handle = handle;
        this.descriptor = descriptor;
    }

    @Override
    public int read(int subAddress) throws IOException {
        try {
            readAddressQueue.add(subAddress);
            return addressedActiveIO.read();
        }
        finally {
            readAddressQueue.remove();
        }
    }

    @Override
    public int read(int subAddress, ByteBuffer dst) throws IOException {
        try {
            readAddressQueue.add(subAddress);
            return addressedActiveIO.read(dst, dst.position(), dst.remaining());
        }
        finally {
            readAddressQueue.remove();
        }
    }

    @Override
    public int read(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
        try {
            readAddressQueue.add(subAddress);
            return addressedActiveIO.read(dst, offset, size);
        }
        finally {
            readAddressQueue.remove();
        }
    }

    @Override
    public int write(int subAddress, ByteBuffer dst) throws IOException {
        try {
            writeAddressQueue.add(subAddress);
            return addressedActiveIO.write(dst, 0, dst.limit());
        }
        finally {
            writeAddressQueue.remove();
        }
    }

    @Override
    public int write(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
        try {
            writeAddressQueue.add(subAddress);
            return addressedActiveIO.write( dst, offset, size);
        }
        finally {
            writeAddressQueue.remove();
        }
    }

    @Override
    public int write(int subAddress, byte... data) throws IOException {
        try {
            writeAddressQueue.add(subAddress);
            return data.length == 1 ? addressedActiveIO.write(data[0]) : addressedActiveIO.write(ByteBuffer.wrap(data), 0, data.length);
        }
        finally {
            writeAddressQueue.remove();
        }
    }

    @Override
    public int write(byte... data) throws IOException {
        return data.length == 1 ? activeIO.write(data[0]) : activeIO.write(ByteBuffer.wrap(data), 0, data.length);
    }

    @Override
    public int write(ByteBuffer dst, int offset, int size) throws IOException {
        return activeIO.write(dst, offset, size);
    }

    @Override
    public int read() throws IOException {
        return activeIO.read();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return activeIO.read(dst, dst.position(), dst.remaining());
    }

    @Override
    public int read(ByteBuffer dst, int offset, int size) throws IOException {
        return activeIO.read(dst, offset, size);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return activeIO.write(src, 0, src.limit());
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void setReadWriteAddresses(Integer readAddress, Integer writeAddress) {
        readAddressQueue.poll();
        readAddressQueue.add(readAddress);
        writeAddressQueue.poll();
        writeAddressQueue.add(writeAddress);
    }

    @Override
    public Integer getReadAddress() {
        return readAddressQueue.peek();
    }

    @Override
    public Integer getWriteAddress() {
        return writeAddressQueue.peek();
    }

    @Override
    public void setMask(Integer mask) {
        this.mask = mask;
    }

    @Override
    public Integer getMask() {
        return mask;
    }

    protected void setActiveIO() {
        if (getMask() != null) {
            if (getReadAddress() != null) {
                activeIO = getDescriptor().getConfiguration().getWidth().equals(ChannelWidth.WIDTH8) ? new AddressedMaskedByteIO() : new AddressedMaskedWordIO();
                addressedActiveIO = activeIO;
            } else {
                activeIO = getDescriptor().getConfiguration().getWidth().equals(ChannelWidth.WIDTH8) ? new MaskedByteIO() : null;
                addressedActiveIO = getDescriptor().getConfiguration().getWidth().equals(ChannelWidth.WIDTH8) ? new AddressedMaskedByteIO() : new AddressedMaskedWordIO();
            }
        } else {
            if (getReadAddress() != null) {
                activeIO = getDescriptor().getConfiguration().getWidth().equals(ChannelWidth.WIDTH8) ? new AddressedByteIO() : new AddressedWordIO();
                addressedActiveIO = activeIO;
            } else {
                activeIO = getDescriptor().getConfiguration().getWidth().equals(ChannelWidth.WIDTH8) ? new ByteIO() : null;
                addressedActiveIO = getDescriptor().getConfiguration().getWidth().equals(ChannelWidth.WIDTH8) ? new AddressedByteIO() : new AddressedWordIO();
            }
        }
        if (activeIO == null ) {
            throw new UnsupportedOperationException("Only 8 bit direct access mode supported");
        }
    }

    @Override
    public PeripheralDescriptor<I2CDevice, I2CDeviceConfig> getDescriptor() {
        return descriptor;
    }

    protected class ByteIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            return I2C.readByteDirect(handle);
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
            return I2C.writeByteDirect(handle, (byte) value);
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeBytesDirect(handle, size, offset, dst.array());
            } else {
                for (int i = 0; i < size; ++i) {
                    write(dst.get(offset + i));
                }
            }
            return size;
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
