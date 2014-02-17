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

import java.io.IOException;
import java.nio.ByteBuffer;

public interface AddressablePeripheralMessage<P extends AddressablePeripheralChannel<? super P,? super C>,C extends PeripheralConfig<? super P>, M extends AddressablePeripheralMessage<? super P, ? super C, ? super M>> extends PeripheralMessage<P,C,M> {

    M appendRead(P peripheral, int subAddress, int subAddressSize, ByteBuffer dst) throws IOException;

    M appendWrite(P peripheral, int subAddress, int subAddressSize, ByteBuffer dst) throws IOException;

    M appendWrite(P peripheral, int subAddress, int subAddressSize, int value) throws IOException;

    M appendRead(P peripheral, int subAddress, ByteBuffer dst) throws IOException;

    M appendWrite(P peripheral, int subAddress, ByteBuffer dst) throws IOException;

    M appendWrite(P peripheral, int subAddress, int value) throws IOException;
}