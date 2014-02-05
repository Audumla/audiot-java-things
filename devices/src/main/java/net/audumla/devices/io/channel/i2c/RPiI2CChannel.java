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

    static protected final Map<Integer, Integer> deviceHandleMap = new HashMap<>();

    protected static class ChannelContext {

        private interface ByteBufferWriter {
            void writeBuffer(ChannelContext ctxt, ByteBuffer buffer, int length);
        }

        private ChannelAddressAttr busAddress;
        private DeviceAddressAttr deviceAddress;
        private DeviceRegisterAttr deviceRegister;
        private DeviceWidthAttr deviceWidth = new DeviceWidthAttr(DeviceWidthAttr.DeviceWidth.WIDTH8);
        private Integer deviceHandle;
        private ByteBufferWriter writer;

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

        public DeviceRegisterAttr getDeviceRegister() {
            return deviceRegister;
        }

        public void setDeviceRegister(DeviceRegisterAttr deviceRegister) {
            this.deviceRegister = deviceRegister;
            setDeviceWidth(getDeviceWidth());
        }

        public DeviceWidthAttr getDeviceWidth() {
            return deviceWidth;
        }

        public void setDeviceWidth(DeviceWidthAttr deviceWidth) {
            switch (deviceWidth.getWidth()) {
                case WIDTH16:
                    break;
                case WIDTH32:
                    throw new UnsupportedOperationException(deviceWidth.getWidth().name());
                case WIDTH864:
                    throw new UnsupportedOperationException(deviceWidth.getWidth().name());
                case WIDTH8:
                default:
                    if (getDeviceRegister() != null) {
                        writer = (ctxt, buffer, length) -> {
//                            if (buffer.hasArray()) {
//                                byte[] bytes = buffer.array();
//                                I2C.writeBytes(ctxt.getDeviceHandle(),ctxt.getDeviceRegister().getRegister(),length,buffer.position(),bytes);
//                                buffer.position(buffer.position()+length);
//                            } else {
                                for (int bi = 0; bi < length; ++bi) {
                                    I2C.writeByte(ctxt.getDeviceHandle(), ctxt.getDeviceRegister().getRegister(), buffer.get());
                                }
//                            }
                        };
                    } else {
                        writer = (ctxt, buffer, length) -> {
                            for (int bi = 0; bi < length; ++bi) {
                                I2C.writeByteDirect(ctxt.deviceHandle, buffer.get());
                            }
                        };
                    }
                    break;
            }

            this.deviceWidth = deviceWidth;
        }

        public Integer getDeviceHandle() {
            return deviceHandle;
        }

        public void setDeviceHandle(Integer deviceHandle) {
            this.deviceHandle = deviceHandle;
        }

        final protected ChannelContext clone() {
            ChannelContext cc = new ChannelContext();
            cc.setBusAddress(getBusAddress());
            cc.setDeviceHandle(getDeviceHandle());
            cc.setDeviceAddress(getDeviceAddress());
            cc.setDeviceRegister(getDeviceRegister());
            cc.setDeviceWidth(getDeviceWidth());
            return cc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChannelContext that = (ChannelContext) o;
            return !(busAddress != null ? !busAddress.equals(that.busAddress) : that.busAddress != null) && !(deviceAddress != null ? !deviceAddress.equals(that.deviceAddress) : that.deviceAddress != null) && !(deviceRegister != null ? !deviceRegister.equals(that.deviceRegister) : that.deviceRegister != null);
        }

    }

    protected ChannelContext defaultContext = new ChannelContext();

    public RPiI2CChannel() {
    }

    public RPiI2CChannel(Attribute... attr) {
        addDefaultAttribute(attr);
    }

    protected <T> T isAttribute(Class<T> t, Attribute newAttr, T currentAttr) {
        return t.isAssignableFrom(newAttr.getClass()) ? (T) newAttr : currentAttr;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        int bytesWritten = 0;
        src.position(0);
        ChannelContext ctxt = defaultContext;
//        bufferAttributes.put(src.limit(),new PositionAttribute());
//        for (int nextPosition : bufferAttributes.keySet()) {
        Set<Integer> ks = bufferAttributes.keySet();
        Iterator<Integer> it = ks.iterator();
//        logger.debug("RPiChannel write number of bytes - "+src.limit()+" Attributes:"+ks.size());
        for (int i = 0; i < ks.size() + 1; ++i) {
            int currentPos = src.position();
            int nextPosition = it.hasNext() ? it.next() : src.limit();
            int runLength = nextPosition - currentPos;
//            logger.debug("CurrentPos:"+currentPos+" NextPosition:"+nextPosition+" RunLength:"+runLength);
            if (runLength > 0) {
                ctxt.writer.writeBuffer(ctxt, src, runLength);
            }
            if (i < ks.size()) {
                ctxt = applyAttributes(ctxt, bufferAttributes.get(nextPosition).getAttributeReferences());
            }
        }
        return bytesWritten;
    }

    protected ChannelContext applyAttributes(ChannelContext ctxt, Collection<Attribute> attr) throws IOException {
        for (Attribute a : attr) {
//            logger.debug(a.toString());
            if (ActionAttr.class.isAssignableFrom(a.getClass())) {
                ((ActionAttr) a).performAction();
                continue;
            }
            // clone the original context so that we do not upset any references to it
            ctxt = ctxt.clone();
            if ((ctxt.busAddress = isAttribute(ChannelAddressAttr.class, a, ctxt.busAddress)) == a) continue;
            if ((ctxt.deviceAddress = isAttribute(DeviceAddressAttr.class, a, ctxt.deviceAddress)) == a) {
                if (ctxt.busAddress != null) {
                    ctxt.setDeviceHandle(getDeviceHandle(ctxt.getBusAddress().getAddress(), ctxt.getDeviceAddress().getAddress()));
                }
                continue;
            }
            if ((ctxt.deviceRegister = isAttribute(DeviceRegisterAttr.class, a, ctxt.deviceRegister)) == a) continue;
        }
        // this may be a clone of the original if we modified it so it is up to the caller to ensure it
        // references this returned value and not the one passed originally passed in
        return ctxt;
    }

    @Override
    public boolean supportsAttribute(Class<? extends Attribute> attr) {
        return DeviceAddressAttr.class.isAssignableFrom(attr) ||
                DeviceRegisterAttr.class.isAssignableFrom(attr) ||
                ChannelAddressAttr.class.isAssignableFrom(attr) ||
                FixedWaitAttr.class.isAssignableFrom(attr) ||
                DeviceWidthAttr.class.isAssignableFrom(attr);
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
        ChannelContext ctxt = new ChannelContext();
        if (ctxt.deviceHandle != null) {
            if (ctxt.deviceAddress != null) {
                if (ctxt.deviceRegister == null) {
                    I2C.writeByteDirect(ctxt.deviceHandle, b);
                } else {
                    I2C.writeByte(ctxt.deviceHandle, ctxt.deviceRegister.getRegister(), b);
                }
                return 1;
            }
        }
        return 0;
    }

    @Override
    synchronized public boolean isOpen() {
        boolean open = false;
        for (Integer f : deviceHandleMap.values()) {
            open = open || f != null;
        }
        return open;
    }

    static public int getDeviceHandle(int bus, int address) throws IOException {
        synchronized (deviceHandleMap) {
            Integer handle = deviceHandleMap.get(bus);
            if (handle == null) {
                handle = net.audumla.devices.io.i2c.jni.rpi.I2C.open("/dev/i2c-" + bus, address);
                if (handle < 0) {
                    throw new IOException("Cannot open I2C Bus [/dev/i2c-" + bus + "] received " + handle);
                }
                deviceHandleMap.put(bus, handle);
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
        return 0;
    }

    @Override
    protected void addDefaultAttribute(Attribute... attr) {
        try {
            defaultContext = applyAttributes(defaultContext, Arrays.asList(attr));
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
