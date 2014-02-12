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

public class AnalogState implements GPIOState<AnalogState.AnalogStateDevice> {
    private static final Logger logger = LoggerFactory.getLogger(AnalogState.class);

    private Float state;
    private int pin;

    public AnalogState(Float state, int pin) {
        this.state = state;
        this.pin = pin;
    }

    @Override
    public void applyState(AnalogStateDevice device) {
        device.setState(pin,state);
    }

    @Override
    public void retrieveState(AnalogStateDevice device) {
        state = device.getState(pin);
    }

    public interface AnalogStateDevice {
        Float getState(int pin);
        void setState(int pin, Float state);
    }

    public Float getState() {
        return state;
    }

    public void setState(Float state) {
        this.state = state;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
