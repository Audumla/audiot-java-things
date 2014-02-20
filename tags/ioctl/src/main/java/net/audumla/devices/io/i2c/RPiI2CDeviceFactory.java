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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RPiI2CDeviceFactory implements I2CDeviceFactory{
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CDeviceFactory.class);

    static final Map<String, Integer> deviceHandleMap = new HashMap<>();

    public RPiI2CDeviceFactory() {
    }

    @Override
    public I2CDevice open(int bus, int address) throws IOException {
        synchronized (deviceHandleMap) {
            String id = String.valueOf(bus) + ":" + String.valueOf(address);
            Integer handle = deviceHandleMap.get(id);
            if (handle == null) {
                handle = net.audumla.devices.io.i2c.jni.rpi.I2C.open("/dev/i2c-" + bus, address);
                if (handle < 0) {
                    throw new IOException("Cannot open I2C Bus [/dev/i2c-" + bus + "] received " + handle);
                }
                deviceHandleMap.put(id, handle);
                logger.debug("Opened Device on '/dev/i2c-" + bus + "' at Address 0x" + Integer.toHexString(address));
            } else {
                logger.debug("Found open Device on '/dev/i2c-" + bus + "' at Address 0x" + Integer.toHexString(address));
            }
            return new RPiI2CDevice(handle);
        }
    }
}
