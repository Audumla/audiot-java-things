package net.audumla.deviceaccess;

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
import java.nio.channels.ByteChannel;
import java.time.Duration;

public interface PeripheralMessage<P extends PeripheralChannel<? super P, ? super C, ? super M>, C extends PeripheralConfig<? super P>, M extends PeripheralMessage<? super P, ? super C, ? super M>> extends ByteChannel {

    Integer[] transfer(ByteBuffer txBuffer, ByteBuffer rxBuffer) throws IOException, UnavailablePeripheralException, ClosedPeripheralException;

    public static interface MessageContext {
        public static int NO_TRANSFER = Integer.MAX_VALUE;

        public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer) throws IOException;
    }

    M appendRead(java.nio.ByteBuffer byteBuffer) throws ClosedPeripheralException;

    M appendRead(java.nio.ByteBuffer byteBuffer, int mask) throws ClosedPeripheralException;

    M appendWrite(java.nio.ByteBuffer byteBuffer) throws java.io.IOException, ClosedPeripheralException;

    M appendWrite(java.nio.ByteBuffer byteBuffer, int mask) throws java.io.IOException, ClosedPeripheralException;

    M appendWrite(int ... value) throws java.io.IOException, ClosedPeripheralException;

    M appendSizedWrite(int size);

    M appendSizedWrite(int size, int mask);

    M appendSizedRead(int size);

    M appendSizedRead(int size, int mask);

    M appendPeripheral(P peripheral);

    M appendMask(int mask);

    M appendWait(Duration duration);

    Integer[] transfer() throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;
}