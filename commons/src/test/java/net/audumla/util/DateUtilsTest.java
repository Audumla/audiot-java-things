package net.audumla.util;

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

import net.audumla.Time;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class DateUtilsTest {

    @Before
    public void setUp() throws Exception {
        //TimeZone.setDefault();

    }

    @Test
    public void testDateOffsetSecond() {
        Date t1 = new Date(0);
        t1 = Time.offset(t1, 0, 0, 1);
        Calendar c = Calendar.getInstance();
        c.setTime(t1);
        Assert.assertEquals(c.get(Calendar.SECOND), 1);
        Assert.assertEquals(c.get(Calendar.DAY_OF_YEAR), 1);
        Assert.assertEquals(c.get(Calendar.MINUTE), 0);
    }

    @Test
    public void testDateOffsetSecondBoundary() {
        Date t1 = new Date(0);
        Calendar c = Calendar.getInstance();
        c.setTime(t1);
        c.set(Calendar.DAY_OF_YEAR, 365);
        c.set(Calendar.YEAR, 2001);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        t1 = c.getTime();
        t1 = Time.offset(t1, 0, 0, 1);
        c.setTime(t1);
        Assert.assertEquals(c.get(Calendar.YEAR), 2002);
        Assert.assertEquals(c.get(Calendar.DAY_OF_YEAR), 1);
        Assert.assertEquals(c.get(Calendar.MINUTE), 0);
        Assert.assertEquals(c.get(Calendar.SECOND), 0);
    }

    @Test
    public void testDateOffsetHourBoundary() {
        Date t1 = new Date(0);
        Calendar c = Calendar.getInstance();
        c.setTime(t1);
        c.set(Calendar.DAY_OF_YEAR, 365);
        c.set(Calendar.YEAR, 2001);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        t1 = c.getTime();
        t1 = Time.offset(t1, 1, 0, 0);
        c.setTime(t1);
        Assert.assertEquals(c.get(Calendar.YEAR), 2002);
        Assert.assertEquals(c.get(Calendar.DAY_OF_YEAR), 1);
        Assert.assertEquals(c.get(Calendar.MINUTE), 0);
        Assert.assertEquals(c.get(Calendar.SECOND), 0);
    }

    @Test
    public void testDateOffsetMinuteBoundary() {
        Date t1 = new Date(0);
        Calendar c = Calendar.getInstance();
        c.setTime(t1);
        c.set(Calendar.DAY_OF_YEAR, 365);
        c.set(Calendar.YEAR, 2001);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);
        t1 = c.getTime();
        t1 = Time.offset(t1, 0, 1, 0);
        c.setTime(t1);
        Assert.assertEquals(c.get(Calendar.YEAR), 2002);
        Assert.assertEquals(c.get(Calendar.DAY_OF_YEAR), 1);
        Assert.assertEquals(c.get(Calendar.MINUTE), 0);
        Assert.assertEquals(c.get(Calendar.SECOND), 0);
    }

    @Test
    public void testDateOffsetLargeHourBoundary() {
        Date t1 = new Date(0);
        Calendar c = Calendar.getInstance();
        c.setTime(t1);
        c.set(Calendar.DAY_OF_YEAR, 364);
        c.set(Calendar.YEAR, 2001);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        t1 = c.getTime();
        t1 = Time.offset(t1, 25, 0, 0);
        c.setTime(t1);
        Assert.assertEquals(c.get(Calendar.YEAR), 2002);
        Assert.assertEquals(c.get(Calendar.DAY_OF_YEAR), 1);
        Assert.assertEquals(c.get(Calendar.MINUTE), 0);
    }

    @Test
    public void testNullTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(Calendar.HOUR_OF_DAY, 15);
        Assert.assertEquals(15, c.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(1970, c.get(Calendar.YEAR));
        Assert.assertEquals(0, c.get(Calendar.MONTH));
        Assert.assertEquals(1, c.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, c.get(Calendar.MINUTE));
        Assert.assertEquals(0, c.get(Calendar.MILLISECOND));
        Assert.assertEquals(1, c.get(Calendar.DAY_OF_YEAR));

    }
}
