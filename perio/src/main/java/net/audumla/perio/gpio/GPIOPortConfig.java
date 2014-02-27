package net.audumla.perio.gpio;

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

public final class GPIOPortConfig implements PeripheralConfig<net.audumla.perio.gpio.GPIOPort> {
    public static final int DIR_BOTH_INIT_INPUT = 2;
    public static final int DIR_BOTH_INIT_OUTPUT = 3;
    public static final int DIR_INPUT_ONLY = 0;
    public static final int DIR_OUTPUT_ONLY = 1;
    private int direction;
    private int initValue;
    private net.audumla.perio.gpio.GPIOPinConfig[] pinConfigs;
    private net.audumla.perio.gpio.GPIOPin[] pins;

    public GPIOPortConfig(int direction, int initValue, net.audumla.perio.gpio.GPIOPinConfig[] pinConfigs) {
        this.direction = direction;
        this.initValue = initValue;
        this.pinConfigs = pinConfigs;
    }

    public GPIOPortConfig(int direction, int initValue, net.audumla.perio.gpio.GPIOPin[] pins) {
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

    public net.audumla.perio.gpio.GPIOPinConfig[] getPinConfigs() {
        return pinConfigs;
    }

    public net.audumla.perio.gpio.GPIOPin[] getPins() {
        return pins;
    }

}