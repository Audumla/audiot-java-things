package net.audumla.perio;

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

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.time.Duration;
import java.util.Collection;

/**
 * A PeripheralChannelMessage can be used to read and write to multiple locations as a single transaction. By appending reads and writes to a message a complete
 * transaction can be built up from multiple parts and executed as a single transfer.
 * <p>
 * A message template can be created by using any of the append methods and then reused multiple times by passing ByteBuffers to either the transfer method or to the read/write methods
 * from the ByteChannel interface.
 */

public interface PeripheralChannelMessage extends ByteChannel {

    /**
     * Message Channel Traits are contained logic that operate on the given read/write buffers at a certain point during the meesage
     * All internal actions (read/write/wait) within the message are executed via traits. By implementing a custom trait the message
     * can be made to perform complex logic as it processes itself. For instance if a write operation needs to wait for a interrupt before
     * applying the next action then a trait could be created and appended to the message after each write statement.
     * The Message can then be passed as a standard ByteChannel allowing complex messaging logic to be applied on what appear to be standard ByteBuffer
     * reads and writes
     */
    public static interface MesssageChannelTrait {
        /**
         * Attempts to apply the logic contained within the trait to given read/write buffers. Implementations of this interface
         * need to maintain references to any channels or variables that it needs to execute its logic.
         *
         * @param txBuffer the buffer that contains the data to be transmitted
         * @param rxBuffer the buffer that will contain read data
         * @return a MessageChannelResult instance indicating the status of the attempted application of the trait
         */
        public MessageChannelResult apply(ByteBuffer txBuffer, ByteBuffer rxBuffer);
    }

    /**
     * An object containing the result of a call to a MessageChannelTraits apply method.
     */
    public static class MessageChannelResult {
        /**
         * A status indicating the type of transaction that occurred
         * READ      - A read operation was successfully executed, getValue() will return the number of bytes read
         * WRITE     - A write operation was successfully executed, getValue() will return the number of bytes written
         * NO_RESULT - An operation was executed that had no impact on the buffers. Eg. A wait operation
         * ERROR     - An error occurred during the transaction, getException will return the underlying exception
         */
        public enum ResultType {
            READ, WRITE, NO_RESULT, ERROR
        }

        protected int transferSize;
        protected ResultType type;
        protected Exception exception;
        protected ByteBuffer buffer;
        protected int transferOffset = -1;

        public MessageChannelResult(ResultType type, int transferSize, ByteBuffer buffer, int transferOffset) {
            this.type = type;
            this.transferSize = transferSize;
            this.buffer = buffer;
            this.transferOffset = transferOffset;
        }

        public MessageChannelResult(ResultType type) {
            this.type = type;
        }

        public MessageChannelResult(Exception exception) {
            this.exception = exception;
            this.type = ResultType.ERROR;
        }

        /**
         * The type of operation that was executed
         *
         * @return the successfully exceuted operation type
         */
        public ResultType getResultType() {
            return type;
        }

        /**
         * A value that can applies to the type of operation indicated by getResultType()
         *
         * @return a numberical value generally indicating bytes read or written
         */
        public int getTransferSize() {
            return transferSize;
        }

        /**
         * Populated if an exception was thrown during the application of a trait
         *
         * @return The underlying cause for an error, or null if no error occured
         */
        public Exception getException() {
            return exception;
        }

        /**
         * returns the buffer that was either written from or read to
         *
         * @return a reference to the ByteBuffer if a read or write operation was successfully completed, otherwise null
         */
        public ByteBuffer getTransferBuffer() {
            return buffer;
        }

        /**
         * returns the offset into the transfer buffer where the read or rwite began
         *
         * @return a value of 0 or greater if a read or write took place, othersize it will return a negative value if no transfer completed
         */
        public int getTransferOffset() {
            return transferOffset;
        }
    }

    /**
     * Appends a read operation to the message. The operation will store the read bytes into the supplied
     * buffer unless a rxBuffer buffer was supplied to the transfer call.
     * When transfer is called the operation will attempt to read a fixed number of bytes equal to the result
     * of byteBuffer.remaining() at the time the buffer was appended to the message. The number of bytes attempted be
     * read does not change based on the state of the buffer during the transfer
     * If the buffer does not have enough capacity to hold the bytes read an error will be returned as a result of the
     * transfer
     *
     * @param channel    the Channel to read from
     * @param byteBuffer the buffer to read into
     * @return the ChannelMessage instance
     */
    PeripheralChannelMessage appendRead(ReadablePeripheralChannel channel, java.nio.ByteBuffer byteBuffer);

