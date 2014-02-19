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

public class DefaultAddressablePeripheralChannelMessage<P extends AddressablePeripheralChannel<? super P, ? super C>, C extends PeripheralConfig<? super P>, M extends AddressablePeripheralChannelMessage<? super P, ? super C, ? super M>> extends DefaultPeripheralChannelMessage<P, C, M> implements AddressablePeripheralChannelMessage<P, C, M> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAddressablePeripheralChannelMessage.class);

    public DefaultAddressablePeripheralChannelMessage(boolean tempalte) {
        super(tempalte);
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
                    return peripheral.write(txBuffer, txBuffer.position(), byteBuffer.remaining());
                }
            });
        }
        return (M) this;
    }

    @Override
    public M appendWrite(byte... value) throws IOException, ClosedPeripheralException {
        if (value.length > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, value);
            contextStack.add(new MessageContextModifier<P>() {
                @Override
                public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                    return peripheral.write(txBuffer, txBuffer.position(), value.length);

                }
            });
        }
        return (M) this;
    }

    @Override
    public M appendRead(int address, ByteBuffer byteBuffer) throws ClosedPeripheralException {
        appendBuffer(defaultRxBufferStack, byteBuffer);
        contextStack.add(new MessageContextModifier<P>() {
            @Override
            public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                return peripheral.read(address, rxBuffer, rxBuffer.position(), byteBuffer.remaining());
            }
        });
        return (M) this;
    }

    @Override
    public M appendWrite(int address, ByteBuffer byteBuffer) throws IOException, ClosedPeripheralException {
        if (byteBuffer.limit() > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, byteBuffer, 0, byteBuffer.limit());
            contextStack.add(new MessageContextModifier<P>() {
                @Override
                public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                    return peripheral.write(address, txBuffer, txBuffer.position(), byteBuffer.remaining());
                }
            });
        }
        return (M) this;
    }

    @Override
    public M appendWrite(int address, byte... value) throws IOException, ClosedPeripheralException {
        if (value.length > 0) {
            defaultTxBuffer = appendBuffer(defaultTxBuffer, value);
            if (value.length == 1) {
                contextStack.add(new MessageContextModifier<P>() {
                    @Override
                    public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                        return peripheral.write(address, txBuffer.get());
                    }
                });

            } else {
                contextStack.add(new MessageContextModifier<P>() {
                    @Override
                    public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                        return peripheral.write(address, txBuffer, txBuffer.position(), value.length);
                    }
                });
            }
        }
        return (M) this;
    }

    @Override
    public M appendSizedWrite(int address, int size) {
        if (size > 0) {
            contextStack.add(new MessageContextModifier<P>() {
                @Override
                public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                    return peripheral.write(address, txBuffer, txBuffer.position(), size);
                }
            });
        }
        return (M) this;
    }

    @Override
    public M appendSizedRead(int address, int size) {
        contextStack.add(new MessageContextModifier<P>() {
            @Override
            public int apply(ByteBuffer txBuffer, ByteBuffer rxBuffer, P peripheral) throws IOException {
                return peripheral.read(address, rxBuffer, rxBuffer.position(), size);
            }
        });
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

}