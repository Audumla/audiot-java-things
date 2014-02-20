package net.audumla.deviceaccess.i2cbus.rpi.jni;

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

public class RPiI2CNative {
    // private constructor
    private RPiI2CNative() {
        // forbid object construction
    }

    static {
        // Load the platform library
        LibraryLoader.load("audumlaRPiI2C", "audumlaRPiI2C.so");
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
    public static native int open(String bus, int deviceAddress);

    /**
     * Closes linux file.
     *
     * @param fd file descriptor
     */
    public static native int close(int fd);

    /**
     * Writes bytes to i2c device. Width is not required when the read does not contain a register address to read from
     *
     * @param fd          file descriptor of i2c bus
     * @param writeBuffer an array to receive the read bytes of size width*readCount
     * @param writeCount  the number of times to read 'width' bytes from the device
     * @param mask        a bit mask that will be applied to every written to the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int write(int fd, int offset, int writeCount, byte[] writeBuffer, byte mask);

    /**
     * Writes bytes to i2c device.
     *
     * @param fd           file descriptor of i2c bus
     * @param localAddress the local register address within the device to read from
     * @param width        the width in bytes of the I2c device
     * @param writeBuffer  an array to receive the read bytes of size width*readCount
     * @param writeCount   the number of times to read 'width' bytes from the device
     * @param mask         an array of length width that contains a bit mask that will be applied to every read from the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int write(int fd, int localAddress, int offset, int width, int writeCount, byte[] writeBuffer, byte[] mask);

    /**
     * Reads bytes from i2c device. Width is not required when the read does not contain a register address to read from
     *
     * @param fd         file descriptor of i2c bus
     * @param readBuffer an array to receive the read bytes of size width*readCount
     * @param readCount  the number of times to read 'width' bytes from the device
     * @param mask       a bit mask that will be applied to every byte read from the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int read(int fd, int offset, int readCount, byte[] readBuffer, byte mask);

    /**
     * Reads bytes from i2c device.
     *
     * @param fd           file descriptor of i2c bus
     * @param localAddress the local register address within the device to read from
     * @param width        the width in bytes of the I2c device
     * @param readBuffer   an array to receive the read bytes of size width*readCount
     * @param readCount    the number of times to read 'width' bytes from the device
     * @param mask         an array of length width that contains a bit mask that will be applied to every read from the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int read(int fd, int localAddress, int offset, int width, int readCount, byte[] readBuffer, byte[] mask);
}

