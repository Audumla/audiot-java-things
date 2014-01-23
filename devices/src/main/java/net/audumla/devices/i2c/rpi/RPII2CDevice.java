package net.audumla.devices.i2c.rpi;

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

import com.pi4j.jni.I2C;
import net.audumla.devices.i2c.I2CDevice;

import java.io.IOException;

public class RPII2CDevice implements I2CDevice {

    /**
     * Reference to i2c bus
     */
    private RPII2CBus bus;

    /**
     * I2c device address
     */
    private int deviceAddress;

    /**
     * Constructor.
     *
     * @param bus     i2c bus
     * @param address i2c device address
     */
    public RPII2CDevice(RPII2CBus bus, int address) {
        this.bus = bus;
        this.deviceAddress = address;
    }

    /**
     * This method writes one byte to i2c device.
     *
     * @param data byte to be written
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    @Override
    public void write(byte data) throws IOException {
        int ret = I2C.i2cWriteByteDirect(bus.fd, deviceAddress, data);
        if (ret < 0) {
            throw new IOException("Error writing to " + makeDescription() + ". Got " + ret + ".");
        }
    }

    /**
     * This method writes several bytes to the i2c device from given buffer at given offset.
     *
     * @param buffer buffer of data to be written to the i2c device in one go
     * @param offset offset in buffer
     * @param size   number of bytes to be written
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    @Override
    public void write(byte[] buffer, int offset, int size) throws IOException {

        int ret = I2C.i2cWriteBytesDirect(bus.fd, deviceAddress, size, offset, buffer);
        if (ret < 0) {
            throw new IOException("Error writing to " + makeDescription() + ". Got " + ret + ".");
        }
    }

    /**
     * This method writes one byte to i2c device.
     *
     * @param address local address in the i2c device
     * @param data    byte to be written
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    @Override
    public void write(int address, byte data) throws IOException {
        int ret = I2C.i2cWriteByte(bus.fd, deviceAddress, address, data);
        if (ret < 0) {
            throw new IOException("Error writing to " + makeDescription(address) + ". Got " + ret + ".");
        }
    }

    /**
     * This method writes several bytes to the i2c device from given buffer at given offset.
     *
     * @param address local address in the i2c device
     * @param buffer  buffer of data to be written to the i2c device in one go
     * @param offset  offset in buffer
     * @param size    number of bytes to be written
     * @throws IOException thrown in case byte cannot be written to the i2c device or i2c bus
     */
    @Override
    public void write(int address, byte[] buffer, int offset, int size) throws IOException {

        int ret = I2C.i2cWriteBytes(bus.fd, deviceAddress, address, size, offset, buffer);
        if (ret < 0) {
            throw new IOException("Error writing to " + makeDescription(address) + ". Got " + ret + ".");
        }
    }

    /**
     * This method reads one byte from the i2c device. Result is between -128 and 127.
     *
     * @return the value read from the device
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    @Override
    public int read() throws IOException {
        int ret = I2C.i2cReadByteDirect(bus.fd, deviceAddress);
        if (ret < 0) {
            throw new IOException("Error reading from " + makeDescription() + ". Got " + ret + ".");
        }
        return ret;
    }

    /**
     * <p>This method reads bytes from the i2c device to given buffer at asked offset. </p>
     * <p>
     * <p>Note: Current implementation calls {@link #read(int)}. That means for each read byte
     * i2c bus will send (next) address to i2c device.
     * </p>
     *
     * @param buffer buffer of data to be read from the i2c device in one go
     * @param offset offset in buffer
     * @param size   number of bytes to be read
     * @return number of bytes read
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    @Override
    public int read(byte[] buffer, int offset, int size) throws IOException {
        // It doesn't work for some reason.
        int ret = I2C.i2cReadBytesDirect(bus.fd, deviceAddress, size, offset, buffer);
        if (ret < 0) {
            throw new IOException("Error reading from " + makeDescription() + ". Got " + ret + ".");
        }
        return ret;
    }

    /**
     * This method reads one byte from the i2c device. Result is between -128 and 127.
     *
     * @param address local address in the i2c device
     * @return the value read from the device
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    @Override
    public int read(int address) throws IOException {
        int ret = I2C.i2cReadByte(bus.fd, deviceAddress, address);
        if (ret < 0) {
            throw new IOException("Error reading from " + makeDescription(address) + ". Got " + ret + ".");
        }
        return ret;
    }

    /**
     * <p>This method reads bytes from the i2c device to given buffer at asked offset. </p>
     * <p>
     * <p>Note: Current implementation calls {@link #read(int)}. That means for each read byte
     * i2c bus will send (next) address to i2c device.
     * </p>
     *
     * @param address local address in the i2c device
     * @param buffer  buffer of data to be read from the i2c device in one go
     * @param offset  offset in buffer
     * @param size    number of bytes to be read
     * @return number of bytes read
     * @throws IOException thrown in case byte cannot be read from the i2c device or i2c bus
     */
    @Override
    public int read(int address, byte[] buffer, int offset, int size) throws IOException {
        // It doesn't work for some reason.
        int ret = I2C.i2cReadBytes(bus.fd, deviceAddress, address, size, offset, buffer);
        if (ret < 0) {
            throw new IOException("Error reading from " + makeDescription(address) + ". Got " + ret + ".");
        }
        return ret;
    }

    /**
     * This helper method creates a string describing bus file name and device address (in hex).
     *
     * @return string with all details
     */
    protected String makeDescription() {
        return "I2C Bus " + bus.fd + " at address 0x" + Integer.toHexString(deviceAddress);
    }

    /**
     * This helper method creates a string describing bus file name, device address (in hex)
     * and local i2c address.
     *
     * @param address local address in i2c device
     * @return string with all details
     */
    protected String makeDescription(int address) {
        return "I2C Bus " + bus.fd + " at address 0x" + Integer.toHexString(deviceAddress)
                + " to address 0x" + Integer.toHexString(address);
    }

}

