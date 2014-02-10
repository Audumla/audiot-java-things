package net.audumla.devices.io.i2c.jni.rpi;

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

import net.audumla.utils.jni.LibraryLoader;

public class I2C {
    // private constructor
    private I2C() {
        // forbid object construction
    }

    static {
        // Load the platform library
        LibraryLoader.load("AudumlaRPi","audumlaRPi.so");
    }

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
     * @return file descriptor or i2c bus.
     */
    public static native int open(String bus,int deviceAddress);

    /**
     * Closes linux file.
     *
     * @param fd file descriptor
     */
    public static native int close(int fd);

    /**
     * Writes one byte to i2c. It uses ioctl to define device address and then writes one byte.
     *
     * @param fd            file descriptor of i2c bus
     * @param data          byte to be written to the device
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeByteDirect(int fd, byte data);

    /**
     * Writes several bytes to i2c. It uses ioctl to define device address and then writes number of bytes defined
     * in size argument.
     *
     * @param fd            file descriptor of i2c bus
     * @param size          number of bytes to be written
     * @param offset        offset in buffer to read from
     * @param buffer        data buffer to be written
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeBytesDirect(int fd, int size, int offset, byte[] buffer);

    /**
     * Writes one 8 bit byte to i2c. It uses ioctl to define device address and then writes two bytes: address in
     * the device itself and value.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param data          byte to be written to the device
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeByte(int fd, int localAddress, byte data);

    /**
     * Writes one 16 bit word to i2c. It uses ioctl to define device address and then writes two bytes: address in
     * the device itself and value.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param data          word to be written to the device
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeWord(int fd, int localAddress, char data);

    /**
     * Writes several bytes to i2c. It uses ioctl to define device address and then writes number of bytes defined
     * in size argument plus one.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param size          number of bytes to be written
     * @param offset        offset in buffer to read from
     * @param buffer        data buffer to be written
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeBytes(int fd, int localAddress, int size, int offset, byte[] buffer);

    /**
     * Writes several 16 bit words to i2c. It uses ioctl to define device address and then writes number of bytes defined
     * in size argument plus one.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param size          number of bytes to be written
     * @param offset        offset in buffer to read from
     * @param buffer        data buffer to be written
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeWords(int fd, int localAddress, int size, int offset, char[] buffer);

    /**
     * Reads one byte from i2c device. It uses ioctl to define device address and then reads one byte.
     *
     * @param fd            file descriptor of i2c bus
     * @return positive number (or zero) to 255 if read was successful. Negative number if reading failed.
     */
    public static native byte readByteDirect(int fd);

    /**
     * Reads one byte from i2c device. It uses ioctl to define device address, writes addres in device and then reads
     * one byte.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @return positive number (or zero) to 255 if read was successful. Negative number if reading failed.
     */
    public static native byte readByte(int fd, int localAddress);

    /**
     * Reads one 16 bit word from i2c device. It uses ioctl to define device address, writes addres in device and then reads
     * one byte.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @return positive number (or zero) to 255 if read was successful. Negative number if reading failed.
     */
    public static native int readWord(int fd, int localAddress);

    /**
     * Writes one byte to i2c with a bit mask applied so that only those bits in the mask are updated
     *
     * @param fd            file descriptor of i2c bus
     * @param data          byte to be written to the device
     * @param mask          the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeByteDirectMask(int fd, byte data, byte mask);

    /**
     * Writes several bytes to i2c applying a bit mask to the bits written.
     *
     * @param fd            file descriptor of i2c bus
     * @param size          number of bytes to be written
     * @param offset        offset in buffer to read from
     * @param buffer        data buffer to be written
     * @param mask          the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeBytesDirectMask(int fd, int size, int offset, byte[] buffer, byte mask);

    /**
     * Writes one byte to i2c with a bit mask applied so that only those bits in the mask are updated
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param data          byte to be written to the device
     * @param mask          the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeByteMask(int fd, int localAddress, byte data, byte mask);

    /**
     * Writes several bytes to i2c applying a bit mask to the bits written.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param size          number of bytes to be written
     * @param offset        offset in buffer to read from
     * @param buffer        data buffer to be written
     * @param mask          the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeBytesMask(int fd, int localAddress, int size, int offset, byte[] buffer, byte mask);

    /**
     * Writes one 16 bit word to i2c applying a bit mask to the bits written.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param data          word to be written to the device
     * @param mask          the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeWordMask(int fd, int localAddress, char data, byte mask);

    /**
     * Writes one 16 bit word to i2c applying a bit mask to the bits written.
     *
     * @param fd            file descriptor of i2c bus
     * @param localAddress  address in the device
     * @param size          number of bytes to be written
     * @param offset        offset in buffer to read from
     * @param buffer        data buffer to be written
     * @param mask          the bit mask to apply
     * @return result of operation. Zero if everything is OK, less than zero if there was an error.
     */
    public static native int writeWordsMask(int fd, int localAddress, int size, int offset, char[] buffer, byte mask);
}

