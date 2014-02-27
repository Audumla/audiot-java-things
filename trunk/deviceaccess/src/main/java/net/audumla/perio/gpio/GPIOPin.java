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

import net.audumla.perio.Peripheral;

public interface GPIOPin extends Peripheral<GPIOPin, GPIOPinConfig> {
    int INPUT = 0;
    int OUTPUT = 1;
    int TRIGGER_BOTH_EDGES = 3;
    int TRIGGER_BOTH_LEVELS = 6;
    int TRIGGER_FALLING_EDGE = 1;
    int TRIGGER_HIGH_LEVEL = 4;
    int TRIGGER_LOW_LEVEL = 5;
    int TRIGGER_NONE = 0;
    int TRIGGER_RISING_EDGE = 2;

    int getDirection() throws java.io.IOException;

    int getTrigger() throws java.io.IOException;

    boolean getValue() throws java.io.IOException;

    void setDirection(int i) throws java.io.IOException;

    void setTrigger(int i) throws java.io.IOException;

    void setInputListener(PinListener pinListener) throws java.io.IOException;

    void setValue(boolean b) throws java.io.IOException;
}