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

import java.io.IOException;

public interface I2CDevice {

    /**
     * This method writes one byte directly to i2c device.
     *
     * @param b byte to be written
     *
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    void write(byte b) throws IOException;

    /**
     * This method writes several bytes directly to the i2c device from given buffer at given offset.
     *
     * @param buffer buffer of data to be written to the i2c device in one go
     * @param offset offset in buffer
     * @param size number of bytes to be written
     *
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    void write(byte[] buffer, int offset, int size) throws IOException;

    /**
     * This method writes one byte to i2c device.
     *
     * @param address local address in the i2c device
     * @param b byte to be written
     *
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    void write(int address, byte b) throws IOException;

    /**
     * This method writes several bytes to the i2c device from given buffer at given offset.
     *
     * @param address local address in the i2c device
     * @param buffer buffer of data to be written to the i2c device in one go
     * @param offset offset in buffer
     * @param size number of bytes to be written
     *
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    void write(int address, byte[] buffer, int offset, int size) throws IOException;

    /**
     * This method reads one byte from the i2c device. Result is between -128 and 127.
     *
     * @return read byte
     *
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    int read() throws IOException;

    /**
     * This method reads bytes directly from the i2c device to given buffer at asked offset.
     *
     * @param buffer buffer of data to be read from the i2c device in one go
     * @param offset offset in buffer
     * @param size number of bytes to be read
     *
     * @return number of bytes read
     *
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    int read(byte[] buffer, int offset, int size) throws IOException;

    /**
     * This method reads one byte from the i2c device. Result is between -128 and 127.
     *
     * @param address local address in the i2c device
     * @return the value read from the device
     *
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    int read(int address) throws IOException;

    /**
     * This method reads bytes from the i2c device to given buffer at asked offset.
     *
     * @param address local address in the i2c device
     * @param buffer buffer of data to be read from the i2c device in one go
     * @param offset offset in buffer
     * @param size number of bytes to be read
     *
     * @return number of bytes read
     *
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    int read(int address, byte[] buffer, int offset, int size) throws IOException;
}