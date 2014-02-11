package net.audumla.devices.io.channel.i2c;

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

import net.audumla.devices.io.channel.*;
import net.audumla.devices.io.i2c.jni.rpi.I2C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class RPiI2CChannel extends AbstractDeviceChannel {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CChannel.class);

    static protected final Map<String, Integer> deviceHandleMap = new HashMap<>();

    protected static class ChannelContext {

//        public ChannelContext() {
//            updateWriters();
//        }

        public ChannelContext(ChannelContext cc) {
            setBusAddress(cc.getBusAddress());
            setDeviceHandle(cc.getDeviceHandle());
            setDeviceAddress(cc.getDeviceAddress());
            setDeviceWriteRegister(cc.getDeviceWriteRegister());
            setDeviceReadRegister(cc.getDeviceReadRegister());
            setDeviceWidth(cc.getDeviceWidth());
            setBitMask(cc.getBitMask());
//            bufferWriter = cc.bufferWriter;
//            atomicWriter = cc.atomicWriter;
//            bufferReader = cc.bufferReader;
//            atomicReader = cc.atomicReader;
            updateWriters();
        }

        private interface ByteBufferCollector {
            int collect(ByteBuffer buffer, int length);
        }

        private interface AtomicWriter {
            int collect(int value);
        }

        private interface AtomicReader {
            int collect();
        }

        private ChannelAddressAttr busAddress;
        private DeviceAddressAttr deviceAddress;
        private DeviceRegisterAttr deviceWriteRegister;
        private DeviceRegisterAttr deviceReadRegister;
        private DeviceWidthAttr deviceWidth = new DeviceWidthAttr(DeviceWidthAttr.DeviceWidth.WIDTH8);
        private Integer deviceHandle;
        private BitMaskAttr bitMask;
        private ByteBufferCollector bufferWriter;
        private AtomicWriter atomicWriter;
        private ByteBufferCollector bufferReader;
        private AtomicReader atomicReader;

        public BitMaskAttr getBitMask() {
            return bitMask;
        }

        private void setBitMask(BitMaskAttr bitMask) {
            this.bitMask = bitMask;
        }

        public DeviceRegisterAttr getDeviceReadRegister() {
            return deviceReadRegister == null ? deviceWriteRegister : deviceReadRegister;
        }

        private void setDeviceReadRegister(DeviceRegisterAttr deviceReadRegister) {
            this.deviceReadRegister = deviceReadRegister;
        }

        public ChannelAddressAttr getBusAddress() {
            return busAddress;
        }

        private void setBusAddress(ChannelAddressAttr busAddress) {
            this.busAddress = busAddress;
        }

        public DeviceAddressAttr getDeviceAddress() {
            return deviceAddress;
        }

        private void setDeviceAddress(DeviceAddressAttr deviceAddress) {
            this.deviceAddress = deviceAddress;
        }

        public DeviceRegisterAttr getDeviceWriteRegister() {
            return deviceWriteRegister == null ? deviceReadRegister : deviceWriteRegister;
        }

        private void setDeviceWriteRegister(DeviceRegisterAttr deviceWriteRegister) {
            this.deviceWriteRegister = deviceWriteRegister;
        }

        public DeviceWidthAttr getDeviceWidth() {
            return deviceWidth;
        }

        private void setDeviceWidth(DeviceWidthAttr deviceWidth) {
            this.deviceWidth = deviceWidth;
        }

        public Integer getDeviceHandle() {
            return deviceHandle;
        }

        private void setDeviceHandle(Integer deviceHandle) {
            this.deviceHandle = deviceHandle;
        }

        protected void updateWriters() {
            switch (getDeviceWidth().getWidth()) {
                case WIDTH8:
                    bufferReader = (buffer, length) -> {
                        for (int bi = 0; bi < length; ++bi) {
                            buffer.put((byte) atomicReader.collect());
                        }
                        return length;
                    };
                    if (getDeviceWriteRegister() != null) {
                        if (getBitMask() != null) {
                            atomicReader = () -> I2C.readByte(getDeviceHandle(), getDeviceReadRegister().getRegister()) & getBitMask().getMask();
                            atomicWriter = (value) -> I2C.writeByteMask(getDeviceHandle(), getDeviceWriteRegister().getRegister(), (byte) value, (byte) getBitMask().getMask());
                            bufferWriter = (buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeBytesMask(getDeviceHandle(), getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array(), (byte) getBitMask().getMask());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        } else {
                            atomicReader = () -> I2C.readByte(getDeviceHandle(), getDeviceReadRegister().getRegister());
                            atomicWriter = (value) -> I2C.writeByte(getDeviceHandle(), getDeviceWriteRegister().getRegister(), (byte) value);
                            bufferWriter = (buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeBytes(getDeviceHandle(), getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        }
                    } else {
                        if (getBitMask() != null) {
                            atomicReader = () -> I2C.readByteDirect(getDeviceHandle()) & getBitMask().getMask();
                            atomicWriter = (value) -> I2C.writeByteDirectMask(getDeviceHandle(), (byte) value, (byte) getBitMask().getMask());
                            bufferWriter = (buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeBytesDirectMask(getDeviceHandle(), length, buffer.position(), buffer.array(), (byte) getBitMask().getMask());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        } else {
                            atomicReader = () -> I2C.readByteDirect(getDeviceHandle());
                            atomicWriter = (value) -> I2C.writeByteDirect(getDeviceHandle(), (byte) value);
                            bufferWriter = (buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeBytesDirect(getDeviceHandle(), length, buffer.position(), buffer.array());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        }
                    }
                case WIDTH16:
                    if (getDeviceWriteRegister() != null) {
                        bufferReader = (buffer, length) -> {
                            for (int bi = 0; bi < length; ++bi) {
                                buffer.putChar((char) atomicReader.collect());
                            }
                            return length;
                        };
                        if (getBitMask() != null) {
                            atomicReader = () -> I2C.readWord(getDeviceHandle(), getDeviceReadRegister().getRegister()) & getBitMask().getMask();
                            atomicWriter = (value) -> I2C.writeWordMask(getDeviceHandle(), getDeviceWriteRegister().getRegister(), (char) value, (char) getBitMask().getMask());
                            bufferWriter = (buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeWordsMask(getDeviceHandle(), getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array(), (char) getBitMask().getMask());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(buffer.getChar());
                                    }
                                    return length;
                                }
                            };
                            break;
                        } else {
                            atomicReader = () -> I2C.readWord(getDeviceHandle(), getDeviceReadRegister().getRegister());
                            atomicWriter = (value) -> I2C.writeWord(getDeviceHandle(), getDeviceWriteRegister().getRegister(), (char) value);
                            bufferWriter = (buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeWords(getDeviceHandle(), getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(buffer.getChar());
                                    }
                                    return length;
                                }
                            };
                            break;
                        }
                    }
                default:
                    throw new UnsupportedOperationException(getDeviceWidth().toString() + getDeviceReadRegister() + getDeviceWriteRegister() + getBitMask());
            }
        }

        final protected ChannelContext clone() {
            return new ChannelContext(this);
        }

        private <T> T isAttribute(Class<? extends T> t, Attribute newAttr, T currentAttr) {
            return t.isAssignableFrom(newAttr.getClass()) ? (T) newAttr : currentAttr;
        }

        public ChannelContext applyAttributes(Collection<Attribute> attr) throws IOException {
            boolean deviceChange = false;
            boolean writersChange = false;
            for (Attribute a : attr) {
//            logger.debug(a.toString());
                if (ActionAttr.class.isAssignableFrom(a.getClass())) {
                    ((ActionAttr) a).performAction();
                    continue;
                }
                // clone the original context so that we do not upset any references to it
//                ctxt = clone();
                if ((busAddress = isAttribute(ChannelAddressAttr.class, a, busAddress)) == a) {
                    deviceChange = true;
                    continue;
                }
                if ((deviceAddress = isAttribute(DeviceAddressAttr.class, a, deviceAddress)) == a) {
                    deviceChange = true;
                    continue;
                }
                if ((deviceWidth = isAttribute(DeviceWidthAttr.class, a, deviceWidth)) == a) {
                    writersChange = true;
                    continue;
                }
                if ((bitMask = isAttribute(BitMaskAttr.class, a, bitMask)) == a) {
                    writersChange = true;
                    continue;
                }
                if ((deviceWriteRegister = isAttribute(DeviceWriteRegisterAttr.class, a, deviceWriteRegister)) == a) {
                    writersChange = true;
                    continue;
                }
                if ((deviceReadRegister = isAttribute(DeviceReadRegisterAttr.class, a, deviceReadRegister)) == a) {
                    writersChange = true;
                    continue;
                }
                if ((deviceReadRegister = isAttribute(DeviceRegisterAttr.class, a, deviceReadRegister)) == a) {
                    deviceWriteRegister = deviceReadRegister;
                    continue;
                }
            }
            if (deviceChange && deviceAddress != null && busAddress != null) {
                setDeviceHandle(openDevice(getBusAddress().getAddress(), getDeviceAddress().getAddress()));
            }
            if (writersChange) {
                updateWriters();
            }
            // this may be a clone of the original if we modified it so it is up to the caller to ensure it
            // references this returned value and not the one passed originally passed in
            return this;
        }

    }

    protected ChannelContext defaultContext = new ChannelContext();

    public RPiI2CChannel() {
    }

    public RPiI2CChannel(Attribute... attr) {
        setAttribute(attr);
    }

    @Override
    public boolean supportsAttribute(Class<? extends Attribute> attr) {
        return DeviceAddressAttr.class.isAssignableFrom(attr) ||
                DeviceWriteRegisterAttr.class.isAssignableFrom(attr) ||
                DeviceReadRegisterAttr.class.isAssignableFrom(attr) ||
                DeviceRegisterAttr.class.isAssignableFrom(attr) ||
                ChannelAddressAttr.class.isAssignableFrom(attr) ||
                FixedWaitAttr.class.isAssignableFrom(attr) ||
                DeviceWidthAttr.class.isAssignableFrom(attr) ||
                BitMaskAttr.class.isAssignableFrom(attr);
    }

    @Override
    public DeviceChannel createChannel(Attribute... attr) {
        RPiI2CChannel dc = new RPiI2CChannel();
        dc.bufferAttributes.putAll(bufferAttributes);
        dc.defaultContext = defaultContext.clone();
        dc.setAttribute(attr);
        return dc;
    }

    @Override
    public int write(byte b, Attribute... attr) throws IOException {
        return attr.length > 0 ?
                defaultContext.clone().applyAttributes(Arrays.asList(attr)).atomicWriter.collect(b) :
                defaultContext.atomicWriter.collect(b);
    }

    @Override
    public byte read(Attribute... attr) throws IOException {
        return (byte) (attr.length > 0 ?
                defaultContext.clone().applyAttributes(Arrays.asList(attr)).atomicReader.collect() :
                defaultContext.atomicReader.collect());
    }

    @Override
    synchronized public boolean isOpen() {
        return defaultContext.getDeviceHandle() != null;
    }

    static public int openDevice(int bus, int address) throws IOException {
        synchronized (deviceHandleMap) {
            String id = String.valueOf(bus) + ":" + String.valueOf(address);
            Integer handle = deviceHandleMap.get(id);
            if (handle == null) {
                handle = net.audumla.devices.io.i2c.jni.rpi.I2C.open("/dev/i2c-" + bus, address);
                if (handle < 0) {
                    throw new IOException("Cannot open I2C Bus [/dev/i2c-" + bus + "] received " + handle);
                }
                deviceHandleMap.put(id, handle);
                logger.debug("Opened Device on '/dev/i2c-" + bus + "' at Address 0x" + Integer.toHexString(address));
            } else {
                logger.debug("Found open Device on '/dev/i2c-" + bus + "' at Address 0x" + Integer.toHexString(address));
            }
            return handle;
        }
    }

    @Override
    synchronized public void close() throws IOException {
        synchronized (deviceHandleMap) {
            if (isOpen()) {
                I2C.close(defaultContext.getDeviceHandle());
                for (Map.Entry<String, Integer> i : new ArrayList<>(deviceHandleMap.entrySet())) {
                    if (i.getValue().equals(defaultContext.getDeviceHandle())) {
                        deviceHandleMap.remove(i.getKey());
                    }
                }
            }
        }
    }

    @Override
    public int read(ByteBuffer src) throws IOException {
        final ChannelContext ctxt = defaultContext.clone();
        return collectBytes(src, ctxt, new ChannelContext.ByteBufferCollector() {
            @Override
            public int collect(ByteBuffer buffer, int length) {
                return ctxt.bufferReader.collect(buffer, length);
            }
        });
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        final ChannelContext ctxt = defaultContext.clone();
        return collectBytes(src, ctxt, new ChannelContext.ByteBufferCollector() {
            @Override
            public int collect(ByteBuffer buffer, int length) {
                return ctxt.bufferWriter.collect(buffer, length);
            }
        });
    }

    private int collectBytes(ByteBuffer src, ChannelContext ctxt, ChannelContext.ByteBufferCollector collector) throws IOException {
        int bytesCollected = 0;
        Set<Integer> ks = bufferAttributes.keySet();
        Iterator<Integer> it = ks.iterator();
        for (int i = 0; i < ks.size() + 1; ++i) {
            int nextPosition = it.hasNext() ? it.next() : src.limit();
            int runLength = nextPosition > src.limit() ? src.limit() - src.position() : nextPosition - src.position();
            if (runLength > 0) bytesCollected += collector.collect(src, runLength);
            if (src.position() == src.limit()) break;
            if (i < ks.size()) ctxt = ctxt.applyAttributes(bufferAttributes.get(nextPosition).getAttributeReferences());
        }
        return bytesCollected;
    }

    @Override
    public void setAttribute(Attribute... attr) {
        try {
            defaultContext = defaultContext.applyAttributes(Arrays.asList(attr));
        } catch (IOException e) {
            logger.warn("Unable to set attribute on I2C Channel", e);
        }
    }
}
