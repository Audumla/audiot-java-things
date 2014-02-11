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

        private interface ByteBufferCollector {
            int collect(ChannelContext ctxt, ByteBuffer buffer, int length);
        }

        private interface AtomicWriter {
            int collect(ChannelContext ctxt, int value);
        }

        private interface AtomicReader {
            int collect(ChannelContext ctxt);
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
                    bufferReader = (ctxt, buffer, length) -> {
                        for (int bi = 0; bi < length; ++bi) {
                            buffer.put((byte) atomicReader.collect(ctxt));
                        }
                        return length;
                    };
                    if (getDeviceWriteRegister() != null) {
                        if (getBitMask() != null) {
                            atomicReader = (ctxt) -> I2C.readByte(ctxt.getDeviceHandle(), ctxt.getDeviceReadRegister().getRegister()) & getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> I2C.writeByteMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), (byte) value, (byte) getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    return I2C.writeBytesMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array(), (byte) getBitMask().getMask());
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(ctxt, buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        } else {
                            atomicReader = (ctxt) -> I2C.readByte(ctxt.getDeviceHandle(), ctxt.getDeviceReadRegister().getRegister());
                            atomicWriter = (ctxt, value) -> I2C.writeByte(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), (byte) value);
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    return I2C.writeBytes(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array());
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(ctxt, buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        }
                    } else {
                        if (getBitMask() != null) {
                            atomicReader = (ctxt) -> I2C.readByteDirect(ctxt.getDeviceHandle()) & getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> I2C.writeByteDirectMask(ctxt.getDeviceHandle(), (byte) value, (byte) getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    return I2C.writeBytesDirectMask(ctxt.getDeviceHandle(), length, buffer.position(), buffer.array(), (byte) getBitMask().getMask());
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(ctxt, buffer.get());
                                    }
                                    return length;
                                }
                            };
                        } else {
                            atomicReader = (ctxt) -> I2C.readByteDirect(ctxt.getDeviceHandle());
                            atomicWriter = (ctxt, value) -> I2C.writeByteDirect(ctxt.getDeviceHandle(), (byte) value);
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    return I2C.writeBytesDirect(ctxt.getDeviceHandle(), length, buffer.position(), buffer.array());
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(ctxt, buffer.get());
                                    }
                                    return length;
                                }
                            };
                        }
                    }
                case WIDTH16:
                    bufferReader = (ctxt, buffer, length) -> {
                        for (int bi = 0; bi < length; ++bi) {
                            buffer.putChar((char) atomicReader.collect(ctxt));
                        }
                        return length;
                    };
                    if (getBitMask() != null) {
                        atomicReader = (ctxt) -> I2C.readWord(ctxt.getDeviceHandle(), ctxt.getDeviceReadRegister().getRegister()) & getBitMask().getMask();
                        atomicWriter = (ctxt, value) -> I2C.writeWordMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), (char) value, (char) getBitMask().getMask());
                        bufferWriter = (ctxt, buffer, length) -> {
                            if (buffer.hasArray()) {
                                return I2C.writeWordsMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array(), (char) getBitMask().getMask());
                            } else {
                                for (int bi = 0; bi < length; ++bi) {
                                    atomicWriter.collect(ctxt, buffer.getChar());
                                }
                                return length;
                            }
                        };
                        break;
                    } else {
                        atomicReader = (ctxt) -> I2C.readWord(ctxt.getDeviceHandle(), ctxt.getDeviceReadRegister().getRegister());
                        atomicWriter = (ctxt, value) -> I2C.writeWord(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), (char) value);
                        bufferWriter = (ctxt, buffer, length) -> {
                            if (buffer.hasArray()) {
                                return I2C.writeWords(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array());
                            } else {
                                for (int bi = 0; bi < length; ++bi) {
                                    atomicWriter.collect(ctxt, buffer.getChar());
                                }
                                return length;
                            }
                        };
                        break;
                    }
                default:
                    throw new UnsupportedOperationException(getDeviceWidth().toString()+getDeviceReadRegister()+getDeviceWriteRegister()+getBitMask());
            }
        }

        final protected ChannelContext clone() {
            ChannelContext cc = new ChannelContext();
            cc.setBusAddress(getBusAddress());
            cc.setDeviceHandle(getDeviceHandle());
            cc.setDeviceAddress(getDeviceAddress());
            cc.setDeviceWriteRegister(getDeviceWriteRegister());
            cc.setDeviceReadRegister(getDeviceReadRegister());
            cc.setDeviceWidth(getDeviceWidth());
            cc.setBitMask(getBitMask());
            cc.updateWriters();
            return cc;
        }

        private <T> T isAttribute(Class<? extends T> t, Attribute newAttr, T currentAttr) {
            return t.isAssignableFrom(newAttr.getClass()) ? (T) newAttr : currentAttr;
        }

        public ChannelContext applyAttributes(Collection<Attribute> attr) throws IOException {
            ChannelContext ctxt = this;
            boolean deviceChange = false;
            boolean writersChange = false;
            for (Attribute a : attr) {
//            logger.debug(a.toString());
                if (ActionAttr.class.isAssignableFrom(a.getClass())) {
                    ((ActionAttr) a).performAction();
                    continue;
                }
                // clone the original context so that we do not upset any references to it
                ctxt = ctxt.clone();
                if ((ctxt.busAddress = isAttribute(ChannelAddressAttr.class, a, ctxt.busAddress)) == a) {
                    deviceChange = true;
                    continue;
                }
                if ((ctxt.deviceAddress = isAttribute(DeviceAddressAttr.class, a, ctxt.deviceAddress)) == a)  {
                    deviceChange = true;
                    continue;
                }
                if ((ctxt.deviceWidth = isAttribute(DeviceWidthAttr.class, a, ctxt.deviceWidth)) == a)  {
                    writersChange = true;
                    continue;
                }
                if ((ctxt.bitMask = isAttribute(BitMaskAttr.class, a, ctxt.bitMask)) == a)  {
                    writersChange = true;
                    continue;
                }
                if ((ctxt.deviceWriteRegister = isAttribute(DeviceWriteRegisterAttr.class, a, ctxt.deviceWriteRegister)) == a)  {
                    writersChange = true;
                    continue;
                }
                if ((ctxt.deviceReadRegister = isAttribute(DeviceReadRegisterAttr.class, a, ctxt.deviceReadRegister)) == a)  {
                    writersChange = true;
                    continue;
                }
            }
            if (deviceChange && ctxt.deviceAddress != null && ctxt.busAddress != null) {
                ctxt.setDeviceHandle(openDevice(ctxt.getBusAddress().getAddress(), ctxt.getDeviceAddress().getAddress()));
            }
            if (writersChange) {
                ctxt.updateWriters();
            }
            // this may be a clone of the original if we modified it so it is up to the caller to ensure it
            // references this returned value and not the one passed originally passed in
            return ctxt;
        }

    }

    protected ChannelContext defaultContext = new ChannelContext();

    public RPiI2CChannel() {
    }

    public RPiI2CChannel(Attribute... attr) {
        addDefaultAttribute(attr);
    }

    @Override
    public boolean supportsAttribute(Class<? extends Attribute> attr) {
        return DeviceAddressAttr.class.isAssignableFrom(attr) ||
                DeviceWriteRegisterAttr.class.isAssignableFrom(attr) ||
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
        dc.addDefaultAttribute(attr);
        return dc;
    }

    @Override
    public int write(byte b) throws IOException {
        return defaultContext.atomicWriter.collect(defaultContext, b);
    }

    @Override
    public byte read() throws IOException {
        return (byte) defaultContext.atomicReader.collect(defaultContext);
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
        return collectBytes(src,(ctxt, buff,length) -> ctxt.bufferReader.collect(ctxt,buff,length));
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return collectBytes(src,(ctxt, buff,length) -> ctxt.bufferWriter.collect(ctxt,buff,length));
    }

    private int collectBytes(ByteBuffer src, ChannelContext.ByteBufferCollector collector) throws IOException {
        int bytesCollected = 0;
        ChannelContext ctxt = defaultContext;
        Set<Integer> ks = bufferAttributes.keySet();
        Iterator<Integer> it = ks.iterator();
        for (int i = 0; i < ks.size() + 1; ++i) {
            int nextPosition = it.hasNext() ? it.next() : src.limit();
            int runLength = nextPosition > src.limit() ? src.limit()-src.position() : nextPosition - src.position();
            if (runLength > 0) bytesCollected += collector.collect(ctxt, src, runLength);
            if (src.position() == src.limit()) break;
            if (i < ks.size()) ctxt = ctxt.applyAttributes(bufferAttributes.get(nextPosition).getAttributeReferences());
        }
        return bytesCollected;
    }

    protected void addDefaultAttribute(Attribute... attr) {
        try {
            defaultContext = defaultContext.applyAttributes(Arrays.asList(attr));
        } catch (IOException e) {
            logger.warn("Unable to set attribute on I2C Channel", e);
        }
    }
}
