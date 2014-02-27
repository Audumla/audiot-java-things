package net.audumla.perio.impl;

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

import net.audumla.perio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultPeripheralChannelMessage implements PeripheralChannelMessage {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPeripheralChannelMessage.class);

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    protected Collection<ByteBuffer> writeBuffers;
    protected Collection<ByteBuffer> readBuffers;
    protected Queue<MesssageChannelTrait> contextStack = new LinkedList<>();
    protected boolean template = false;
    protected boolean haltOnError = true;

    public DefaultPeripheralChannelMessage() {
        writeBuffers = new ArrayList<>();
        readBuffers = new ArrayList<>();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        Collection<MessageChannelResult> results = transfer(null, dst);
        final AtomicInteger size = new AtomicInteger();
        results.stream().filter(r -> r.getResultType().equals(MessageChannelResult.ResultType.READ)).forEach(r -> size.set(size.get() + r.getTransferSize()));
        return size.get();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        Collection<MessageChannelResult> results = transfer(src, null);
        final AtomicInteger size = new AtomicInteger();
        results.stream().filter(r -> r.getResultType().equals(MessageChannelResult.ResultType.WRITE)).forEach(r -> size.set(size.get() + r.getTransferSize()));
        return size.get();
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {
        writeBuffers = null;
        readBuffers = null;
    }

    @Override
    public PeripheralChannelMessage appendRead(ReadablePeripheralChannel channel, ByteBuffer byteBuffer) {
        appendBuffer(readBuffers, byteBuffer);
        return appendSizedRead(channel, byteBuffer.remaining());
    }

    @Override
    public PeripheralChannelMessage appendWrite(WritablePeripheralChannel channel, ByteBuffer byteBuffer) {
        appendBuffer(writeBuffers, byteBuffer);
        return appendSizedWrite(channel, byteBuffer.limit());
    }

    @Override
    public PeripheralChannelMessage appendSizedWrite(WritablePeripheralChannel channel, int size) {
        contextStack.add((txBuffer, rxBuffer) -> {
            try {
                int pos = txBuffer.position();
                return new MessageChannelResult(MessageChannelResult.ResultType.WRITE,channel.write(txBuffer, pos, size),txBuffer,pos);
            } catch (IOException ex) {
                return new MessageChannelResult(ex);
            }
        });
        return this;
    }

    @Override
    public PeripheralChannelMessage appendSizedRead(ReadablePeripheralChannel channel, int size) {
        contextStack.add((txBuffer, rxBuffer) -> {
            try {
                int pos = rxBuffer.position();
                return new MessageChannelResult(MessageChannelResult.ResultType.READ, channel.read(rxBuffer, pos, size), rxBuffer, pos);
            } catch (IOException ex) {
                return new MessageChannelResult(ex);
            }
        });
        return this;
    }

    @Override
    public PeripheralChannelMessage appendWrite(WritablePeripheralChannel channel, byte... value) {
        return appendWrite(channel, ByteBuffer.wrap(value));
    }

    @Override
    public PeripheralChannelMessage appendWait(Duration duration) {
        long millis = (duration.getSeconds() * 1000) + (duration.getNano() / 1000000);
        int nanos = duration.getNano() % 1000000;
        contextStack.add((txBuffer, rxBuffer) -> {
                    synchronized (Thread.currentThread()) {
                        try {
                            Thread.sleep(millis, nanos);
                        } catch (InterruptedException e) {
                            return new MessageChannelResult(e);
                        }
                    }
                    return new MessageChannelResult(MessageChannelResult.ResultType.NO_RESULT);
                }
        );
        return this;
    }

    @Override
    public PeripheralChannelMessage appendTrait(MesssageChannelTrait trait) {
        contextStack.add(trait);
        return this;
    }

    @Override
    public Collection<MessageChannelResult> transfer() {
        return transfer(null, null);
    }

    @Override
    public void haltOnError(boolean halt) {
        haltOnError = halt;
    }

    @Override
    public boolean willHaltOnError() {
        return haltOnError;
    }

    @Override
    public Collection<MessageChannelResult> transfer(ByteBuffer txBuffer, ByteBuffer rxBuffer) {
        Collection<MessageChannelResult> results = new ArrayList<>();
        Iterator<ByteBuffer> writeIt = null;
        Iterator<ByteBuffer> readIt = null;
        rxBuffer = rxBuffer != null ? rxBuffer : (readIt = readBuffers.iterator()).hasNext() ? (ByteBuffer) readIt.next().rewind() : ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        txBuffer = txBuffer != null ? txBuffer : (writeIt = writeBuffers.iterator()).hasNext() ? (ByteBuffer) writeIt.next().rewind() : ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        for (MesssageChannelTrait c : contextStack) {
            try {
                MessageChannelResult result = c.apply(txBuffer, rxBuffer);
                switch (result.getResultType()) {
                    case READ:
                        rxBuffer = (readIt != null && readIt.hasNext()) ? (ByteBuffer) readIt.next().rewind() : rxBuffer;
                        results.add(result);
                        break;
                    case WRITE:
                        txBuffer = (writeIt != null && writeIt.hasNext()) ? (ByteBuffer) writeIt.next().rewind() : txBuffer;
                        results.add(result);
                        break;
                    case ERROR:
                        results.add(result);
                        logger.error("Failed to execute message", result.getException());
                        if (haltOnError) {
                            return results;
                        }
                }
            } catch (Exception e) {
                results.add(new MessageChannelResult(e));
                logger.error("Failed to execute message", e);
                if (haltOnError) {
                    return results;
                }
            }
        }
        return results;
    }

//    protected ByteBuffer appendBuffer(ByteBuffer dest, ByteBuffer src, int offset, int size) {
//        if (!template) {
//            dest = getSizedBuffer(dest, size);
//            if (src.hasArray()) {
//                dest.put(src.array(), offset, size);
//            } else {
//                for (int i = offset; i < offset + size; ++i) {
//                    dest.put(src.get(i));
//                }
//            }
//        }
//        return dest;
//    }

    protected ByteBuffer getSizedBuffer(ByteBuffer src, int size) {
        if (src.remaining() < size) {
            src = ByteBuffer.allocate(src.capacity() + DEFAULT_BUFFER_SIZE).put(src);
        }
        return src;
    }

    protected void appendBuffer(Collection<ByteBuffer> bufferStack, ByteBuffer byteBuffer) {
        if (!template) {
            bufferStack.add(byteBuffer);
        }
    }


}
