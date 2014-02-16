package com.oracle.deviceaccess.i2cbus;

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

import com.oracle.deviceaccess.PeripheralConfig;

public class I2CDeviceConfig implements PeripheralConfig.HardwareAddressing, PeripheralConfig<com.oracle.deviceaccess.i2cbus.I2CDevice> {
    public static final int ADDR_SIZE_10 = 10;
    public static final int ADDR_SIZE_7 = 7;

    private int address;
    private int clockFrequency;
    private int deviceNumber;
    private String deviceName;
    private int addressSize;


    I2CDeviceConfig(java.lang.String deviceName, int address, int addressSize, int clockFrequency) {
        this.address = address;
        this.addressSize = addressSize;
        this.clockFrequency = clockFrequency;
        this.deviceName = deviceName;
    }

    I2CDeviceConfig(int deviceNumber, int address, int addressSize, int clockFrequency) {
        this.address = address;
        this.clockFrequency = clockFrequency;
        this.deviceNumber = deviceNumber;
        this.addressSize = addressSize;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getClockFrequency() {
        return clockFrequency;
    }

    public void setClockFrequency(int clockFrequency) {
        this.clockFrequency = clockFrequency;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getAddressSize() {
        return addressSize;
    }

    public void setAddressSize(int addressSize) {
        this.addressSize = addressSize;
    }

}