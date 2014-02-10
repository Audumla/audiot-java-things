package net.audumla.devices.io.channel.i2c.rpi;

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

import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.activator.factory.PCF8574GPIOActivatorFactory;
import net.audumla.devices.activator.factory.RPIGPIOActivatorFactory;
import net.audumla.devices.io.channel.*;
import net.audumla.devices.io.channel.i2c.RPiI2CChannel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class I2ctest {
    private static final Logger logger = LoggerFactory.getLogger(I2ctest.class);


    @Test
    public void PCF8574readwrite8() throws Exception {
        DeviceChannel d = new RPiI2CChannel().createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(PCF8574GPIOActivatorFactory.PCF8574_0x21));
        byte val = (byte) 0x01;
        d.write(val);
        Assert.assertEquals(val,d.read());
        d.write((byte) ~val);
        Assert.assertEquals(~val,d.read());
    }

}