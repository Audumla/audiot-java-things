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

        private interface ByteBufferWriter {
            void writeBuffer(ChannelContext ctxt, ByteBuffer buffer, int length, int masked);
        }

        private ChannelAddressAttr busAddress;
        private DeviceAddressAttr deviceAddress;
        private DeviceWriteRegisterAttr deviceWriteRegister;
        private DeviceReadRegisterAttr deviceReadRegister;
        private DeviceWidthAttr deviceWidth = new DeviceWidthAttr(DeviceWidthAttr.DeviceWidth.WIDTH8);
        private Integer deviceHandle;
        private BitMaskAttr bitMask;
        private ByteBufferWriter writer;

        public DeviceReadRegisterAttr getDeviceReadRegister() {
            return deviceReadRegister;
        }

        public void setDeviceReadRegister(DeviceReadRegisterAttr deviceReadRegister) {
            this.deviceReadRegister = deviceReadRegister;
        }

        public ChannelAddressAttr getBusAddress() {
            return busAddress;
        }

        public void setBusAddress(ChannelAddressAttr busAddress) {
            this.busAddress = busAddress;
        }

        public DeviceAddressAttr getDeviceAddress() {
            return deviceAddress;
        }

        public void setDeviceAddress(DeviceAddressAttr deviceAddress) {
            this.deviceAddress = deviceAddress;
        }

        public DeviceWriteRegisterAttr getDeviceWriteRegister() {
            return deviceWriteRegister;
        }

        public void setDeviceWriteRegister(DeviceWriteRegisterAttr deviceWriteRegister) {
            this.deviceWriteRegister = deviceWriteRegister;
            setDeviceWidth(getDeviceWidth());
        }

        public DeviceWidthAttr getDeviceWidth() {
            return deviceWidth;
        }

        public void setDeviceWidth(DeviceWidthAttr deviceWidth) {
            //TODO: this needs to be simplified and most of the logic pushed down into the native code so that we can mask within a bulk write
            switch (deviceWidth.getWidth()) {
                case WIDTH8:
                    if (getBitMask() != null) {
                        final int mask = getBitMask().getMask();
                        logger.debug("Mask:"+Integer.toBinaryString(getBitMask().getMask()));
                        writer = (getDeviceWriteRegister() != null) ?
                                (ctxt, buffer, length, masked) -> {
                                    logger.debug("Masked:"+Integer.toBinaryString(masked));
                                    for (int bi = 0; bi < length; ++bi) {
                                        I2C.writeByte(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), (byte) ((buffer.get() & mask) | masked));
                                    }
                                }
                                :
                                (ctxt, buffer, length, masked) -> {
                                    logger.debug("Masked:"+Integer.toBinaryString(masked));
                                    for (int bi = 0; bi < length; ++bi) {
                                        I2C.writeByteDirect(ctxt.getDeviceHandle(), (byte) ((buffer.get() & mask) | masked));
                                    }
                                };
                    } else {
                        writer = (getDeviceWriteRegister() != null) ?
                                (ctxt, buffer, length, masked) -> {
                                    for (int bi = 0; bi < length; ++bi) {
                                        I2C.writeByte(ctxt.getDeviceHandle(), ctxt.getDeviceWriteRegister().getRegister(), buffer.get());
                                    }
                                }
                                :
                                (ctxt, buffer, length, masked) -> {
                                    for (int bi = 0; bi < length; ++bi) {
                                        I2C.writeByteDirect(ctxt.getDeviceHandle(), buffer.get());
                                    }
                                };
                    }
                    break;
                case WIDTH16:
                case WIDTH32:
                case WIDTH64:
                    throw new UnsupportedOperationException(deviceWidth.getWidth().name());
            }

            this.deviceWidth = deviceWidth;
        }

        public Integer getDeviceHandle() {
            return deviceHandle;
        }

        public void setDeviceHandle(Integer deviceHandle) {
            this.deviceHandle = deviceHandle;
            setDeviceWidth(getDeviceWidth()); // reset write methods
        }

        public BitMaskAttr getBitMask() {
            return bitMask;
        }

        public void setBitMask(BitMaskAttr bitMask) {
            this.bitMask = bitMask;
            setDeviceWidth(getDeviceWidth()); // reset write methods
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
            return cc;
        }

        private <T> T isAttribute(Class<T> t, Attribute newAttr, T currentAttr) {
            return t.isAssignableFrom(newAttr.getClass()) ? (T) newAttr : currentAttr;
        }

        public ChannelContext applyAttributes(Collection<Attribute> attr) throws IOException {
            ChannelContext ctxt = this;
            boolean deviceChange = false;
            for (Attribute a : attr) {
//            logger.debug(a.toString());
                if (ActionAttr.class.isAssignableFrom(a.getClass())) {
                    ((ActionAttr) a).performAction();
                    continue;
                }
                // clone the original context so that we do not upset any references to it
                ctxt = ctxt.clone();
                if ((ctxt.bitMask = isAttribute(BitMaskAttr.class, a, ctxt.bitMask)) == a) continue;
                deviceChange |= (ctxt.busAddress = isAttribute(ChannelAddressAttr.class, a, ctxt.busAddress)) == a;
                deviceChange |= (ctxt.deviceAddress = isAttribute(DeviceAddressAttr.class, a, ctxt.deviceAddress)) == a;
                ctxt.deviceWriteRegister = isAttribute(DeviceWriteRegisterAttr.class, a, ctxt.deviceWriteRegister);
                ctxt.deviceReadRegister = isAttribute(DeviceReadRegisterAttr.class, a, ctxt.deviceReadRegister);
            }
            if (deviceChange && ctxt.deviceAddress != null && ctxt.busAddress != null) {
                ctxt.setDeviceHandle(openDevice(ctxt.getBusAddress().getAddress(), ctxt.getDeviceAddress().getAddress()));
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
    public int write(ByteBuffer src) throws IOException {
        int bytesWritten = 0;
        src.position(0);
        ChannelContext ctxt = defaultContext;
        Set<Integer> ks = bufferAttributes.keySet();
        Iterator<Integer> it = ks.iterator();
        // if we have a mask then read the current value so that when we apply the mask to the write value we can fill in any unmasked bits
        // with the existing bits that are on the device. This prevents us writting 0s over any existing 1s on the target device
        // To get the masked value we need to invert the mask so that we only grab the values that are not to be overridden.
        int masked = defaultContext.getBitMask() != null ? read() | ~defaultContext.getBitMask().getMask() : 0;
        for (int i = 0; i < ks.size() + 1; ++i) {
            int nextPosition = it.hasNext() ? it.next() : src.limit();
            int runLength = nextPosition - src.position();
            if (runLength > 0) {
                ctxt.writer.writeBuffer(ctxt, src, runLength, masked);
            }
            if (i < ks.size()) {
                ctxt = ctxt.applyAttributes(bufferAttributes.get(nextPosition).getAttributeReferences());
            }
        }
        return bytesWritten;
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
//        logger.debug("Write byte I2C ["+b+"] - "+defaultContext.getBusAddress()+" "+defaultContext.getDeviceAddress() + " " + defaultContext.getDeviceWriteRegister());
        if (defaultContext.getDeviceWriteRegister() == null) {
            return I2C.writeByteDirect(defaultContext.getDeviceHandle(), b);
        } else {
            return I2C.writeByte(defaultContext.getDeviceHandle(), defaultContext.getDeviceWriteRegister().getRegister(), b);
        }
    }

    @Override
    public int read() throws IOException {
        switch (defaultContext.getDeviceWidth().getWidth()) {
            case WIDTH8:
                return defaultContext.getDeviceReadRegister() == null ?
                        I2C.readByteDirect(defaultContext.getDeviceHandle()) :
                        I2C.readByte(defaultContext.getDeviceHandle(), defaultContext.getDeviceReadRegister().getRegister());
            case WIDTH16:
                if (defaultContext.getDeviceReadRegister() != null)
                    return I2C.readWord(defaultContext.getDeviceHandle(), defaultContext.getDeviceReadRegister().getRegister());
        }
        return -1;
    }

    @Override
    synchronized public boolean isOpen() {
        boolean open = false;
        for (Integer f : deviceHandleMap.values()) {
            open = open || f != null;
        }
        return open;
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
            for (Integer e : deviceHandleMap.values()) {
                I2C.close(e);
            }
            deviceHandleMap.clear();
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int i = 0;
        switch (defaultContext.getDeviceWidth().getWidth()) {
            case WIDTH8:
                for (i = 0; i < dst.position() - dst.limit(); ++i) {
                    dst.put((byte) read());
                }
                break;
            case WIDTH16:
                for (i = 0; i < (dst.position() - dst.limit()) / 2; ++i) {
                    dst.putChar((char) read());
                }
                break;
            case WIDTH32:
                for (i = 0; i < (dst.position() - dst.limit() / 4); ++i) {
                    dst.putInt(read());
                }
                break;
            default:
                return -1;
        }
        return i;
    }

    @Override
    protected void addDefaultAttribute(Attribute... attr) {
        try {
            defaultContext = defaultContext.applyAttributes(Arrays.asList(attr));
        } catch (IOException e) {
            logger.warn("Unable to set attribute on I2C Channel", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RPiI2CChannel)) return false;

        RPiI2CChannel that = (RPiI2CChannel) o;

        if (defaultContext != null ? !defaultContext.equals(that.defaultContext) : that.defaultContext != null)
            return false;

        return true;
    }


}
