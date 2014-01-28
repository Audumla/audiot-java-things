package net.audumla.climate;
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
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * User: audumla
 * JulianDate: 27/07/13
 * Time: 6:53 PM
 */

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class LoadTest {
    private static final Logger logger = Logger.getLogger(LoadTest.class);

    @Test
    public void testDoubleLoadByID() {
        Date now = new Date();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086351");
        ClimateObserver s1 = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        int size = ClimateObserverCatalogue.getInstance().stationMap.size();
        ClimateObserver s2 = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        Assert.assertEquals(ClimateObserverCatalogue.getInstance().stationMap.size(), size);
    }

    @Test
    public void testDoubleLoadByCoordinates() {
        Date now = new Date();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        ClimateObserver s1 = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        int size = ClimateObserverCatalogue.getInstance().stationMap.size();
        ClimateObserver s2 = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        Assert.assertEquals(ClimateObserverCatalogue.getInstance().stationMap.size(), size);
    }

    @Test
    public void testDoubleLoadByCoordinatesWithID() {
        Date now = new Date();
        ClimateDataSource source1 = ClimateDataSourceFactory.getInstance().newInstance();
        source1.setLatitude(-37.84);
        source1.setLongitude(144.98);
        ClimateObserver s1 = ClimateObserverCatalogue.getInstance().getClimateObserver(source1);
        int size = ClimateObserverCatalogue.getInstance().stationMap.size();
        ClimateDataSource source2 = ClimateDataSourceFactory.getInstance().newInstance();
        source2.setId("086351");
        ClimateObserver s2 = ClimateObserverCatalogue.getInstance().getClimateObserver(source2);

        Assert.assertEquals(ClimateObserverCatalogue.getInstance().stationMap.size(), size);
    }
}
