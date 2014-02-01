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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        // set the default bus to 0 for v1 or 1 for v2
        ChannelAddressAttr busAddress = null;
        DeviceAddressAttr deviceAddress = null;
        DeviceRegisterAttr deviceRegister = null;
        Integer busHandle = null;
        int bytesWritten = 0;
        int position = 0;

        for (Attribute a : defaultAttributes) {
            setAttribute(src, 0, a);
        }

        Map<Integer, PositionAttribute> ba = getBufferAttributes(src);
        ba.put(src.limit(),new PositionAttribute());
        for (Integer nextPosition : ba.keySet()) {
            int runLength = nextPosition - position;
            if (runLength > 0) {
                byte[] run = new byte[runLength];
                logger.debug("Writing to [I2Cbus #"+busAddress+":Address "+deviceAddress+"] [limit:"+src.limit()+"][capacity:"+src.capacity()+"][StartPos:"+position+"][Length:"+runLength+"]");
//                logger.debug("Writing to [I2Cbus #"+busAddress.getAddress()+":Address 0x"+Integer.toHexString(deviceAddress.getAddress())+"] [limit:"+src.limit()+"][capacity:"+src.capacity()+"][StartPos:"+position+"][Length:"+runLength+"]");
                src.get(run, position, runLength);
                if (busHandle != null) {
                    if (deviceAddress != null) {
                        if (deviceRegister == null) {
                            logger.debug("Writing to [I2Cbus #"+busAddress.getAddress()+":Address 0x"+Integer.toHexString(deviceAddress.getAddress())+"] [limit:"+src.limit()+"][capacity:"+src.capacity()+"]");
                            for (byte b : run) {
                                I2C.i2cWriteByteDirect(busHandle, deviceAddress.getAddress(), b);
                            }
                        } else {
                            logger.debug("Writing to [I2Cbus #"+busAddress.getAddress()+":Address 0x"+Integer.toHexString(deviceAddress.getAddress())+":Register 0x"+deviceRegister.getRegister()+"] [limit:"+src.limit()+"][capacity:"+src.capacity()+"]");
                            for (byte b : run) {
                                I2C.i2cWriteByte(busHandle, deviceAddress.getAddress(), deviceRegister.getRegister(), b);
                            }
                        }
                        bytesWritten += run.length;
                    }
                }
            }
            for (Attribute a : ba.get(nextPosition).getAttributeReferences()) {
                if ((busAddress = isAttribute(ChannelAddressAttr.class, a, busAddress)) == a) {
                    busHandle = getBusHandle(busAddress.getAddress());
                    continue;
                }
                if ((deviceAddress = isAttribute(DeviceAddressAttr.class, a, deviceAddress)) == a) continue;
                if ((deviceRegister = isAttribute(DeviceRegisterAttr.class, a, deviceRegister)) == a) continue;
                if (SleepAttr.class.isAssignableFrom(a.getClass())) ((SleepAttr) a).sleep();
            }
            position = nextPosition;
        }
        bufferAttributes.remove(src);
        return bytesWritten;
    }

    @Override
    public boolean supportsAttribute(Class<? extends Attribute> attr) {
        return DeviceAddressAttr.class.isAssignableFrom(attr) || DeviceRegisterAttr.class.isAssignableFrom(attr) || ChannelAddressAttr.class.isAssignableFrom(attr) || SleepAttr.class.isAssignableFrom(attr);
    }

    @Override
    public DeviceChannel createChannel(Attribute... attr) {
        return new RPiI2CChannel(attr);
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
