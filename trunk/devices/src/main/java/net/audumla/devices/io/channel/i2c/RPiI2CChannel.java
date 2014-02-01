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

import com.pi4j.jni.I2C;
import com.pi4j.wiringpi.Gpio;
import net.audumla.devices.io.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class RPiI2CChannel extends AbstractDeviceChannel {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2CChannel.class);

    static protected Map<Integer, Integer> busHandleMap = new HashMap<>();

    static {
        if (Gpio.piBoardRev() == 1) {
            busHandleMap.put(0, null);
        } else {
            busHandleMap.put(0, null);
            busHandleMap.put(1, null);
        }
    }

    private class ChannelContext {
        ChannelAddressAttr busAddress;
        DeviceAddressAttr deviceAddress;
        DeviceRegisterAttr deviceRegister;
        Integer busHandle;
    }

    public RPiI2CChannel() {
    }

    protected RPiI2CChannel(Attribute... attr) {
        addDefaultAttribute(attr);
    }

    protected <T> T isAttribute(Class<T> t, Attribute newAttr, T currentAttr) {
        return t.isAssignableFrom(newAttr.getClass()) ? (T) newAttr : currentAttr;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        int bytesWritten = 0;
        src.position(0);
        ChannelContext ctxt = new ChannelContext();

        Set<Integer> ks = bufferAttributes.keySet();
        Iterator<Integer> it = ks.iterator();
        for (int i = 0; i <= ks.size(); ++i) {
            Integer nextPosition = it.hasNext() ? it.next() : src.limit();
            int runLength = nextPosition - src.position();
            if (runLength > 0) {
                byte[] run = new byte[runLength];
                src.get(run, 0, runLength);
                if (ctxt.busHandle != null) {
                    if (ctxt.deviceAddress != null) {
                        if (ctxt.deviceRegister == null) {
                            for (byte b : run) {
                                I2C.i2cWriteByteDirect(ctxt.busHandle, ctxt.deviceAddress.getAddress(), b);
                            }
                        } else {
                            for (byte b : run) {
                                I2C.i2cWriteByte(ctxt.busHandle, ctxt.deviceAddress.getAddress(), ctxt.deviceRegister.getRegister(), b);
                            }
                        }
                        bytesWritten += run.length;
                    }
                }
            }
            applyAttributes(ctxt, bufferAttributes.get(nextPosition).getAttributeReferences());
        }
        return bytesWritten;
    }

    protected ChannelContext applyAttributes(ChannelContext ctxt, Collection<Attribute> attr) throws IOException {
        for (Attribute a : attr) {
            if ((ctxt.busAddress = isAttribute(ChannelAddressAttr.class, a, ctxt.busAddress)) == a) {
                ctxt.busHandle = getBusHandle(ctxt.busAddress.getAddress());
                continue;
            }
            if ((ctxt.deviceAddress = isAttribute(DeviceAddressAttr.class, a, ctxt.deviceAddress)) == a) continue;
            if ((ctxt.deviceRegister = isAttribute(DeviceRegisterAttr.class, a, ctxt.deviceRegister)) == a) continue;
            if (SleepAttr.class.isAssignableFrom(a.getClass())) ((SleepAttr) a).sleep();
        }
        return ctxt;
    }

    @Override
    public boolean supportsAttribute(Class<? extends Attribute> attr) {
        return DeviceAddressAttr.class.isAssignableFrom(attr) || DeviceRegisterAttr.class.isAssignableFrom(attr) || ChannelAddressAttr.class.isAssignableFrom(attr) || SleepAttr.class.isAssignableFrom(attr);
    }

    @Override
    public DeviceChannel createChannel(Attribute... attr) {
        RPiI2CChannel dc = new RPiI2CChannel(attr);
        dc.bufferAttributes.putAll(bufferAttributes);
        return dc;
    }

    @Override
    public void write(byte b) throws IOException {
        ChannelContext ctxt = new ChannelContext();
        ctxt = applyAttributes(ctxt, bufferAttributes.get(0).getAttributeReferences());
        if (ctxt.busHandle != null) {
            if (ctxt.deviceAddress != null) {
                if (ctxt.deviceRegister == null) {
                    I2C.i2cWriteByteDirect(ctxt.busHandle, ctxt.deviceAddress.getAddress(), b);
                } else {
                    I2C.i2cWriteByte(ctxt.busHandle, ctxt.deviceAddress.getAddress(), ctxt.deviceRegister.getRegister(), b);
                }
            }
        }
    }

    @Override
    synchronized public boolean isOpen() {
        boolean open = false;
        for (Integer f : busHandleMap.values()) {
            open = open || f != null;
        }
        return open;
    }

    synchronized protected int getBusHandle(int bus) throws IOException {
        Integer handle = busHandleMap.get(bus);
        if (handle == null) {
            handle = I2C.i2cOpen("/dev/i2c-" + bus);
            if (handle < 0) {
                throw new IOException("Cannot open I2C Bus [/dev/i2c-" + bus + "] received " + handle);
            }
            busHandleMap.put(bus, handle);
        }
        return handle;
    }

    @Override
    synchronized public void close() throws IOException {
        for (Integer f : new ArrayList<Integer>(busHandleMap.values())) {
            if (f != null) {
                I2C.i2cClose(f);
                busHandleMap.remove(f);
            }
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return 0;
    }
}
