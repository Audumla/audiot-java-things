package net.audumla.climate;
/**
 * User: audumla
 * Date: 1/08/13
 * Time: 9:42 PM
 */

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DataSourceTest {
    private static final Logger logger = LogManager.getLogger(DataSourceTest.class);

    protected ClimateData getData(Date now) {
        ClimateDataSource source = ClimateDataSourceFactory.newInstance();
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
        cd = getData(DateUtils.addDays(now,-1));
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
        cd = getData(DateUtils.addDays(now,-1));
        cd.getRainfall();
        Assert.assertEquals(cd.getDataSource().getType(), ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);
    }
}
