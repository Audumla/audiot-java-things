package net.audumla;

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

import junit.framework.Assert;
import net.audumla.devices.rpi.SystemInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitiveTest {
    private static final Logger logger = LoggerFactory.getLogger(PrimitiveTest.class);

    @Test
    public void testByteComparison() throws Exception {
        byte bb = (byte) 255;
        byte hb = (byte) 0xff;
        assert bb == hb;
        Assert.assertEquals(Integer.toBinaryString((byte) bb), "11111111111111111111111111111111");
        int ii = bb;
        assert ii == bb;
        Assert.assertEquals(Integer.toBinaryString(ii), "11111111111111111111111111111111");
//        System.out.println(ii);
//        System.out.println(bb);
    }

    @Test
    public void testMask() throws Exception {
        byte value = (byte) 0xff;
        byte mask = 0x0f;
        byte data = 0x00;
        Assert.assertEquals((value & mask) | (data & ~mask),0x0f);

    }
}