    /**
     * Appends a write operation to the message. The operation will read the bytes from the supplied
     * buffer unless a txBuffer buffer was supplied to the transfer call.
     * When transfer is called the operation will attempt to write a fixed number of bytes equal to the limit
     * of the byteBuffer at the time the buffer was appended to the message. The number of bytes attempted be
     * written does not change based on the state of the buffer during the transfer
     * If the buffer does not have any bytes left an error will be returned as a result of the transfer
     *
     * @param channel    the Channel to write to
     * @param byteBuffer the buffer to write from
     * @return the ChannelMessage instance
     */
    PeripheralChannelMessage appendWrite(WritablePeripheralChannel channel, java.nio.ByteBuffer byteBuffer);

    /**
     * This is a convenience method to allow simplified write calls. The underlying implementation is the same as
     * creating a ByteBuffer and appending it as a write
     *
     * @param channel the Channel to read from
     * @param value   the bytes to be written to the supplied channel
     * @return the ChannelMessage instance
     */
    PeripheralChannelMessage appendWrite(WritablePeripheralChannel channel, byte... value);

    /**
     * Appends a fixed sized write to the message. This method is only applicable when a txBuffer is supplied to the transfer
     * call as no source buffer has been supplied.
     * This allows a message template to be created that can read or write a given number of bytes in a specific sequence but
     * allowing differnet source and target buffers to be passed in as part of the transfer call
     *
     * @param channel the Channel to write to
     * @param size    the number of bytes to write from the txBuffer supplied as part of the transfer call
     * @return the ChannelMessage instance
     */
    PeripheralChannelMessage appendSizedWrite(WritablePeripheralChannel channel, int size);

    /**
     * Appends a fixed sized read to the message. This method is only applicable when a rxBuffer is supplied to the transfer
     * call as no source buffer ahs been supplied.
     * This allows a message template to be created that can read or write a given number of bytes in a specific sequence but
     * allowing differnet source and target buffers to be passed in as part of the transfer call
     *
     * @param channel the Channel to read from
     * @param size    the number of bytes to read into the rxBuffer supplied as part of the transfer call
     * @return the ChannelMessage instance
     */
    PeripheralChannelMessage appendSizedRead(ReadablePeripheralChannel channel, int size);

    /**
     * Appends a fixed duration wait to the message. This allows the message to pause while underlying devices complete writes
     * or other logic
     *
     * @param duration the amount of time to wait before applying the next transfer
     * @return the ChannelMessage instance
     */
    PeripheralChannelMessage appendWait(Duration duration);

    /**
     * Appends a custom message trait to the message. This allows the message to perform operations at specific points allowing
     * underlying devices to complete operations or external sources queried before completing the next transfer
     *
     * @param trait the trait to be appended
     * @return the ChannelMessage instance
     */
    PeripheralChannelMessage appendTrait(MesssageChannelTrait trait);

    /**
     * Executes the message transfer using the supplied tx and rx buffers. The supplied buffers will be used instead of any buffers
     * supplied to the message when appending a read or write.
     * If either of the buffers is null then the underlying buffers supplied to the append read or write methods will be used instead.
     * If only fixed read and writes were appended to the message and a null was supplied for either the tx or rx buffers, then an internal buffer
     * will be created and can be accessed using the returned MessageChannelResult collection. In this case all writes will pass 0 as the data to
     * the channel
     *
     * @param rxBuffer The buffer to read data into or null to use the buffers supplied when initializing the message
     * @param txBuffer The buffer to write data from or null to use the buffers supplied when initializing the message
     * @return a collection of MessageChannelResult objects representing every individual message transfer. If an error occurred then
     * the last instance in the collection will contain the error information
     */
    Collection<MessageChannelResult> transfer(ByteBuffer txBuffer, ByteBuffer rxBuffer);

    /**
     * Executes the message transfer using the buffers supplied to the message when appending a read or write.
     *
     * @return a collection of MessageChannelResult objects representing every individual message transfer. If an error occurred then
     * the last instance in the collection will contain the error information
     */
    Collection<MessageChannelResult> transfer();

    /**
     * Determines whether the message will continue to process or not if an error is encountered. Default is true
     * If set to not halt errors the MessageChannelResult collection returned by the transfer methods should be queried to determine whether
     * errors occurred during the data transfer.
     *
     * @param halt true to halt the transfer if an error is encountered on a read or write. false will continue to process the other parts
     *             of the message which may generate more errors.
     */
    void haltOnError(boolean halt);

    /**
     * Return whether the channel will halt when errors are encountered during a data transmission
     *
     * @return true if the channel will halt on errors, otherwise false
     */
    boolean willHaltOnError();

}