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

import net.audumla.devices.io.i2c.jni.rpi.I2C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RPiI2CDevice implements I2CDevice{
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CDevice.class);

    private Integer handle;

    RPiI2CDevice(int handle) throws IOException {
        this.handle = handle;
    }

    @Override
    public void close() {
        synchronized (RPiI2CDeviceFactory.deviceHandleMap) {
            if (handle != null) {
                I2C.close(handle);
                for (Map.Entry<String, Integer> i : new ArrayList<>(RPiI2CDeviceFactory.deviceHandleMap.entrySet())) {
                    if (i.getValue().equals(handle)) {
                        RPiI2CDeviceFactory.deviceHandleMap.remove(i.getKey());
                    }
                }
            }
        }
    }

    @Override
    public int writeByteDirect(byte data) {
        return I2C.writeByteDirect(handle,data);
    }

    @Override
    public int writeBytesDirect(int size, int offset, byte[] buffer) {
        return I2C.writeBytesDirect(handle,size,offset,buffer);
    }

    @Override
    public int writeByte(int localAddress, byte data) {
        return I2C.writeByte(handle,localAddress,data);
    }

    @Override
    public int writeWord(int localAddress, char data) {
        return I2C.writeWord(handle,localAddress,data);
    }

    @Override
    public int writeBytes(int localAddress, int size, int offset, byte[] buffer) {
        return I2C.writeBytes(handle,localAddress,size,offset,buffer);
    }

    @Override
    public int writeWords(int localAddress, int size, int offset, char[] buffer) {
        return I2C.writeWords(handle,localAddress,size,offset,buffer);
    }

    @Override
    public byte readByteDirect() {
        return I2C.readByteDirect(handle);
    }

    @Override
    public byte readByte(int localAddress) {
        return I2C.readByte(handle,localAddress);
    }

    @Override
    public char readWord(int localAddress) {
        return I2C.readWord(handle,localAddress);
    }

    @Override
    public int writeByteDirectMask(byte data, byte mask) {
        return I2C.writeByteDirectMask(handle,data,mask);
    }

    @Override
    public int writeBytesDirectMask(int size, int offset, byte[] buffer, byte mask) {
        return I2C.writeBytesDirectMask(handle,size,offset,buffer,mask);
    }

    @Override
    public int writeByteMask(int localAddress, byte data, byte mask) {
        return I2C.writeByteMask(handle,localAddress,data,mask);
    }

    @Override
    public int writeBytesMask(int localAddress, int size, int offset, byte[] buffer, byte mask) {
        return I2C.writeBytesMask(handle,localAddress,size,offset,buffer,mask);
    }

    @Override
    public int writeWordMask(int localAddress, char data, char mask) {
        return I2C.writeWordMask(handle,localAddress, data, mask);
    }

    @Override
    public int writeWordsMask(int localAddress, int size, int offset, char[] buffer, char mask) {
        return I2C.writeWordsMask(handle,localAddress, size,offset,buffer,mask);
    }
}
