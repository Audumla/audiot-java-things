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

public class DefaultPeripheralMessage<P extends PeripheralChannel<? super P, ? super C, ? super M>, C extends PeripheralConfig<? super P>, M extends PeripheralMessage<? super P, ? super C, ? super M>> implements PeripheralMessage<P, C, M> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPeripheralMessage.class);

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    protected P peripheral;
    protected Integer mask;
    protected ByteBuffer defaultTxBuffer;
    protected Collection<ByteBuffer> defaultRxBufferStack;
    protected Queue<MessageContext> contextStack = new LinkedList<>();
    protected boolean template = false;

    public DefaultPeripheralMessage(boolean tempalte) {
        this.template = tempalte;
        defaultRxBufferStack = new ArrayList<>();
        if (!tempalte) {
            defaultTxBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        }
    }

    @Override
    public M appendRead(ByteBuffer byteBuffer) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add((txBuffer, rxBuffer) -> {
            return mask == null ? peripheral.read(rxBuffer, rxBuffer.position(), byteBuffer.remaining()) : peripheral.read(rxBuffer, rxBuffer.position(), byteBuffer.remaining(), mask);
        });
        return (M) this;
    }

    @Override
    public M appendRead(ByteBuffer byteBuffer, int cmask) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add((txBuffer, rxBuffer) -> {
            return peripheral.read(rxBuffer, rxBuffer.position(), byteBuffer.remaining(), cmask);
        });
        return (M) this;
    }

    @Override
    public M appendWrite(ByteBuffer byteBuffer) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer, 0, byteBuffer.limit());
            contextStack.add((txBuffer, rxBuffer) -> {
                return mask == null ? peripheral.write(txBuffer, txBuffer.position(), byteBuffer.limit()) : peripheral.write(txBuffer, rxBuffer.position(), byteBuffer.limit(), mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendWrite(ByteBuffer byteBuffer, int cmask) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer, 0, byteBuffer.limit());
            contextStack.add((txBuffer, rxBuffer) -> {
                return peripheral.write(txBuffer, txBuffer.position(), byteBuffer.limit(), cmask);
            });
        }
        return (M) this;
    }


    @Override
    public M appendWrite(int... value) throws IOException, ClosedPeripheralException {
        if (value.length > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, value);
            contextStack.add((txBuffer, rxBuffer) -> {
                return mask == null ? peripheral.write(txBuffer, txBuffer.position(), value.length) : peripheral.write(txBuffer, txBuffer.position(), value.length, mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedWrite(int size) {
        if (size > 0) {
            contextStack.add((txBuffer, rxBuffer) -> {
                return mask == null ? peripheral.write(txBuffer, txBuffer.position(), size) : peripheral.write(txBuffer, txBuffer.position(), size, mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedWrite(int size, int mask) {
        if (size > 0) {
            contextStack.add((txBuffer, rxBuffer) -> {
                return peripheral.write(txBuffer, txBuffer.position(), size, mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedRead(int size) {
        contextStack.add((txBuffer, rxBuffer) -> {
            return mask == null ? peripheral.read(rxBuffer, rxBuffer.position(), size) : peripheral.read(rxBuffer, rxBuffer.position(), size, mask);
        });
        return (M) this;
    }

    @Override
    public M appendSizedRead(int size, int mask) {
        contextStack.add((txBuffer, rxBuffer) -> {
            return peripheral.read(rxBuffer, rxBuffer.position(), size, mask);
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
        transfer(src,null);
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
    public M appendPeripheral(P newPeripheral) {
        contextStack.add((txBuffer, rxBuffer) -> {
            this.peripheral = newPeripheral;
            return MessageContext.NO_TRANSFER;
        });
        return (M) this;
    }

    @Override
    public M appendMask(int newMask) {
        contextStack.add((txBuffer, rxBuffer) -> {
            this.mask = newMask;
            return MessageContext.NO_TRANSFER;
        });
        return (M) this;
    }

    @Override
    public M appendWait(Duration duration) {
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
            return transfer(null,null);
        } else {
            throw new PeripheralException();
        }
    }

    @Override
    public Integer[] transfer(ByteBuffer txBuffer, ByteBuffer rxBuffer) throws IOException, UnavailablePeripheralException, ClosedPeripheralException {
        P tperipheral = peripheral;
        Integer tmask = mask;
        Collection<Integer> readCount = new ArrayList<>();
        try {
            Iterator<ByteBuffer> it = null;
            rxBuffer = rxBuffer != null ? rxBuffer : (it = defaultRxBufferStack.iterator()).hasNext() ? (ByteBuffer) it.next().rewind() : ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            txBuffer = txBuffer != null ? txBuffer : defaultTxBuffer != null ? (ByteBuffer) defaultTxBuffer.rewind() : ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            for (MessageContext c : contextStack) {
                int pos = rxBuffer.position();
                int bytes = c.apply(txBuffer, rxBuffer);
                if (rxBuffer != null && MessageContext.NO_TRANSFER != bytes) {
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
            mask = tmask;
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

    protected ByteBuffer appendBuffer(ByteBuffer dest, int... value) {
        if (!template) {
            dest = getSizedBuffer(dest, peripheral.getWidth().byteSize() * value.length);
            for (int i : value) {
                switch (peripheral.getWidth()) {
                    case WIDTH8:
                        dest.put((byte) i);
                        break;
                    case WIDTH16:
                        dest.putChar((char) i);
                        break;
                    case WIDTH32:
                        dest.putInt(i);
                        break;
                }
            }
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
