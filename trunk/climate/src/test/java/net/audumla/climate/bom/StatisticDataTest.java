package net.audumla.climate.bom;

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

import net.audumla.climate.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StatisticDataTest {

    @Test
    public void testStatisticalDataLoad() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, 100);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086351");
        ClimateObserver station = new BOMStatisticalClimateDataObserver(source);
        ClimateData data = station.getClimateData(now);
        Assert.assertEquals(2.6, data.getEvaporation(), 0.01);
        Assert.assertEquals(20.5, data.getMaximumTemperature(), 0.01);
        Assert.assertEquals(9.7, data.getMinimumTemperature(), 0.01);
    }

    @Test
    public void testStatisticalObservations() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, 100);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086351");
        ClimateObserver station = new BOMStatisticalClimateDataObserver(source);
        ClimateData data = station.getClimateData(now);
        c.set(Calendar.HOUR, 10);
        ClimateObservation obs = data.getObservation(c.getTime(), ClimateData.ObservationMatch.CLOSEST);
        Assert.assertEquals(14.7, obs.getTemperature(), 0.01);
        Assert.assertEquals(73, obs.getHumidity(), 0.01);
        try {
            obs.getApparentTemperature();
            Assert.fail("Should not execute");
        } catch (Exception e) {
        }
        c.setTime(obs.getTime());
        Assert.assertEquals(c.get(Calendar.HOUR_OF_DAY), 9);
    }

    @Test
    public void testStatisticalDataSupported() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        WritableClimateData data = ClimateDataFactory.newWritableClimateData(new BOMStatisticalClimateDataObserver(source), source);
        data.setSunrise(new Date());
        try {
            data.getSunset();
            Assert.fail("Method should not execute");
        } catch (Exception ex) {
        }

    }

    @Test
    public void testStatisticalMethods() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, 100);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086351");
        ClimateObserver station = new BOMStatisticalClimateDataObserver(source);
        ClimateData data = station.getClimateData(now);
        try {
            data.getSunrise();
            Assert.fail("Method should not execute");
        } catch (Exception ex) {
        }
        try {
            data.getSunset();
            Assert.fail("Method should not execute");
        } catch (Exception ex) {
        }
        try {
            data.getMaximumHumidity();
            Assert.fail("Method should not execute");
        } catch (Exception ex) {
        }
        try {
            data.getMinimumHumidity();
            Assert.fail("Method should not execute");
        } catch (Exception ex) {
        }
        try {
            data.getAverageWindSpeed();
            Assert.fail("Method should not execute");
        } catch (Exception ex) {
        }
        try {
            data.getEvapotranspiration();
            Assert.fail("Method should not execute");
        } catch (Exception ex) {
        }

    }
}
