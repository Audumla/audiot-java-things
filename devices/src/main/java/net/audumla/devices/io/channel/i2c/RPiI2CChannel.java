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

        public ChannelContext() {
            updateWriters();
        }

        public ChannelContext(ChannelContext cc, Attribute... attr) throws IOException {
            setBusAddress(cc.getBusAddress());
            setDeviceHandle(cc.getDeviceHandle());
            setDeviceAddress(cc.getDeviceAddress());
            setDeviceWriteRegister(cc.getDeviceWriteRegister());
            setDeviceReadRegister(cc.getDeviceReadRegister());
            setDeviceWidth(cc.getDeviceWidth());
            setBitMask(cc.getBitMask());
            bufferWriter = cc.bufferWriter;
            atomicWriter = cc.atomicWriter;
            bufferReader = cc.bufferReader;
            atomicReader = cc.atomicReader;
            applyAttributes(Arrays.asList(attr), false);
//            updateWriters();
        }

        public ChannelContext(Attribute... attr) throws IOException {
            applyAttributes(Arrays.asList(attr), false);
        }

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
                            atomicReader = (ctxt) -> I2C.readByte(ctxt.getDeviceHandle(), ctxt.getDeviceReadRegister().getRegister()) & ctxt.getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> I2C.writeByteMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), (byte) value, (byte) ctxt.getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeBytesMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array(), (byte) ctxt.getBitMask().getMask());
                                    buffer.position(buffer.position() + length);
                                    return ret;
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
                                    int ret = I2C.writeBytes(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array());
                                    buffer.position(buffer.position() + length);
                                    return ret;
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
                            atomicReader = (ctxt) -> I2C.readByteDirect(ctxt.getDeviceHandle()) & ctxt.getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> I2C.writeByteDirectMask(ctxt.getDeviceHandle(), (byte) value, (byte) ctxt.getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeBytesDirectMask(ctxt.getDeviceHandle(), length, buffer.position(), buffer.array(), (byte) ctxt.getBitMask().getMask());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(ctxt, buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        } else {
                            atomicReader = (ctxt) -> I2C.readByteDirect(ctxt.getDeviceHandle());
                            atomicWriter = (ctxt, value) -> I2C.writeByteDirect(ctxt.getDeviceHandle(), (byte) value);
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeBytesDirect(ctxt.getDeviceHandle(), length, buffer.position(), buffer.array());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(ctxt, buffer.get());
                                    }
                                    return length;
                                }
                            };
                            break;
                        }
                    }
                case WIDTH16:
                    if (getDeviceWriteRegister() != null) {
                        bufferReader = (ctxt, buffer, length) -> {
                            for (int bi = 0; bi < length; ++bi) {
                                buffer.putChar((char) atomicReader.collect(ctxt));
                            }
                            return length;
                        };
                        if (getBitMask() != null) {
                            atomicReader = (ctxt) -> I2C.readWord(ctxt.getDeviceHandle(), ctxt.getDeviceReadRegister().getRegister()) & ctxt.getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> I2C.writeWordMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), (char) value, (char) ctxt.getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = I2C.writeWordsMask(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array(), (char) ctxt.getBitMask().getMask());
                                    buffer.position(buffer.position() + length);
                                    return ret;
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
                                    int ret = I2C.writeWords(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array());
                                    buffer.position(buffer.position() + length);
                                    return ret;
                                } else {
                                    for (int bi = 0; bi < length; ++bi) {
                                        atomicWriter.collect(ctxt, buffer.getChar());
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
            try {
                logger.debug("Clone!");
                return new ChannelContext(this);
            } catch (IOException e) {
                logger.error("Unable to clone", e);
            }
            return null;
        }


        public ChannelContext applyAttributes(Collection<Attribute> attr, boolean clone) throws IOException {
            boolean deviceChange = false;
            boolean writersChange = false;
            ChannelContext ctxt = this;
            for (Attribute a : attr) {
//            logger.debug(a.toString());
                if (ActionAttr.class.isAssignableFrom(a.getClass())) {
                    ((ActionAttr) a).performAction();
                    continue;
                }
                if (ChannelAddressAttr.class.isAssignableFrom(a.getClass())) {
                    (ctxt = (ctxt == this && clone) ? clone() : ctxt).setBusAddress((ChannelAddressAttr) a);
                    deviceChange = true;
                    continue;
                }
                if (DeviceAddressAttr.class.isAssignableFrom(a.getClass())) {
                    (ctxt = (ctxt == this && clone) ? clone() : ctxt).setDeviceAddress((DeviceAddressAttr) a);
                    deviceChange = true;
                    continue;
                }
                if (DeviceWidthAttr.class.isAssignableFrom(a.getClass())) {
                    (ctxt = (ctxt == this && clone) ? clone() : ctxt).setDeviceWidth((DeviceWidthAttr) a);
                    writersChange = true;
                    continue;
                }
                if (BitMaskAttr.class.isAssignableFrom(a.getClass())) {
                    (ctxt = (ctxt == this && clone) ? clone() : ctxt).setBitMask((BitMaskAttr) a);
                    writersChange = true;
                    continue;
                }
                if (DeviceRegisterAttr.class.isAssignableFrom(a.getClass())) {
                    (ctxt = (ctxt == this && clone) ? clone() : ctxt).setDeviceWriteRegister((DeviceRegisterAttr) a);
                    writersChange = true;
                    continue;
                }
                if (DeviceRegisterAttr.class.isAssignableFrom(a.getClass())) {
                    (ctxt = (ctxt == this && clone) ? clone() : ctxt).setDeviceReadRegister((DeviceRegisterAttr) a);
                    writersChange = true;
                    continue;
                }
                if (DeviceAddressAttr.class.isAssignableFrom(a.getClass())) {
                    ctxt = (ctxt == this && clone) ? clone() : ctxt;
                    ctxt.setDeviceReadRegister((DeviceRegisterAttr) a);
                    ctxt.setDeviceWriteRegister((DeviceRegisterAttr) a);
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
            return this;
        }


    }

    protected ChannelContext defaultContext;

    public RPiI2CChannel() {
        defaultContext = new ChannelContext();
    }

    public RPiI2CChannel(Attribute... attr) throws IOException {
        defaultContext = new ChannelContext(attr);
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
        try {
            dc.bufferAttributes.putAll(bufferAttributes);
            dc.defaultContext = dc.defaultContext.applyAttributes(Arrays.asList(attr), true);
        } catch (IOException e) {
            logger.error("Unable to create Channel ", e);
        }
        return dc;
    }

    @Override
    public int write(byte b, Attribute... attr) throws IOException {
        ChannelContext ctxt = attr.length > 0 ? defaultContext.applyAttributes(Arrays.asList(attr),true) : defaultContext;
        return ctxt.atomicWriter.collect(ctxt, b);
    }

    @Override
    public byte read(Attribute... attr) throws IOException {
        ChannelContext ctxt = attr.length > 0 ? defaultContext.applyAttributes(Arrays.asList(attr),true) : defaultContext;
        return (byte) ctxt.atomicReader.collect(ctxt);
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
        return collectBytes(src, defaultContext, defaultContext.bufferReader::collect);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return collectBytes(src, defaultContext, defaultContext.bufferWriter::collect);
    }

    private int collectBytes(ByteBuffer src, ChannelContext ctxt, ChannelContext.ByteBufferCollector collector) throws IOException {
        int bytesCollected = 0;
        Set<Integer> ks = bufferAttributes.keySet();
        Iterator<Integer> it = ks.iterator();
        for (int i = 0; i < ks.size() + 1; ++i) {
            int nextPosition = it.hasNext() ? it.next() : src.limit();
            int runLength = nextPosition > src.limit() ? src.limit() - src.position() : nextPosition - src.position();
            if (runLength > 0) bytesCollected += collector.collect(ctxt, src, runLength);
            if (src.position() == src.limit()) break;
            if (i < ks.size()) ctxt = ctxt.applyAttributes(bufferAttributes.get(nextPosition).getAttributeReferences(),true);
        }
        return bytesCollected;
    }

    @Override
    public void setAttribute(Attribute... attr) {
        try {
            defaultContext = defaultContext.applyAttributes(Arrays.asList(attr),true);
        } catch (IOException e) {
            logger.warn("Unable to set attribute on I2C Channel", e);
        }
    }
}
