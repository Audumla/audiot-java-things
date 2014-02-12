package net.audumla.devices.io.channel;

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

import net.audumla.devices.io.i2c.I2CDevice;
import net.audumla.devices.io.i2c.I2CDeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class I2CDeviceChannel extends AbstractDeviceChannel {
    private static final Logger logger = LoggerFactory.getLogger(I2CDeviceChannel.class);

    protected I2CDeviceFactory factory;
    protected ChannelContext context;

    protected static class ChannelContext {

        private ChannelAddressAttr busAddress;
        private DeviceAddressAttr deviceAddress;
        private DeviceRegisterAttr deviceWriteRegister;
        private DeviceRegisterAttr deviceReadRegister;
        private DeviceWidthAttr deviceWidth = new DeviceWidthAttr(DeviceWidthAttr.DeviceWidth.WIDTH8);
        private I2CDevice device;
        private BitMaskAttr bitMask;
        private ByteBufferCollector bufferWriter;
        private AtomicWriter atomicWriter;
        private ByteBufferCollector bufferReader;
        private AtomicReader atomicReader;
        protected I2CDeviceFactory factory;

        public ChannelContext(I2CDeviceFactory factory) {
            this.factory = factory;
            updateWriters();
        }

        public ChannelContext(ChannelContext cc, Attribute... attr) throws IOException {
            this.factory = cc.factory;
            setBusAddress(cc.getBusAddress());
            setDevice(cc.getDevice());
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

        public ChannelContext(I2CDeviceFactory factory, Attribute... attr) throws IOException {
            this.factory = factory;
            applyAttributes(Arrays.asList(attr), false);
            if (bufferWriter == null) {
                // if the attributes did not set the writers then manually call update
                updateWriters();
            }
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

        public I2CDevice getDevice() {
            return device;
        }

        private void setDevice(I2CDevice deviceHandle) {
            this.device = deviceHandle;
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
                            atomicReader = (ctxt) -> ctxt.getDevice().readByte(ctxt.getDeviceReadRegister().getRegister()) & ctxt.getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> ctxt.getDevice().writeByteMask(ctxt.getDeviceWriteRegister().getRegister(), (byte) value, (byte) ctxt.getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = ctxt.getDevice().writeBytesMask(ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array(), (byte) ctxt.getBitMask().getMask());
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
                            atomicReader = (ctxt) -> ctxt.getDevice().readByte(ctxt.getDeviceReadRegister().getRegister());
                            atomicWriter = (ctxt, value) -> ctxt.getDevice().writeByte(ctxt.getDeviceWriteRegister().getRegister(), (byte) value);
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = ctxt.getDevice().writeBytes(ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.array());
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
                            atomicReader = (ctxt) -> ctxt.getDevice().readByteDirect() & ctxt.getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> ctxt.getDevice().writeByteDirectMask((byte) value, (byte) ctxt.getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = ctxt.getDevice().writeBytesDirectMask(length, buffer.position(), buffer.array(), (byte) ctxt.getBitMask().getMask());
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
                            atomicReader = (ctxt) -> ctxt.getDevice().readByteDirect();
                            atomicWriter = (ctxt, value) -> ctxt.getDevice().writeByteDirect((byte) value);
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = ctxt.getDevice().writeBytesDirect(length, buffer.position(), buffer.array());
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
                            atomicReader = (ctxt) -> ctxt.getDevice().readWord(ctxt.getDeviceReadRegister().getRegister()) & ctxt.getBitMask().getMask();
                            atomicWriter = (ctxt, value) -> ctxt.getDevice().writeWordMask(ctxt.getDeviceWriteRegister().getRegister(), (char) value, (char) ctxt.getBitMask().getMask());
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = ctxt.getDevice().writeWordsMask(ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array(), (char) ctxt.getBitMask().getMask());
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
                            atomicReader = (ctxt) -> ctxt.getDevice().readWord(ctxt.getDeviceReadRegister().getRegister());
                            atomicWriter = (ctxt, value) -> ctxt.getDevice().writeWord(ctxt.getDeviceWriteRegister().getRegister(), (char) value);
                            bufferWriter = (ctxt, buffer, length) -> {
                                if (buffer.hasArray()) {
                                    int ret = ctxt.getDevice().writeWords(ctxt.getDeviceWriteRegister().getRegister(), length, buffer.position(), buffer.asCharBuffer().array());
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
                if (DeviceWriteRegisterAttr.class.isAssignableFrom(a.getClass())) {
                    (ctxt = (ctxt == this && clone) ? clone() : ctxt).setDeviceWriteRegister((DeviceRegisterAttr) a);
                    writersChange = true;
                    continue;
                }
                if (DeviceReadRegisterAttr.class.isAssignableFrom(a.getClass())) {
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
                ctxt.setDevice(factory.open(ctxt.getBusAddress().getAddress(), ctxt.getDeviceAddress().getAddress()));
            }
            if (writersChange) {
                ctxt.updateWriters();
            }
            // this may be a clone of the original if we modified it so it is up to the caller to ensure it
            // references this returned value and not the one passed originally passed in
            return ctxt;
        }


    }

    public I2CDeviceChannel(I2CDeviceFactory factory) {
        this.factory = factory;
        context = new ChannelContext(factory);
    }

    public I2CDeviceChannel(I2CDeviceFactory factory, Attribute... attr) throws IOException {
        this.factory = factory;
        context = new ChannelContext(factory,attr);
    }

    public I2CDeviceChannel(I2CDeviceChannel channel, Attribute... attr) throws IOException {
        this.factory = channel.factory;
        context = new ChannelContext(channel.context, attr);
        bufferAttributes.putAll(channel.bufferAttributes);
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
    public DeviceChannel createChannel(Attribute... attr) throws IOException {
        return new I2CDeviceChannel(this, attr);
    }

    @Override
    public int write(byte b, Attribute... attr) throws IOException {
        ChannelContext ctxt = attr.length > 0 ? context.applyAttributes(Arrays.asList(attr), true) : context;
        return ctxt.atomicWriter.collect(ctxt, b);
    }

    @Override
    public byte read(Attribute... attr) throws IOException {
        ChannelContext ctxt = attr.length > 0 ? context.applyAttributes(Arrays.asList(attr), true) : context;
        return (byte) ctxt.atomicReader.collect(ctxt);
    }

    @Override
    synchronized public boolean isOpen() {
        return context.getDevice() != null;
    }

    @Override
    public void close() throws IOException {
        context.device.close();
    }


    @Override
    public int read(ByteBuffer src) throws IOException {
        return collectBytes(src, context, context.bufferReader::collect);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return collectBytes(src, context, context.bufferWriter::collect);
    }

    private int collectBytes(ByteBuffer src, ChannelContext ctxt, ChannelContext.ByteBufferCollector collector) throws IOException {
        ChannelContext origCtxt = ctxt; // keep a reference to the original context so that we can ensure we only clone once in this method if necessary
        int bytesCollected = 0;
        Set<Integer> ks = bufferAttributes.keySet();
        Iterator<Integer> it = ks.iterator();
        // for each attribute that has been assigned a position in the channel we execute the read/write command for the appropriate length
        // and then apply the attribute. We then continue on until the next attribute position.
        for (int i = 0; i < ks.size() + 1; ++i) {
            int nextPosition = it.hasNext() ? it.next() : src.limit();
            int runLength = nextPosition > src.limit() ? src.limit() - src.position() : nextPosition - src.position();
            if (runLength > 0) bytesCollected += collector.collect(ctxt, src, runLength);
            if (src.position() == src.limit()) break;
            if (i < ks.size())
                ctxt = ctxt.applyAttributes(bufferAttributes.get(nextPosition).getAttributeReferences(), ctxt == origCtxt); // only clone the context if we have not already done so.
        }
        return bytesCollected;
    }

    @Override
    public void setAttribute(Attribute... attr) {
        try {
            context = context.applyAttributes(Arrays.asList(attr), true);
        } catch (IOException e) {
            logger.warn("Unable to set attribute on I2C Channel", e);
        }
    }
}
