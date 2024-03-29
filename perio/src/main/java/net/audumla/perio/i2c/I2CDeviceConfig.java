package net.audumla.perio.i2c;

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

import net.audumla.perio.PeripheralConfig;

public class I2CDeviceConfig implements PeripheralConfig.HardwareAddressing, PeripheralConfig<net.audumla.perio.i2c.I2CDevice> {
    public static final int ADDR_SIZE_10 = 10;
    public static final int ADDR_SIZE_7 = 7;

    private int address;
    private int clockFrequency = DEFAULT;
    private int deviceNumber = DEFAULT;
    private String deviceName;
    private int addressSize = ADDR_SIZE_7;
    private int width = 1;

    public I2CDeviceConfig(java.lang.String deviceName, int address) {
        this.address = address;
        this.deviceName = deviceName;
    }

    public I2CDeviceConfig(int deviceNumber, int address) {
        this.deviceNumber = deviceNumber;
        this.address = address;
    }

    public I2CDeviceConfig(java.lang.String deviceName, int address, int addressSize, int clockFrequency, int width) {
        this.address = address;
        this.addressSize = addressSize;
        this.clockFrequency = clockFrequency;
        this.deviceName = deviceName;
        this.width = width;
    }

    public I2CDeviceConfig(int deviceNumber, int address, int addressSize, int clockFrequency, int width) {
        this.address = address;
        this.clockFrequency = clockFrequency;
        this.deviceNumber = deviceNumber;
        this.addressSize = addressSize;
        this.width = width;
    }

    public I2CDeviceConfig(String deviceName, int deviceNumber, int address, int addressSize, int clockFrequency, int width) {
        this.deviceName = deviceName;
        this.deviceNumber = deviceNumber;
        this.address = address;
        this.addressSize = addressSize;
        this.clockFrequency = clockFrequency;
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int getAddress() {
        return address;
    }


    public int getClockFrequency() {
        return clockFrequency;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getAddressSize() {
        return addressSize;
    }


}