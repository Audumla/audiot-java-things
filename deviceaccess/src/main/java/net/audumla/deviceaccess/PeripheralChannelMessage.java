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
import java.util.Collection;

public interface PeripheralChannelMessage<P extends IOPeripheral<? super P, ? super C>, C extends PeripheralConfig<? super P>, M extends PeripheralChannelMessage<? super P, ? super C, ? super M>> extends ByteChannel {

    public static interface MesssageChannelTrait {
        public static int NO_IO_TRANSFER = Integer.MAX_VALUE;

        public MessageChannelResult apply(ByteBuffer txBuffer, ByteBuffer rxBuffer) throws IOException;
    }

    public static class MessageChannelResult {
        public enum ResultType {READ,WRITE,NO_RESULT,ERROR}

        protected int value;
        protected ResultType type;
        protected Exception exception;

        public MessageChannelResult(ResultType type) {
            this.type = type;
        }

        public MessageChannelResult(Exception exception) {
            this.exception = exception;
            this.type = ResultType.ERROR;
        }

        public MessageChannelResult(int value, ResultType type) {
            this.value = value;
            this.type = type;
        }

        public ResultType getType() {
            return type;
        }

        public int getValue() {
            return value;
        }

        public Exception getException() {
            return exception;
        }
    }

    M appendRead(ReadablePeripheralChannel channel, java.nio.ByteBuffer byteBuffer) throws IOException;

    M appendWrite(WritablePeripheralChannel channel, java.nio.ByteBuffer byteBuffer) throws IOException;

    M appendWrite(WritablePeripheralChannel channel, int size) throws IOException;

    M appendRead(ReadablePeripheralChannel channel, int size) throws IOException;

    M appendWrite(WritablePeripheralChannel channel, byte... value) throws java.io.IOException, ClosedPeripheralException;

    M appendWait(Duration duration);

    M appendTrait(MesssageChannelTrait trait);

    Collection<MessageChannelResult> transfer(ByteBuffer txBuffer, ByteBuffer rxBuffer) throws IOException, UnavailablePeripheralException, ClosedPeripheralException;

    Collection<MessageChannelResult> transfer() throws java.io.IOException, UnavailablePeripheralException, ClosedPeripheralException;
}