package net.audumla.devices.io;

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

public class AnalogStateSet implements DeviceState<AnalogStateDevice> {
    private static final Logger logger = LoggerFactory.getLogger(AnalogStateSet.class);

    private Float[] pinStates;

    public AnalogStateSet(Float[] pinStates) {
        this.pinStates = pinStates;
    }

    public AnalogStateSet(Float state, Integer... pins) {
        setState(state,pins);
    }

    public AnalogStateSet(Float state, long pinMask) {
        setStates(state, pinMask);
    }

    @Override
    public void applyState(AnalogStateDevice device) {
        device.setState(pinStates);
    }

    @Override
    public void retrieveState(AnalogStateDevice device) {
        device.getState(pinStates);
    }

    public void setState(Float state, Integer... pins) {
        final Float[] pinArray = getPinStateArray(pins.length);
        Arrays.asList(pins).stream().forEach((t) -> {
            pinArray[t] = state;
        });
    }

    public Float[] getStates() {
        return pinStates;
    }

    public void setStates(Float state, long pinMask) {
        final BitSet mask = BitSet.valueOf(new long[]{pinMask});
        final Float[] pinArray = getPinStateArray(64);
        mask.stream().forEach( (t) -> {pinArray[t] = state;});
    }

    public Float getState(int pin) {
        if (pinStates == null || pin > pinStates.length) {
            return null;
        }
        return pinStates[pin];
    }

    private Float[] getPinStateArray(int size) {
        if (pinStates == null) {
            pinStates = new Float[size];
        }
        else {
            if (size > pinStates.length) {
                pinStates = Arrays.copyOf(pinStates,size);
            }
        }
        return pinStates;
    }

}
