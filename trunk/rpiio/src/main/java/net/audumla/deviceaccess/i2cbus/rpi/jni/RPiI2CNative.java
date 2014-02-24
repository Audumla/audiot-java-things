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
     * @param bus           file name of device. For i2c should be /dev/i2c-0 or /dev/i2c-1 for first or second bus.
     * @param deviceAddress the address on the bus of the device
     * @return identifier for the i2c bus.
     */
    public static native int open(int bus, int deviceAddress);

    /**
     * Closes linux file.
     *
     * @param bus identifier of i2c bus
     */
    public static native int close(int bus);

    /**
     * Sets the I2C clock to the value specified. Any value that not within the bounds of 10000
     * to 400000 will be changed to these values
     *
     * @param bus       identifier of i2c bus
     * @param frequency The frequency value to set the I2C clock (minimum 10000, maximum 400000)
     * @return The previous frequency
     */
    public static native int setClock(int bus, int frequency);

    /**
     * Sets the I2C clock to the value specified. Any value that not within the bounds of 10000
     * to 400000 will be changed to these values
     *
     * @return the current frequency for the given bus
     */
    public static native int getClock(int bus);

    /**
     * Writes a byte to the local address of the i2c device
     *
     * @param bus          identifier of i2c bus
     * @param address      the address of the device on the i2c bus
     * @param localAddress the local register address within the device to read from
     * @param value        an array to receive the read bytes of size width*readCount
     * @param mask         a bit mask that will be applied to every byte written to the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int write(int bus, int address, byte localAddress, byte value, byte mask);

    /**
     * Reads a byte from the local address of the i2c device
     *
     * @param bus          identifier of i2c bus
     * @param address      the address of the device on the i2c bus
     * @param localAddress the local register address within the device to read from
     * @return returns the value read from the device
     */
    public static native byte read(int bus, int address, byte localAddress);

    /**
     * Writes a byte to the i2c device
     *
     * @param bus     identifier of i2c bus
     * @param address the address of the device on the i2c bus
     * @param value   an array to receive the read bytes of size width*readCount
     * @param mask    a bit mask that will be applied to every byte written to the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int write(int bus, int address, byte value, byte mask);

    /**
     * Reads a byte from the i2c device
     *
     * @param bus     identifier of i2c bus
     * @param address the address of the device on the i2c bus
     * @return returns the value read from the device
     */
    public static native byte read(int bus, int address);

    /**
     * Writes bytes to the i2c device
     *
     * @param bus         identifier of i2c bus
     * @param address     the address of the device on the i2c bus
     * @param offset      the offset within the buffer to start the operation. The actual byte offset will be evaluated as offset * width
     * @param writeBuffer an array to receive the read bytes of size width*readCount
     * @param writeCount  the number of times to read 'width' bytes from the device
     * @param mask        a bit mask that will be applied to every written to the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int write(int bus, int address, int offset, int writeCount, byte[] writeBuffer, byte mask);

    /**
     * Writes bytes to the i2c device.
     *
     * @param bus          identifier of i2c bus
     * @param address      the address of the device on the i2c bus
     * @param localAddress the local register address within the device to read from
     * @param offset       the offset within the buffer to start the operation. The actual byte offset will be evaluated as offset * width
     * @param width        the width in bytes for each read or write of the I2c device
     * @param writeBuffer  an array to receive the read bytes of size width*readCount
     * @param writeCount   the number of times to read 'width' bytes from the device
     * @param mask         an array of length width that contains a bit mask that will be applied to every read from the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int write(int bus, int address, byte localAddress, int offset, int width, int writeCount, byte[] writeBuffer, byte[] mask);

    /**
     * Reads bytes from the i2c device
     *
     * @param bus        identifier of i2c bus
     * @param address    the address of the device on the i2c bus
     * @param offset     the offset within the buffer to start the operation. The actual byte offset will be evaluated as offset * width
     * @param readBuffer an array to receive the read bytes of size width*readCount
     * @param readCount  the number of times to read 'width' bytes from the device
     * @param mask       a bit mask that will be applied to every byte read from the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int read(int bus, int address, int offset, int readCount, byte[] readBuffer, byte mask);

    /**
     * Reads bytes from the i2c device.
     *
     * @param bus          identifier of i2c bus
     * @param address      the address of the device on the i2c bus
     * @param localAddress the local register address within the device to read from
     * @param offset       the offset within the buffer to start the operation. The actual byte offset will be evaluated as offset * width
     * @param width        the width in bytes for each read or write of the I2c device
     * @param readBuffer   an array to receive the read bytes of size width*readCount
     * @param readCount    the number of times to read 'width' bytes from the device
     * @param mask         contains a bit mask that will be applied to every read from the device
     * @return return a negative value if an error was encountered otherwise the number of bytes read
     */
    public static native int read(int bus, int address, byte localAddress, int offset, int width, int readCount, byte[] readBuffer, byte[] mask);
}

