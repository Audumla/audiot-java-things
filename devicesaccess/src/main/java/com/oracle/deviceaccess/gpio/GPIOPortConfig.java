package com.oracle.deviceaccess.gpio;

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

public final class GPIOPortConfig implements PeripheralConfig<GPIOPort> {
    public static final int DIR_BOTH_INIT_INPUT = 2;
    public static final int DIR_BOTH_INIT_OUTPUT = 3;
    public static final int DIR_INPUT_ONLY = 0;
    public static final int DIR_OUTPUT_ONLY = 1;
    private int direction;
    private int initValue;
    private com.oracle.deviceaccess.gpio.GPIOPinConfig[] pinConfigs;
    private com.oracle.deviceaccess.gpio.GPIOPin[] pins;

    public GPIOPortConfig(int direction, int initValue, GPIOPinConfig[] pinConfigs) {
        this.direction = direction;
        this.initValue = initValue;
        this.pinConfigs = pinConfigs;
    }

    public GPIOPortConfig(int direction, int initValue, GPIOPin[] pins) {
        this.direction = direction;
        this.initValue = initValue;
        this.pins = pins;
    }

    public int getDirection() {
        return direction;
    }

    public int getInitValue() {
        return initValue;
    }

    public GPIOPinConfig[] getPinConfigs() {
        return pinConfigs;
    }

    public GPIOPin[] getPins() {
        return pins;
    }

}