package net.audumla.deviceaccess.impl;

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

import net.audumla.deviceaccess.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;

public class DefaultPeripheralChannelMessage<P extends PeripheralChannel<? super P, ? super C>, C extends PeripheralConfig<? super P>, M extends PeripheralChannelMessage<? super P, ? super C, ? super M>> implements PeripheralChannelMessage<P, C, M> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPeripheralChannelMessage.class);

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    protected P peripheral;
    protected ByteBuffer defaultTxBuffer;
    protected Collection<ByteBuffer> defaultRxBufferStack;
    protected Queue<MessageContextModifier> contextStack = new LinkedList<>();
    protected boolean template = false;

    public DefaultPeripheralChannelMessage(P peripheral, boolean template) {
        this.peripheral = peripheral;
        this.template = template;
        defaultRxBufferStack = new ArrayList<>();
        if (!template) {
            defaultTxBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        }
    }

    public DefaultPeripheralChannelMessage(boolean template) {
        this.template = template;
        defaultRxBufferStack = new ArrayList<>();
        if (!template) {
            defaultTxBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        }
    }

    @Override
    public M appendRead(ByteBuffer byteBuffer) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add(new MessageContextModifier<P>() {
            @Override
            public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                return peripheral.read(rxBuffer, rxBuffer.position(), byteBuffer.remaining());
            }
        });
        return (M) this;
    }

    @Override
    public M appendWrite(ByteBuffer byteBuffer) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer, 0, byteBuffer.limit());
            contextStack.add(new MessageContextModifier<P>() {
                @Override
                public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                    return peripheral.write(txBuffer, txBuffer.position(), byteBuffer.limit());
                }
            });
        }
        return (M) this;
    }


    @Override
    public M appendWrite(byte... value) throws IOException, ClosedPeripheralException {
        if (value.length > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, value);
            if (value.length == 1) {
                contextStack.add(new MessageContextModifier<P>() {
                    @Override
                    public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                        return peripheral.write(txBuffer.get());
                    }
                });

            } else {
                contextStack.add(new MessageContextModifier<P>() {
                    @Override
                    public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                        return peripheral.write(txBuffer, txBuffer.position(), value.length);

                    }
                });
            }
        }
        return (M) this;
    }

    @Override
    public M appendSizedWrite(int size) {
        if (size > 0) {
            contextStack.add(new MessageContextModifier<P>() {
                @Override
                public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                    return peripheral.write(txBuffer, txBuffer.position(), size);
                }
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedRead(int size) {
        contextStack.add(new MessageContextModifier<P>() {
            @Override
            public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                return peripheral.read(rxBuffer, rxBuffer.position(), size);
            }
        });
        return (M) this;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        Integer[] r = transfer(null, dst);
        int size = 0;
        for (Integer aR : r) {
            size += aR;
        }
        return size;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        int pos = src.position();
        transfer(src, null);
        return src.position() - pos;
    }

    @Override
    public boolean isOpen() {
        return peripheral.isOpen();
    }

    @Override
    public void close() throws IOException {
        defaultRxBufferStack = null;
        defaultTxBuffer = null;
    }

    @Override
    public M setPeripheral(P newPeripheral) {
        contextStack.add(new MessageContextModifier<P>() {
            @Override
            public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                DefaultPeripheralChannelMessage.this.peripheral = newPeripheral;
                return MessageContextModifier.NO_TRANSFER;
            }
        });
        return (M) this;
    }

    @Override
    public M wait(Duration duration) {
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep(duration.getSeconds() * 1000, duration.getNano());
            } catch (InterruptedException e) {
                logger.error("Unable to apply wait", e);
            }
        }
        return (M) this;
    }

    @Override
    public Integer[] transfer() throws IOException, UnavailablePeripheralException, ClosedPeripheralException {
        if (!template) {
            // this will default to using the default tx and rx byte buffers
            return transfer(null, null);
        } else {
            throw new PeripheralChannelMessageException();
        }
    }

    @Override
    public Integer[] transfer(ByteBuffer txBuffer, ByteBuffer rxBuffer) throws IOException, UnavailablePeripheralException, ClosedPeripheralException {
        P tperipheral = peripheral;
        Collection<Integer> readCount = new ArrayList<>();
        try {
            Iterator<ByteBuffer> it = null;
            rxBuffer = rxBuffer != null ? rxBuffer : (it = defaultRxBufferStack.iterator()).hasNext() ? (ByteBuffer) it.next().rewind() : ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            txBuffer = txBuffer != null ? txBuffer : defaultTxBuffer != null ? (ByteBuffer) defaultTxBuffer.rewind() : ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            for (MessageContextModifier c : contextStack) {
                int pos = rxBuffer.position();
                int bytes = c.apply(txBuffer, rxBuffer, peripheral);
                if (rxBuffer != null && MessageContextModifier.NO_TRANSFER != bytes) {
                    int posDiff = rxBuffer.position() - pos;
                    if (posDiff > 0) {
                        readCount.add(posDiff);
                        if (it != null && it.hasNext()) {
                            rxBuffer = it.next();
                            rxBuffer.rewind();
                        }
                    }
                }
            }
        } finally {
            peripheral = tperipheral;
        }
        return readCount.toArray(new Integer[readCount.size()]);
    }

    protected ByteBuffer appendBuffer(ByteBuffer dest, ByteBuffer src, int offset, int size) {
        if (!template) {
            dest = getSizedBuffer(dest, size);
            if (src.hasArray()) {
                dest.put(src.array(), offset, size);
            } else {
                for (int i = offset; i < offset + size; ++i) {
                    dest.put(src.get(i));
                }
            }
        }
        return dest;
    }

    protected ByteBuffer appendBuffer(ByteBuffer dest, byte[] value) {
        if (!template) {
            dest = getSizedBuffer(dest, value.length);
            dest.put(value);
        }
        return dest;
    }

    protected ByteBuffer getSizedBuffer(ByteBuffer src, int size) {
        if (src.remaining() < size) {
            src = ByteBuffer.allocate(defaultTxBuffer.capacity() + DEFAULT_BUFFER_SIZE).put(src);
        }
        return src;
    }

    protected void appendBuffer(Collection<ByteBuffer> bufferStack, ByteBuffer byteBuffer) {
        if (!template) {
            bufferStack.add(byteBuffer);
        }
    }


}
