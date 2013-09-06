package net.audumla.climate.bom;

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
