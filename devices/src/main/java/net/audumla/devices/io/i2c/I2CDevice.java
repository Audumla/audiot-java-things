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

public interface I2CDevice {


    /**
     * Closes linux device.
     */
    void close();

    /**
     * Writes one byte to i2c. It uses ioctl to define device address and then writes one byte.
     *
     * @param data byte to be written to the device
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeByteDirect(byte data);

    /**
     * Writes several bytes to i2c. It uses ioctl to define device address and then writes number of bytes defined
     * in size argument.
     *
     * @param size   number of bytes to be written
     * @param offset offset in buffer to read from
     * @param buffer data buffer to be written
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeBytesDirect(int size, int offset, byte[] buffer);

    /**
     * Writes one 8 bit byte to i2c. It uses ioctl to define device address and then writes two bytes: address in
     * the device itself and value.
     *
     * @param localAddress address in the device
     * @param data         byte to be written to the device
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeByte(int localAddress, byte data);

    /**
     * Writes one 16 bit word to i2c. It uses ioctl to define device address and then writes two bytes: address in
     * the device itself and value.
     *
     * @param localAddress address in the device
     * @param data         word to be written to the device
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeWord(int localAddress, char data);

    /**
     * Writes several bytes to i2c. It uses ioctl to define device address and then writes number of bytes defined
     * in size argument plus one.
     *
     * @param localAddress address in the device
     * @param size         number of bytes to be written
     * @param offset       offset in buffer to read from
     * @param buffer       data buffer to be written
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeBytes(int localAddress, int size, int offset, byte[] buffer);

    /**
     * Writes several 16 bit words to i2c. It uses ioctl to define device address and then writes number of bytes defined
     * in size argument plus one.
     *
     * @param localAddress address in the device
     * @param size         number of bytes to be written
     * @param offset       offset in buffer to read from
     * @param buffer       data buffer to be written
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeWords(int localAddress, int size, int offset, char[] buffer);

    /**
     * Reads one byte from i2c device. It uses ioctl to define device address and then reads one byte.
     *
     * @return positive number (or zero) to 255 if read was successful. Negative number if reading failed.
     */
    byte readByteDirect();

    /**
     * Reads one byte from i2c device. It uses ioctl to define device address, writes addres in device and then reads
     * one byte.
     *
     * @param localAddress address in the device
     * @return positive number (or zero) to 255 if read was successful. Negative number if reading failed.
     */
    byte readByte(int localAddress);

    /**
     * Reads one 16 bit word from i2c device. It uses ioctl to define device address, writes addres in device and then reads
     * one byte.
     *
     * @param localAddress address in the device
     * @return positive number (or zero) to 255 if read was successful. Negative number if reading failed.
     */
    char readWord(int localAddress);

    /**
     * Writes one byte to i2c with a bit mask applied so that only those bits in the mask are updated
     *
     * @param data byte to be written to the device
     * @param mask the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeByteDirectMask(byte data, byte mask);

    /**
     * Writes several bytes to i2c applying a bit mask to the bits written.
     *
     * @param size   number of bytes to be written
     * @param offset offset in buffer to read from
     * @param buffer data buffer to be written
     * @param mask   the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeBytesDirectMask(int size, int offset, byte[] buffer, byte mask);

    /**
     * Writes one byte to i2c with a bit mask applied so that only those bits in the mask are updated
     *
     * @param localAddress address in the device
     * @param data         byte to be written to the device
     * @param mask         the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeByteMask(int localAddress, byte data, byte mask);

    /**
     * Writes several bytes to i2c applying a bit mask to the bits written.
     *
     * @param localAddress address in the device
     * @param size         number of bytes to be written
     * @param offset       offset in buffer to read from
     * @param buffer       data buffer to be written
     * @param mask         the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeBytesMask(int localAddress, int size, int offset, byte[] buffer, byte mask);

    /**
     * Writes one 16 bit word to i2c applying a bit mask to the bits written.
     *
     * @param localAddress address in the device
     * @param data         word to be written to the device
     * @param mask         the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeWordMask(int localAddress, char data, char mask);

    /**
     * Writes one 16 bit word to i2c applying a bit mask to the bits written.
     *
     * @param localAddress address in the device
     * @param size         number of bytes to be written
     * @param offset       offset in buffer to read from
     * @param buffer       data buffer to be written
     * @param mask         the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    int writeWordsMask(int localAddress, int size, int offset, char[] buffer, char mask);
}

