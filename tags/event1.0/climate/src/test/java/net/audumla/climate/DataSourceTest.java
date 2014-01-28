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
 * JulianDate: 1/08/13
 * Time: 9:42 PM
 */

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DataSourceTest {
    private static final Logger logger = Logger.getLogger(DataSourceTest.class);

    protected ClimateData getData(Date now) {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086068");
        ClimateObserver obs = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        return obs.getClimateData(now);
    }

    @Test
    public void testStatisticalCall() {
        Date now = new Date();
        ClimateData cd = getData(DateUtils.setYears(now, 1990));
        cd.getMaximumTemperature();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.MONTHLY_STATISTICAL);

    }

    @Test
    public void testHistoricalCall() {
        Date now = new Date();
        ClimateData cd = getData(DateUtils.addDays(now, -30));
        cd.getMaximumTemperature();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);

    }

    @Test
    public void testForecastCall() {
        Date now = new Date();
        ClimateData cd = getData(DateUtils.addDays(now, 2));
        cd.getMaximumTemperature();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST);
    }

    @Test
    public void testObservationCall() {
        Date now = new Date();
        ClimateData cd = getData(now);
        cd.getObservation(now, ClimateData.ObservationMatch.CLOSEST);
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.PERIODIC_OBSERVATION);
    }

    @Test
    public void testDerivedCall() {
        Date now = new Date();
        ClimateData cd = getData(now);
        cd.getEvapotranspiration();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DERIVED);
    }

    @Test
    public void testMulticall() {
        Date now = new Date();
        ClimateData cd = getData(now);
        cd.getMaximumTemperature();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST);
        cd.getMinimumTemperature();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);
        cd.getEvapotranspiration();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DERIVED);
        ClimateObservation obs = cd.getObservation(now, ClimateData.ObservationMatch.CLOSEST);
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.PERIODIC_OBSERVATION);
        cd.getAverageWindSpeed();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.MONTHLY_STATISTICAL);
    }

    @Test
    public void testRainfallcall() {
        Date now = new Date();
        ClimateData cd = getData(now);
        cd.getRainfall();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST);
        cd = getData(DateUtils.addDays(now, -1));
        cd.getRainfall();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);
    }

    @Test
    public void testTodaycall() {
        Date now = new Date();
        ClimateData cd = getData(now);
        cd.getMaximumTemperature();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST);
        cd.getRainfall();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST);
    }

    @Test
    public void testYesterdaysRainfall() throws Exception {
        Date now = new Date();
        ClimateData cd = getData(DateUtils.addDays(now, -1));
        cd.getRainfall();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);

    }

}
