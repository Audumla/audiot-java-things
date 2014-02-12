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

import java.io.IOException;

public interface I2CDeviceFactory {
    /**
     * Opens linux file for r/w returning file handle.
     *
     * @param device file name of device. For i2c should be /dev/i2c-0 or /dev/i2c-1 for first or second bus.
     * @return file descriptor or i2c bus.
     */
    /**
     * Opens linux file for r/w returning file handle.
     *
     * @param bus The bus number to open
     */
    I2CDevice open(int bus, int deviceAddress) throws IOException;
}
