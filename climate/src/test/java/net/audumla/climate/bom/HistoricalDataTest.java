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

public class HistoricalDataTest {

    @Test
    public void testHistoricalDataLoad() {

        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, 200);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086351");
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateData data = station.getClimateData(now);
        Assert.assertNotNull(data.getEvaporation());
        /*
        try {
            data.getEvapotranspiration();
            Assert.fail("There is no evapotranspiration data for this station");
        }
        catch (Exception ex) {
            
        }
        */
        Assert.assertNotNull(data.getMaximumTemperature());
        //Assert.assertEquals(10.9, data.getMinimumTemperature(),0.01);
        //Assert.assertEquals(0.2, data.getMaximumRelativeHumidity(),0.01);
        //Assert.assertEquals(0.2, data.getMinimumRelativeHumidity(),0.01);
        // Assert.assertEquals(2.4, data.getRainfall(),0.01);
        //Assert.assertEquals(100, data.getRainfallProbability(),0.01);
        Assert.assertEquals(now, data.getTime());

    }

    @Test
    public void testHistoricalDataLoadEvapotranspiration() {

        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, 200);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086068");
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateData data = station.getClimateData(now);
        Assert.assertNotNull(data.getEvapotranspiration());
        //Assert.assertEquals(14.9, data.getMaximumTemperature(),0.02);
        //Assert.assertEquals(11, data.getMinimumTemperature(),0.01);
        //Assert.assertEquals(0.2, data.getMaximumRelativeHumidity(),0.01);
        //Assert.assertEquals(0.2, data.getMinimumRelativeHumidity(),0.01);
        Assert.assertNotNull(data.getRainfall());
        Assert.assertNotNull(data.getRainfallProbability());
        Assert.assertEquals(now, data.getTime());

    }


    @Test
    public void testHistoricalObservations() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, 200);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        c.set(Calendar.HOUR, 10);
        Date time1 = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086351");
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateData data = station.getClimateData(now);
        ClimateObservation obs1 = data.getObservation(time1, ClimateData.ObservationMatch.CLOSEST);
        Assert.assertNotNull(obs1.getTemperature());
        Assert.assertNotNull(obs1.getHumidity());

        c.set(Calendar.HOUR, 11);
        Date time2 = c.getTime();
        ClimateObservation obs2 = data.getObservation(time2, ClimateData.ObservationMatch.CLOSEST);
        Assert.assertNotNull(obs2);
        Assert.assertNotNull(obs2.getTemperature());
        Assert.assertSame(obs1.getTime(), obs2.getTime());


        c.set(Calendar.HOUR, 9);
        time1 = c.getTime();
        Assert.assertEquals(time1, obs1.getTime());
        Assert.assertEquals(time1, obs2.getTime());
        Assert.assertEquals(obs1.getTime(), obs2.getTime());
    }
}
