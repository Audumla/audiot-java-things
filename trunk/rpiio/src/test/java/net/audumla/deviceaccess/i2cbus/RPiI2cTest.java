package net.audumla.deviceaccess.i2cbus;

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

import net.audumla.deviceaccess.PeripheralManager;
import net.audumla.deviceaccess.i2cbus.rpi.RPiI2CPeripheralProvider;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPiI2cTest {
    private static final Logger logger = LoggerFactory.getLogger(RPiI2cTest.class);

    public I2CDevice createI2CDevice() throws Exception {
        I2CDeviceConfig c = new I2CDeviceConfig(1, 0x21);
        RPiI2CPeripheralProvider provider = new RPiI2CPeripheralProvider();
        I2CDevice device = provider.open(c,null, PeripheralManager.SHARED);
        assert device != null;
        return device;
    }

    @Test
    public void PCF8574readwrite8() throws Exception {
        I2CDevice d = createI2CDevice();
        byte val = (byte) 0x01;
        d.write(val);
        Assert.assertEquals(val, d.read());
        d.write((byte) ~val);
        Assert.assertEquals(~val, d.read());
    }

    @Test
    public void PCF8574RWMask8() throws Exception {
        I2CDevice d = createI2CDevice();
        d.write((byte) 0x00);
        byte val = (byte) 0xff;
        d.setMask(0x0f);
        d.write(val);
        Assert.assertEquals(0x0f, d.read());
        d.setMask(0xff);
        d.write((byte) ~val);
        Assert.assertEquals(~val & 0x0f, d.read());
    }
}
