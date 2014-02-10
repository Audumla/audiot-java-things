package net.audumla.devices.io.channel.gpio;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MCP2308DeviceChannel extends AbstractDeviceChannel {
    private static final Logger logger = LoggerFactory.getLogger(MCP2308DeviceChannel.class);

    public static final int MCP23008_IODIR = 0x00;
    public static final int MCP23008_IPOL = 0x01;
    public static final int MCP23008_GPINTEN = 0x02;
    public static final int MCP23008_DEFVAL = 0x03;
    public static final int MCP23008_INTCON = 0x04;
    public static final int MCP23008_IOCON = 0x05;
    public static final int MCP23008_GPPU = 0x06;
    public static final int MCP23008_INTF = 0x07;
    public static final int MCP23008_INTCAP = 0x08;
    public static final int MCP23008_GPIO = 0x09;
    public static final int MCP23008_OLAT = 0x0A;

    protected DeviceChannel targetChannel;

    public MCP2308DeviceChannel(DeviceChannel targetChannel) {
        this.targetChannel = targetChannel;
    }

    @Override
    public boolean supportsAttribute(Class<? extends Attribute> attr) {
        return DeviceWriteRegisterAttr.class.isAssignableFrom(attr) || FixedWaitAttr.class.isAssignableFrom(attr);
    }
    @Override
    public DeviceChannel createChannel(Attribute... attr) {
        return new MCP2308DeviceChannel(targetChannel.createChannel(attr));
    }

    @Override
    public int write(byte b) throws IOException {
        return targetChannel.write(b);
    }

    @Override
    public byte read() throws IOException {
        return targetChannel.read();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return targetChannel.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return targetChannel.write(src);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }
}
