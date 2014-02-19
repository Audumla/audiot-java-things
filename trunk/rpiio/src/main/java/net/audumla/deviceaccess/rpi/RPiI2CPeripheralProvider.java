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

import net.audumla.deviceaccess.*;
import net.audumla.deviceaccess.i2cbus.I2CDevice;
import net.audumla.deviceaccess.i2cbus.I2CDeviceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RPiI2CPeripheralProvider implements PeripheralProvider<I2CDevice,I2CDeviceConfig> {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CPeripheralProvider.class);

    public static final int I2C_CLOCK_FREQ_MIN = 10000;
    public static final int	I2C_CLOCK_FREQ_MAX = 400000;

    @Override
    public I2CDevice open(I2CDeviceConfig config, String[] properties, int mode) throws PeripheralNotFoundException, UnavailablePeripheralException, PeripheralConfigInvalidException, UnsupportedAccessModeException, IOException {
        PeripheralManager.ReferencedPeripheralDescriptor<I2CDevice, I2CDeviceConfig> desc = new PeripheralManager.ReferencedPeripheralDescriptor<I2CDevice, I2CDeviceConfig>(config,0,"",properties);
        return null;
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
