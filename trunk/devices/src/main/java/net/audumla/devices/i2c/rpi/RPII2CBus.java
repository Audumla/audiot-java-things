package net.audumla.devices.i2c.rpi;

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

import com.pi4j.io.i2c.impl.I2CDeviceImpl;
import com.pi4j.jni.I2C;
import net.audumla.devices.i2c.I2CBus;
import net.audumla.devices.i2c.I2CDevice;

import java.io.IOException;

public class RPII2CBus implements I2CBus {

    public RPII2CBus(int busid) throws IOException {
        fd = I2C.i2cOpen("/dev/i2c-" + busid);
        if (fd < 0) {
            throw new IOException("Cannot open I2C Bus [/dev/i2c-" + busid + "] received " + fd);
        }
    }

    /**
     * File handle for this i2c bus
     */
    protected int fd;

    /**
     * Returns i2c device implementation
     *
     * @param address address of i2c device
     * @return implementation of i2c device with given address
     * @throws IOException never in this implementation
     */
    @Override
    public I2CDevice getDevice(int address) throws IOException {
        return new RPII2CDevice(this, address);
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
}