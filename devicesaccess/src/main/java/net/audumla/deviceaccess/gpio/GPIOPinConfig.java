package net.audumla.deviceaccess.gpio;

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

import net.audumla.deviceaccess.PeripheralConfig;

public final class GPIOPinConfig implements PeripheralConfig<net.audumla.deviceaccess.gpio.GPIOPin>, PeripheralConfig.HardwareAddressing {
    public static final int DIR_BOTH_INIT_INPUT = 2;
    public static final int DIR_BOTH_INIT_OUTPUT = 3;
    public static final int DIR_INPUT_ONLY = 0;
    public static final int DIR_OUTPUT_ONLY = 1;
    public static final int MODE_INPUT_PULL_DOWN = 2;
    public static final int MODE_INPUT_PULL_UP = 1;
    public static final int MODE_OUTPUT_OPEN_DRAIN = 8;
    public static final int MODE_OUTPUT_PUSH_PULL = 4;
    public static final int TRIGGER_BOTH_EDGES = 3;
    public static final int TRIGGER_BOTH_LEVELS = 6;
    public static final int TRIGGER_FALLING_EDGE = 1;
    public static final int TRIGGER_HIGH_LEVEL = 4;
    public static final int TRIGGER_LOW_LEVEL = 5;
    public static final int TRIGGER_NONE = 0;
    public static final int TRIGGER_RISING_EDGE = 2;
    private java.lang.String deviceName;
    private int direction;
    private boolean initValue;
    private int mode;
    private int pinNumber;
    private int deviceNumber;
    private int trigger;

    GPIOPinConfig(java.lang.String deviceName, int pinNumber, int direction, int mode, int trigger, boolean initValue) {
        this.deviceName = deviceName;
        this.direction = direction;
        this.pinNumber = pinNumber;
        this.mode = mode;
        this.initValue = initValue;
        this.trigger = trigger;
    }

    GPIOPinConfig(int deviceNumber, int pinNumber, int direction, int mode, int trigger, boolean initValue) {
        this.pinNumber = pinNumber;
        this.deviceNumber = deviceNumber;
        this.trigger = trigger;
        this.direction = direction;
        this.mode = mode;
        this.initValue = initValue;
    }

    public int getTrigger() {
        return trigger;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public int getMode() {
        return mode;
    }

    public boolean isInitValue() {
        return initValue;
    }

    public int getDirection() {
        return direction;
    }

    public String getDeviceName() {
        return deviceName;
    }

}