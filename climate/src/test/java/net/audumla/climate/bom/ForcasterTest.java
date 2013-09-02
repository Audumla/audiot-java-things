package net.audumla.climate.bom;

import net.audumla.climate.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ForcasterTest {

    @Test
    public void testForcast() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.newInstance();
        source.setId("086351");
        ClimateObserver forcaster = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateData data = forcaster.getClimateData(now);
        Assert.assertEquals(now, data.getTime());
        Assert.assertNotNull(data.getMaximumTemperature());
    }

}
