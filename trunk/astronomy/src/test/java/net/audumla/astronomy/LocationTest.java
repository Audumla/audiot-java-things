package net.audumla.astronomy;

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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationTest {
    private static final Logger logger = LoggerFactory.getLogger(LocationTest.class);

    @Test
    public void testSerialization() throws Exception {
        Geolocation loc1 = Geolocation.newGeoLocation(37.7, 145.1, 100);
        Geolocation loc2 = Geolocation.newGeoLocation(loc1.toString());
        assert loc1.getLongitude().equals(loc2.getLongitude());
        assert loc1.getLatitude().equals(loc2.getLatitude());
        assert loc1.getElevation().equals(loc2.getElevation());

    }
}
