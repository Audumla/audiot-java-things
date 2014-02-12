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

public class DigitalStateSet implements GPIOState<DigitalStateSet.DigitalStateSetDevice> {
    private static final Logger logger = LoggerFactory.getLogger(DigitalStateSet.class);

    protected BitSet pinStates = new BitSet(MAX_PINS_STATES);

    public interface DigitalStateSetDevice {
        void setState(BitSet pinStates);

        void getState(BitSet pinStates);
    }

    public DigitalStateSet(BitSet pinStates) {
        this.pinStates = pinStates;
    }

    public DigitalStateSet(boolean state, long pinMask) {
        setStates(state, pinMask);
    }

    public DigitalStateSet(boolean state, Integer... pins) {
        setStates(state, pins);
    }

    void setStates(boolean state, long pinMask) {
        if (state) {
            pinStates.or(BitSet.valueOf(new long[]{pinMask}));
        } else {
            pinStates.andNot(BitSet.valueOf(new long[]{pinMask}));
        }
    }

    void setStates(boolean state, Integer... pins) {
        Arrays.asList(pins).stream().forEach((t) -> {
            pinStates.set(t, state);
        });
    }

    @Override
    public void applyState(DigitalStateSetDevice device) {
        device.setState(pinStates);
    }

    @Override
    public void retrieveState(DigitalStateSetDevice device) {
        device.getState(pinStates);
    }

    public boolean isHigh(int pin) {
        return pinStates.get(pin);
    }

    public boolean isLow(int pin) {
        return !pinStates.get(pin);
    }

    public BitSet getPinStates() {
        return pinStates;
    }

    public long getMaskedPinStates() {
        return pinStates.toLongArray()[0];
    }


}
