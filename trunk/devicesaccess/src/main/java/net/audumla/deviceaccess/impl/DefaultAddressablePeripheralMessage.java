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

public class DefaultAddressablePeripheralMessage<P extends AddressablePeripheralChannel<? super P, ? super C, ? super M>, C extends PeripheralConfig<? super P>, M extends AddressablePeripheralMessage<? super P, ? super C, ? super M>> extends DefaultPeripheralMessage<P, C, M> implements AddressablePeripheralMessage<P, C, M> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAddressablePeripheralMessage.class);

    protected Integer writeAddress;
    protected Integer readAddress;

    public DefaultAddressablePeripheralMessage(boolean tempalte) {
        super(tempalte);
    }

    @Override
    public M appendRead(ByteBuffer byteBuffer) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add((txBuffer, rxBuffer) -> {
            return readAddress != null ? (mask == null ? peripheral.read(readAddress, rxBuffer, rxBuffer.position(), byteBuffer.remaining()) : peripheral.read(readAddress, rxBuffer, rxBuffer.position(), byteBuffer.remaining(), mask)) : (mask == null ? peripheral.read(rxBuffer, rxBuffer.position(), byteBuffer.remaining()) : peripheral.read(rxBuffer, rxBuffer.position(), byteBuffer.remaining(), mask));
        });
        return (M) this;
    }

    @Override
    public M appendRead(ByteBuffer byteBuffer, int cmask) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add((txBuffer, rxBuffer) -> {
            return readAddress != null ? peripheral.read(readAddress, rxBuffer, rxBuffer.position(), byteBuffer.remaining(), cmask) : peripheral.read(rxBuffer, rxBuffer.position(), byteBuffer.remaining(), cmask);
        });
        return (M) this;
    }

    @Override
    public M appendWrite(ByteBuffer byteBuffer) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer.limit());
            contextStack.add((txBuffer, rxBuffer) -> {
                return writeAddress != null ? (mask == null ? peripheral.write(writeAddress, txBuffer, txBuffer.position(), byteBuffer.remaining()) : peripheral.write(writeAddress, txBuffer, txBuffer.position(), byteBuffer.remaining(), mask)) : (mask == null ? peripheral.write(txBuffer, txBuffer.position(), byteBuffer.remaining()) : peripheral.write(txBuffer, txBuffer.position(), byteBuffer.remaining(), mask));
            });
        }
        return (M) this;
    }

    @Override
    public M appendWrite(ByteBuffer byteBuffer, int cmask) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer.limit());
            contextStack.add((txBuffer, rxBuffer) -> {
                return writeAddress != null ? peripheral.write(writeAddress, txBuffer, txBuffer.position(), byteBuffer.remaining(), cmask) : peripheral.write(txBuffer, txBuffer.position(), byteBuffer.remaining(), cmask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendWrite(int... value) throws IOException, ClosedPeripheralException {
        if (value.length > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, value);
            contextStack.add((txBuffer, rxBuffer) -> {
                return writeAddress != null ? (mask == null ? peripheral.write(writeAddress, txBuffer, txBuffer.position(), value.length) : peripheral.write(writeAddress, txBuffer, txBuffer.position(), value.length, mask)) : (mask == null ? peripheral.write(txBuffer, txBuffer.position(), value.length) : peripheral.write(txBuffer, txBuffer.position(), value.length, mask));
            });
        }
        return (M) this;
    }

    @Override
    public M appendRead(int address, ByteBuffer byteBuffer) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add((txBuffer, rxBuffer) -> {
            return mask == null ? peripheral.read(address, rxBuffer, rxBuffer.position(), byteBuffer.remaining()) : peripheral.read(address, rxBuffer, rxBuffer.position(), byteBuffer.remaining(), mask);
        });
        return (M) this;
    }

    @Override
    public M appendRead(int address, ByteBuffer byteBuffer, int mask) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add((txBuffer, rxBuffer) -> {
            return peripheral.read(address, rxBuffer, rxBuffer.position(), byteBuffer.remaining(), mask);
        });
        return (M) this;
    }

    @Override
    public M appendWrite(int address, ByteBuffer byteBuffer) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer.limit());
            contextStack.add((txBuffer, rxBuffer) -> {
                return mask == null ? peripheral.write(address, txBuffer, txBuffer.position(), byteBuffer.remaining()) : peripheral.write(address, txBuffer, txBuffer.position(), byteBuffer.remaining(), mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendWrite(int address, ByteBuffer byteBuffer, int mask) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer.limit());
            contextStack.add((txBuffer, rxBuffer) -> {
                return peripheral.write(address, txBuffer, txBuffer.position(), byteBuffer.remaining(), mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendWrite(int address, int... value) throws IOException, ClosedPeripheralException {
        if (value.length > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, value);
            contextStack.add((txBuffer, rxBuffer) -> {
                return mask == null ? peripheral.write(address, txBuffer, txBuffer.position(), value.length) : peripheral.write(address, txBuffer, txBuffer.position(), value.length, mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedAddressWrite(int address, int size) {
        if (size > 0) {
            contextStack.add((txBuffer, rxBuffer) -> {
                return mask == null ? peripheral.write(address,txBuffer, txBuffer.position(), size) : peripheral.write(address,txBuffer, txBuffer.position(), size, mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedAddressWrite(int address, int size, int mask) {
        if (size > 0) {
            contextStack.add((txBuffer, rxBuffer) -> {
                return peripheral.write(address,txBuffer, txBuffer.position(), size, mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedAddressRead(int address, int size) {
        contextStack.add((txBuffer, rxBuffer) -> {
            return mask == null ? peripheral.read(address, rxBuffer, rxBuffer.position(), size) : peripheral.read(address, rxBuffer, rxBuffer.position(), size, mask);
        });
        return (M) this;
    }

    @Override
    public M appendSizedAddressRead(int address, int size, int mask) {
        contextStack.add((txBuffer, rxBuffer) -> {
            return peripheral.read(address, rxBuffer, rxBuffer.position(), size, mask);
        });
        return (M) this;
    }

    @Override
    public M appendSizedWrite(int size) {
        if (size > 0) {
            contextStack.add((txBuffer, rxBuffer) -> {
                return writeAddress != null ? (mask == null ? peripheral.write(writeAddress,txBuffer, txBuffer.position(), size) : peripheral.write(writeAddress,txBuffer, txBuffer.position(), size, mask)) : (mask == null ? peripheral.write(txBuffer, txBuffer.position(), size) : peripheral.write(txBuffer, txBuffer.position(), size, mask));
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedWrite(int size, int mask) {
        if (size > 0) {
            contextStack.add((txBuffer, rxBuffer) -> {
                return writeAddress != null ? peripheral.write(writeAddress,txBuffer, txBuffer.position(), size, mask) : peripheral.write(txBuffer, txBuffer.position(), size, mask);
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedRead(int size) {
        contextStack.add((txBuffer, rxBuffer) -> {
            return readAddress != null ? (mask == null ? peripheral.read(readAddress, rxBuffer, rxBuffer.position(), size) : peripheral.read(readAddress, rxBuffer, rxBuffer.position(), size, mask)) : (mask == null ? peripheral.read(rxBuffer, rxBuffer.position(), size) : peripheral.read(rxBuffer, rxBuffer.position(), size, mask));
        });
        return (M) this;
    }

    @Override
    public M appendSizedRead(int size, int mask) {
        contextStack.add((txBuffer, rxBuffer) -> {
            return readAddress != null ? peripheral.read(readAddress, rxBuffer, rxBuffer.position(), size, mask) : peripheral.read(rxBuffer, rxBuffer.position(), size, mask);
        });
        return (M) this;
    }


    @Override
    public M appendWriteAddress(int address) {
        contextStack.add((txBuffer, rxBuffer) -> {
            this.writeAddress = address;
            return MessageContext.NO_TRANSFER;
        });
        return (M) this;
    }

    @Override
    public M appendReadAddress(int address) {
        contextStack.add((txBuffer, rxBuffer) -> {
            this.readAddress = address;
            return MessageContext.NO_TRANSFER;
        });
        return (M) this;
    }

    @Override
    public Integer[] transfer(ByteBuffer txBuffer, ByteBuffer rxBuffer) throws IOException, UnavailablePeripheralException, ClosedPeripheralException {
        int tReadAddress = readAddress;
        int tWriteAddress = writeAddress;
        try {
            return super.transfer(txBuffer, rxBuffer);
        } finally {
            readAddress = tReadAddress;
            writeAddress = tWriteAddress;
        }
    }
}