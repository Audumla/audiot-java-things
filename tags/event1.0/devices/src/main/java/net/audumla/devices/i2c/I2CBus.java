package net.audumla.devices.i2c;

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

import net.audumla.devices.i2c.rpi.RPII2CBusFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public interface I2CBus {

    /**
     * Returns i2c device.
     *
     * @param address i2c device's address
     * @return i2c device
     * @throws IOException thrown in case this bus cannot return i2c device.
     */
    I2CDevice getDevice(int address) throws IOException;

    /**
     * Closes this bus. This usually means closing underlying file.
     *
     * @throws IOException thrown in case there are problems closing this i2c bus.
     */
    void close() throws IOException;

    public interface I2CBusFactory {
        I2CBus getInstance(int busid) throws IOException;
    }

    static final AtomicReference<I2CBusFactory> factory = new AtomicReference<I2CBusFactory>(new RPII2CBusFactory());

    static I2CBusFactory getI2CBusFactory() {
        return factory.get();
    }

    static void setI2CBusFactory(I2CBusFactory f) {
        factory.set(f);

    }

}