package net.audumla.devices.io.gpio;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.BitSet;

public class DigitalState implements GPIOState<DigitalState.DigitalStateDevice> {
    private static final Logger logger = LoggerFactory.getLogger(DigitalState.class);

    protected int pin;
    protected boolean state;

    public DigitalState(int pin, boolean state) {
        this.pin = pin;
        this.state = state;
    }

    public interface DigitalStateDevice {
        boolean getState(int pin);

        void setState(int pin, boolean state);
    }

    @Override
    public void applyState(DigitalStateDevice device) {
        device.setState(pin,state);
    }

    @Override
    public void retrieveState(DigitalStateDevice device) {
        state = device.getState(pin);

    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public boolean isHigh() {
        return state;
    }

    public boolean isLow() {
        return !state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
