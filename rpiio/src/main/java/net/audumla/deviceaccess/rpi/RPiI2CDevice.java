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

import net.audumla.deviceaccess.PeripheralDescriptor;
import net.audumla.deviceaccess.i2cbus.I2CDevice;
import net.audumla.deviceaccess.i2cbus.I2CDeviceConfig;
import net.audumla.deviceaccess.i2cbus.I2CMessage;
import net.audumla.deviceaccess.impl.DefaultPeripheralAddressableChannel;
import net.audumla.devices.io.i2c.jni.rpi.I2C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RPiI2CDevice extends DefaultPeripheralAddressableChannel<I2CDevice,I2CDeviceConfig,I2CMessage> implements I2CDevice {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CDevice.class);

    private Integer handle;


    @Override
    public int read(int subAddress) throws IOException {
        return I2C.readByte(handle,subAddress);
    }

    @Override
    public int read(int subAddress, ByteBuffer dst) throws IOException {
        return read(subAddress,dst,dst.position(),dst.remaining());
    }

    @Override
    public int read(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
        for (int i = 0; i < size; ++i) {
            dst.put(offset + i, I2C.readByte(handle, subAddress));
        }
        return size;
    }

    @Override
    public int read(int subAddress, ByteBuffer dst, int offset, int size, int mask) throws IOException {
        for (int i = 0; i < size; ++i) {
            dst.put(offset+i, (byte) (I2C.readByte(handle, subAddress) & mask));
        }
        return size;
    }

    @Override
    public int write(int subAddress, ByteBuffer dst) throws IOException {
        return write(subAddress,dst,0,dst.limit());
    }

    @Override
    public int write(int subAddress, ByteBuffer dst, int offset, int size) throws IOException {
        if (dst.hasArray()) {
            return I2C.writeBytes(handle,subAddress,size,offset,dst.array());
        }
        else {
            for (int i = 0; i < size; ++i) {
                I2C.writeByte(handle,subAddress,dst.get(offset+i));
            }
            return size;
        }
    }

    @Override
    public int write(int subAddress, ByteBuffer dst, int offset, int size, int mask) throws IOException {
        if (dst.hasArray()) {
            return I2C.writeBytesMask(handle, subAddress, size, offset, dst.array(), (byte) mask);
        }
        else {
            for (int i = 0; i < size; ++i) {
                I2C.writeByte(handle,subAddress, (byte) (dst.get(offset+i)&mask));
            }
            return size;
        }
    }

    @Override
    public int write(int subAddress, int... data) throws IOException {
        for (int aData : data) {
            I2C.writeByte(handle, subAddress, (byte) (aData));
        }
        return data.length;
    }

    @Override
    public int write(int... data) throws IOException {
        for (int aData : data) {
            I2C.writeByteDirect(handle, (byte) (aData));
        }
        return data.length;
    }

    @Override
    public int write(ByteBuffer dst, int mask) throws IOException {
        return write(dst, 0, dst.limit());
    }

    @Override
    public int write(ByteBuffer dst, int offset, int size, int mask) throws IOException {
        if (dst.hasArray()) {
            return I2C.writeBytesDirectMask(handle, size, offset, dst.array(), (byte) mask);
        }
        else {
            for (int i = 0; i < size; ++i) {
                I2C.writeByteDirect(handle, (byte) (dst.get(offset + i) & mask));
            }
            return size;
        }
    }

    @Override
    public int write(ByteBuffer dst, int offset, int size) throws IOException {
        if (dst.hasArray()) {
            return I2C.writeBytesDirect(handle, size, offset, dst.array());
        }
        else {
            for (int i = 0; i < size; ++i) {
                I2C.writeByteDirect(handle, dst.get(offset+i));
            }
            return size;
        }
    }

    @Override
    public int read() throws IOException {
        return I2C.readByteDirect(handle);
    }

    @Override
    public int read(ByteBuffer dst, int mask) throws IOException {
        return read(dst,dst.position(),dst.remaining(),mask);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return read(dst,dst.position(),dst.remaining());
    }

    @Override
    public int read(ByteBuffer dst, int offset, int size, int mask) throws IOException {
        return 0;
    }

    @Override
    public int read(ByteBuffer dst, int offset, int size) throws IOException {
        return 0;
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
    public I2CMessage createMessage() {
        return new I2CMessage(false);
    }
}
