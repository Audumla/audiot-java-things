package com.oracle.deviceaccess;

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

import java.time.Duration;

public interface PeripheralMessage<P extends PeripheralChannel<? super P,? super C>,C extends PeripheralConfig<? super P>, M extends PeripheralMessage> {

    M appendRead(P peripheral, java.nio.ByteBuffer byteBuffer) throws ClosedPeripheralException;

    M appendWrite(P peripheral, java.nio.ByteBuffer byteBuffer) throws java.io.IOException, ClosedPeripheralException;

    M appendWrite(P peripheral, int value) throws java.io.IOException, ClosedPeripheralException;

    M appendSleep(Duration duration);

    int[] transfer() throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;
}