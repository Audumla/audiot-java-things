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

import net.audumla.deviceaccess.ClosedPeripheralException;
import net.audumla.deviceaccess.Peripheral;
import net.audumla.deviceaccess.UnavailablePeripheralException;

public interface GPIOPort extends Peripheral<GPIOPort, GPIOPortConfig> {

    int INPUT = 0;
    int OUTPUT = 1;

    int getDirection() throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;

    int getMaxValue() throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;

    int getValue() throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;

    void setDirection(int i) throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;

    void setInputListener(PortListener portListener) throws java.io.IOException, ClosedPeripheralException;

    void setValue(int i) throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;
}