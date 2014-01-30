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

public interface I2CBus {

    /**
     * Returns i2c device.
     *
     * @param address i2c device's address
     * @return i2c device represented as byte channel
     * @throws IOException thrown in case this bus cannot return i2c device.
     */
    default I2CByteChannelFactory getByteChannelFactory(int address) throws IOException {
        return new I2CByteChannelFactory(address,this);
    }

    /**
     * Closes this bus. This usually means closing underlying file.
     *
     * @throws IOException thrown in case there are problems closing this i2c bus.
     */
    void close() throws IOException;

    /**
     * Opens this bus.
     *
     * @throws IOException thrown in case there are problems closing this i2c bus.
     */
    void open() throws IOException;

    boolean isOpen();

    int getBusId();

    int write(int deviceAddress, int deviceRegister, byte value)  throws IOException;

    int write(int deviceAddress, byte value) throws IOException;

    byte read(int deviceAddress, int deviceRegister)  throws IOException;

    byte read(int deviceAddress) throws IOException;
}