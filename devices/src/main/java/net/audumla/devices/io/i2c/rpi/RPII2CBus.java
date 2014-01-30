package net.audumla.devices.io.i2c.rpi;

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

import com.pi4j.jni.I2C;
import net.audumla.devices.io.i2c.I2CBus;
import net.audumla.devices.io.i2c.I2CByteChannelFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RPII2CBus implements I2CBus {

    /**
     * File handle for this i2c bus
     */
    protected int fd;
    private final int busid;

    public RPII2CBus(int busid) throws IOException {
        this.busid = busid;
    }

    /**
     * Closes this i2c bus
     *
     * @throws IOException never in this implementation
     */
    @Override
    public void close() throws IOException {
        I2C.i2cClose(fd);
    }

    @Override
    public void open() throws IOException {
        fd = I2C.i2cOpen("/dev/i2c-" + busid);
        if (fd < 0) {
            throw new IOException("Cannot open I2C Bus [/dev/i2c-" + busid + "] received " + fd);
        }
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public int getBusId() {
        return busid;
    }

    @Override
    public int write(int deviceAddress, int deviceRegister, byte value) throws IOException {
        return I2C.i2cWriteByteDirect(fd, deviceAddress, value);
    }

    @Override
    public int write(int deviceAddress, byte value) throws IOException {
        return I2C.i2cWriteByteDirect(fd, deviceAddress, value);
    }

    @Override
    public byte read(int deviceAddress, int deviceRegister) throws IOException {
        return (byte) I2C.i2cReadByte(fd, deviceAddress, deviceRegister);
    }

    @Override
    public byte read(int deviceAddress) throws IOException {
        return (byte) I2C.i2cReadByteDirect(fd, deviceAddress);
    }

    private static Map<Integer, I2CBus> busRegistry = new HashMap<Integer, I2CBus>();

    static public I2CBus getInstance(int busid) throws IOException {
        I2CBus bus = busRegistry.get(busid);
        if (bus == null) {
            bus = new RPII2CBus(busid);
            busRegistry.put(busid, bus);
        }
        return bus;
    }

}

