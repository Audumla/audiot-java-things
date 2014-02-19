package net.audumla.deviceaccess.rpi;

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
import net.audumla.deviceaccess.i2cbus.I2CDevice;
import net.audumla.deviceaccess.i2cbus.I2CDeviceConfig;
import net.audumla.deviceaccess.i2cbus.I2CMessage;
import net.audumla.deviceaccess.impl.DefaultAddressablePeripheralChannel;
import net.audumla.devices.io.i2c.jni.rpi.I2C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RPiI2CDevice extends DefaultAddressablePeripheralChannel<I2CDevice, I2CDeviceConfig, I2CMessage> implements I2CDevice {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CDevice.class);

    private int handle;
    private DeviceBusIO activeAddressIO;
    private DeviceBusIO activeDirectIO;

    @Override
    public int read(int subAddress) throws IOException {
        return activeAddressIO.read(subAddress);
    }

    @Override
    public int read(int subAddress, ByteBuffer dst) throws IOException {
        return activeAddressIO.read(subAddress,dst,dst.position(),dst.remaining());
    }

    @Override
    public int read(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
        return activeAddressIO.read(subAddress,dst,offset,size);
    }

    @Override
    public int write(int subAddress, ByteBuffer dst) throws IOException {
        return activeAddressIO.write(subAddress, dst, 0, dst.limit());
    }

    @Override
    public int write(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
        return activeAddressIO.write(subAddress,dst,offset,size);
    }

    @Override
    public int write(int subAddress, byte... data) throws IOException {
        return data.length == 1 ? activeAddressIO.write(subAddress,data[0]) : write(subAddress, ByteBuffer.wrap(data));
    }

    @Override
    public int write(byte... data) throws IOException {
        return data.length == 1 ? activeAddressIO.write(data[0]) : write(ByteBuffer.wrap(data));
    }

    @Override
    public int write(ByteBuffer dst, int offset, int size) throws IOException {
        return activeAddressIO.write(dst,offset,size);
    }

    @Override
    public int read() throws IOException {
        return activeAddressIO.read();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return activeAddressIO.read(dst,dst.position(),dst.remaining());
    }

    @Override
    public int read(ByteBuffer dst, int offset, int size) throws IOException {
        return activeAddressIO.read(dst,offset,size);
    }

    @Override
    public I2CMessage createMessage() {
        return new I2CMessage();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return 0;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void setReadAddress(Integer addr) {
        super.setReadAddress(addr);
    }

    @Override
    public void setWriteAddress(Integer addr) {
        super.setWriteAddress(addr);
    }

    @Override
    public void setAddressSize(Integer size) {
        super.setAddressSize(size);
    }

    @Override
    public void setWidth(ChannelWidth width) {
        super.setWidth(width);
    }

    @Override
    public void setMask(Integer mask) {
        super.setMask(mask);
    }

    public class ByteIO implements DeviceBusIO {

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
        public int read(int subAddress) throws IOException {
            return I2C.readByte(handle, subAddress);
        }

        @Override
        public int read(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read(subAddress));
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

        @Override
        public int write(int subAddress, int value) throws IOException {
            return I2C.writeByte(handle, subAddress, (byte) value);
        }

        @Override
        public int write(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeBytes(handle, subAddress, size, offset, dst.array());
            } else {
                for (int i = 0; i < size; ++i) {
                    write(subAddress, dst.get(offset + i));
                }
            }
            return size;
        }
    }

    public class MaskedByteIO implements DeviceBusIO {

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
        public int read(int subAddress) throws IOException {
            return I2C.readByte(handle, subAddress) & getMask().byteValue();
        }

        @Override
        public int read(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read(subAddress));
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

        @Override
        public int write(int subAddress, int value) throws IOException {
            return I2C.writeByte(handle, subAddress, (byte) (value & getMask().byteValue()));
        }

        @Override
        public int write(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeBytesMask(handle, subAddress, size, offset, dst.array(),getMask().byteValue());
            } else {
                for (int i = 0; i < size; ++i) {
                    write(subAddress, dst.get(offset + i));
                }
            }
            return size;
        }
    }

    public class WordIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read());
            }
            return size;
        }

        @Override
        public int read(int subAddress) throws IOException {
            return I2C.readWord(handle, subAddress);
        }

        @Override
        public int read(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read(subAddress));
            }
            return size;
        }

        @Override
        public int write(int value) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                throw new UnsupportedOperationException();
            } else {
                for (int i = 0; i < size; ++i) {
                    write(dst.get(offset + i));
                }
            }
            return size;
        }

        @Override
        public int write(int subAddress, int value) throws IOException {
            return I2C.writeWord(handle, subAddress, (char) value);
        }

        @Override
        public int write(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeWords(handle, subAddress, size / 2, offset / 2, dst.asCharBuffer().array());
            } else {
                for (int i = 0; i < size/2; ++i) {
                    write(subAddress, dst.getChar(offset + i));
                }
            }
            return size;
        }
    }

    public class MaskedWordIO implements DeviceBusIO {

        @Override
        public int read() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read(ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read());
            }
            return size;
        }

        @Override
        public int read(int subAddress) throws IOException {
            return I2C.readWord(handle, subAddress) & getMask();
        }

        @Override
        public int read(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            for (int i = 0; i < size; ++i) {
                dst.put(offset + i, (byte) read(subAddress));
            }
            return size;
        }

        @Override
        public int write(int value) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int write(ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                throw new UnsupportedOperationException();
            } else {
                for (int i = 0; i < size; ++i) {
                    write(dst.get(offset + i));
                }
            }
            return size;
        }

        @Override
        public int write(int subAddress, int value) throws IOException {
            return I2C.writeWord(handle, subAddress, (char) (value & getMask()));
        }

        @Override
        public int write(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
            if (dst.hasArray()) {
                I2C.writeWordsMask(handle, subAddress, size/2, offset/2, dst.asCharBuffer().array(), (char) getMask().intValue());
            } else {
                for (int i = 0; i < size/2; ++i) {
                    write(subAddress, dst.getChar(offset + i));
                }
            }
            return size;
        }
    }
}
