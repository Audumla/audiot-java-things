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

import com.oracle.deviceaccess.PeripheralChannel;
import com.oracle.deviceaccess.PeripheralMessage;

public interface GPIOPort extends PeripheralChannel<GPIOPort, GPIOPortConfig> {

    int INPUT = 0;
    int OUTPUT = 1;

    int getDirection() throws java.io.IOException, com.oracle.deviceaccess.UnavailablePeripheralException, com.oracle.deviceaccess.ClosedPeripheralException;

    int getMaxValue() throws java.io.IOException, com.oracle.deviceaccess.UnavailablePeripheralException, com.oracle.deviceaccess.ClosedPeripheralException;

    int getValue() throws java.io.IOException, com.oracle.deviceaccess.UnavailablePeripheralException, com.oracle.deviceaccess.ClosedPeripheralException;

    void setDirection(int i) throws java.io.IOException, com.oracle.deviceaccess.UnavailablePeripheralException, com.oracle.deviceaccess.ClosedPeripheralException;

    void setInputListener(com.oracle.deviceaccess.gpio.PortListener portListener) throws java.io.IOException, com.oracle.deviceaccess.ClosedPeripheralException;

    void setValue(int i) throws java.io.IOException, com.oracle.deviceaccess.UnavailablePeripheralException, com.oracle.deviceaccess.ClosedPeripheralException;
}