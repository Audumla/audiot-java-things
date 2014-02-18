package com.oracle.deviceaccess;

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

import com.oracle.deviceaccess.i2cbus.I2CDevice;
import com.oracle.deviceaccess.i2cbus.I2CDeviceConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class J2MEBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(J2MEBaseTest.class);


    @Test
    public void testConfig() throws Exception {
        I2CDeviceConfig c = new I2CDeviceConfig(1,1,1,1);
        assert 1 == c.getAddress();
        assert 1 == c.getAddressSize();
        assert 1 == c.getClockFrequency();
        assert 1 == c.getDeviceNumber();

    }

    @Test
    public void testBases() throws Exception {



//        I2CDeviceConfig dc = new I2CDeviceConfig(1,2,3,1000);
//        ByteBuffer b = ByteBuffer.allocate(10);
//        b.put(new byte[] {1,2,3,4,5,6,7,8,9});
//        I2CCombinedMessage m = new I2CCombinedMessage();
//        m.appendRead(d, b);
//        PortListener
//        m.transfer();
//        assert dc.getDeviceNumber() == 1;
//        assert dc.getAddress() == 2;
//        assert dc.getAddressSize() == 3;
//        assert dc.getClockFrequency() == 1000;


        I2CDevice i1 = PeripheralManager.open(5, I2CDevice.class, PeripheralManager.EXCLUSIVE);
        I2CDevice i2 = PeripheralManager.open(5, I2CDevice.class);
        I2CDevice i3 = PeripheralManager.open(new I2CDeviceConfig(0,0,0,0));
        I2CDevice i4 = PeripheralManager.open(new I2CDeviceConfig(0, 0, 0, 0), PeripheralManager.EXCLUSIVE);
//        I2CDevice i5 = PeripheralManager.open();



    }
}
