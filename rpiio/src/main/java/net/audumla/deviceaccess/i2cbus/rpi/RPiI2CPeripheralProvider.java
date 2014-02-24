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

import net.audumla.deviceaccess.*;
import net.audumla.deviceaccess.i2cbus.I2CDevice;
import net.audumla.deviceaccess.i2cbus.I2CDeviceConfig;
import net.audumla.deviceaccess.i2cbus.rpi.jni.RPiI2CNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RPiI2CPeripheralProvider implements PeripheralProvider<I2CDevice, I2CDeviceConfig> {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CPeripheralProvider.class);

    public static final int I2C_CLOCK_FREQ_MIN = 10000;
    public static final int I2C_CLOCK_FREQ_MAX = 400000;

    protected static String DEVICE_FILE_PREFIX = "/dev/i2c-";

    static final Map<String, Integer> deviceHandleMap = new HashMap<>();

    @Override
    public I2CDevice open(I2CDeviceConfig config, String[] properties, int mode) throws PeripheralNotFoundException, UnavailablePeripheralException, PeripheralConfigInvalidException, UnsupportedAccessModeException, IOException {

        String deviceName = config.getDeviceName() == null ? DEVICE_FILE_PREFIX + (config.getDeviceNumber() == PeripheralConfig.DEFAULT ? 1 : config.getDeviceNumber()) : config.getDeviceName();
        int deviceNumber = Integer.parseInt(deviceName.substring(DEVICE_FILE_PREFIX.length()));
        String name = "I2C Device [Bus:" + String.valueOf(deviceNumber) + "][Addr:0x" + String.valueOf(Integer.toHexString(config.getAddress())) + "]";

        synchronized (deviceHandleMap) {
            String id = String.valueOf(deviceNumber) + ":" + String.valueOf(config.getAddress());
            Integer handle = deviceHandleMap.get(id);
            if (handle == null) {
                handle = RPiI2CNative.open(deviceNumber, config.getAddress());
                if (handle < 0) {
                    throw new IOException("Cannot open " + name + " received " + handle);
                }
                deviceHandleMap.put(id, handle);
                logger.debug("Opened " + name);
            } else {
                logger.debug("Found " + name);
            }
            int freq = config.getClockFrequency();
            if (config.getClockFrequency() == PeripheralConfig.DEFAULT) {
                freq = RPiI2CNative.getClock(deviceNumber);
            } else {
                if (config.getClockFrequency() < I2C_CLOCK_FREQ_MIN) {
                    freq = I2C_CLOCK_FREQ_MIN;
                } else {
                    if (config.getClockFrequency() > I2C_CLOCK_FREQ_MAX) {
                        freq = I2C_CLOCK_FREQ_MAX;
                    }
                }
                RPiI2CNative.setClock(deviceNumber, freq);
            }

            I2CDeviceConfig nc = new I2CDeviceConfig(deviceName, deviceNumber, config.getAddress(), config.getAddressSize(), freq, config.getWidth());
            PeripheralManager.ReferencedPeripheralDescriptor<I2CDevice, I2CDeviceConfig> desc = new PeripheralManager.ReferencedPeripheralDescriptor<I2CDevice, I2CDeviceConfig>(nc, 0, name, properties);
            return new RPiI2CDevice(handle, desc);
        }
    }

    @Override
    public Class<? super I2CDeviceConfig> getConfigType() {
        return I2CDeviceConfig.class;
    }

    @Override
    public Class<I2CDevice> getType() {
        return I2CDevice.class;
    }

    @Override
    public boolean matches(String[] properties) {
        return false;
    }
}
