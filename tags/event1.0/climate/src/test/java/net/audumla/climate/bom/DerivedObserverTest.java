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
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DerivedObserverTest {
    ClimateData data;

    @Before
    public void setup() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.YEAR, 2013);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086068");
        ClimateObserver forcaster = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        data = forcaster.getClimateData(now);
    }

    @Test
    public void testSaturationDerivationByTemp() {
        Assert.assertEquals(26.6, data.getMaximumTemperature(), 0.1);
        Assert.assertEquals(3.482, data.getMaximumSaturationVapourPressure(), 0.1);
//        Assert.assertEquals(14.3, data.getMinimumTemperature(), 0.1);
        Assert.assertEquals(13.1, data.getMinimumTemperature(), 0.1);
//        Assert.assertEquals(1.629, data.getMinimumSaturationVapourPressure(), 0.1);
        Assert.assertEquals(1.432, data.getMinimumSaturationVapourPressure(), 0.1);
    }

    @Test
    public void testVapourPressureDerivation() {
        Assert.assertEquals(3.308, data.getMaximumVapourPressure(), 0.1);
        Assert.assertEquals(0.587, data.getMinimumVapourPressure(), 0.1);
    }
}
