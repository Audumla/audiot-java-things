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

import net.audumla.collections.ExpiringMap;
import net.audumla.Time;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ExpiringCacheTest {
    @Test
    public void testExpirary() {
        Time.setNow(new Date());
        ExpiringMap ec = new ExpiringMap();
        ec.addExpirationRule("test.*", Time.offset(Time.getZeroDate(), 0, 0, 2));
        ec.add("default");
        ec.add("testCache");
        Assert.assertFalse(ec.hasDataExpired("testCache"));
        Assert.assertFalse(ec.hasDataExpired("default"));
        Time.setNow(Time.offset(Time.getNow(), 0, 0, 3));
        Assert.assertTrue(ec.hasDataExpired("testCache"));
        Assert.assertFalse(ec.hasDataExpired("default"));
    }

    @Test
    public void testPropertyLoad() throws ConfigurationException {
        Time.setNow(new Date());
        ExpiringMap ec = new ExpiringMap(new PropertiesConfiguration("cache.properties"));
        ec.add("default");
        ec.add("testCache.json");
        Time.setNow(Time.offset(Time.getNow(), 0, 0, 14));
        Assert.assertFalse(ec.hasDataExpired("testCache.json"));
        Assert.assertFalse(ec.hasDataExpired("default"));
        Time.setNow(Time.offset(Time.getNow(), 0, 0, 16));
        Assert.assertTrue(ec.hasDataExpired("testCache.json"));
        Assert.assertFalse(ec.hasDataExpired("default"));
    }
}
