package net.audumla.climate;

import net.audumla.climate.bom.BOMHistoricalClimateObserver;
import net.audumla.climate.bom.BOMSimpleHistoricalClimateObserver;
import net.audumla.climate.bom.BOMStatisticalClimateDataObserver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalculationTest {
    @Test
    public void testEToHourly1() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        ClimateDataFactory sw = new BOMSimpleHistoricalClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        Calendar c = GregorianCalendar.getInstance();
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);
        WritableClimateObservation obs = (WritableClimateObservation) ClimateDataFactory.newWritableClimateObservation(sw,source);

        c.set(Calendar.DAY_OF_YEAR, 274);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 2);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();

        source.setLatitude(16.21667);
        source.setLongitude(145.05);
        source.setElevation(8.0);

        obs.setTemperature(28.0);
        obs.setHumidity(90.0);
        obs.setWindSpeed(1.9);
        obs.setTime(now);
        obs.setWindSpeedHeight(2.0);

        data.addObservation(obs);

        double Ra = cc.Ra(data, now, 2);
        Assert.assertEquals(0, Ra, Ra * 0.01);
        double Rs = cc.Rs(data, now, 2);
        Assert.assertEquals(0, Rs, Rs * 0.01);
        double Rso = cc.Rso(data, now, 2);
        Assert.assertEquals(0, Rso, 0.01);
        double RsRso = cc.RsRso(Rs, Rso, cc.w(data, now, 2), cc.ws(data, now));
        Assert.assertEquals(0.8, RsRso, 0.01);
        double ea = cc.ea(data, now, 2);
        Assert.assertEquals(3.402, ea, ea * 0.01);
        double es = cc.es(data, now, 2);
        Assert.assertEquals(3.780, es, es * 0.01);
        double Rnl = cc.Rnl(data, now, 2);
        Assert.assertEquals(0.1, Rnl, Rnl * 0.01);
        double Rn = cc.Rn(data, now, 2);
        Assert.assertEquals(-0.1, Rn, 0.01);
        double T = cc.T(data, now, 2);
        Assert.assertEquals(28, T, T * 0.01);
        double G = cc.G(data, now, 1);
        Assert.assertEquals(-0.05, G, 0.0002);
        double d = cc.d(now);
        Assert.assertEquals(-0.0753, d, 0.0001);
        double value = cc.ETo(data, now, 2);

        Assert.assertEquals(0, value, 0.01);
    }


    public void testEToHourly2() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        Calendar c = GregorianCalendar.getInstance();
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);
        WritableClimateObservation obs1 = (WritableClimateObservation) ClimateDataFactory.newWritableClimateObservation(sw,source);
        WritableClimateObservation obs2 = (WritableClimateObservation) ClimateDataFactory.newWritableClimateObservation(sw,source);

        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.OCTOBER);
        c.set(Calendar.HOUR_OF_DAY, 2);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();

        source.setLatitude(16.21667);
        source.setLongitude(145.05);
        source.setElevation(8.0);

        obs1.setTemperature(28.0);
        obs1.setHumidity(90.0);
        obs1.setWindSpeed(1.9);
        obs1.setTime(now);

        c.set(Calendar.HOUR_OF_DAY, 14);
        now = c.getTime();

        obs2.setTemperature(38.0);
        obs2.setHumidity(52.0);
        obs2.setWindSpeed(3.3);
        obs2.setTime(now);

        double Ra = cc.Ra(data, now, 24);
        Assert.assertEquals(41.09, Ra, Ra * 0.01);
        double Rs = cc.Rs(data, now, 24);
        Assert.assertEquals(22.07, Rs, Rs * 0.01);
        double Rso = cc.Rso(data, now, 24);
        Assert.assertEquals(30.90, Rso, Rso * 0.01);
        double ea = cc.ea(data, now, 24);
        Assert.assertEquals(1.409, ea, ea * 0.01);
        double G = cc.G(data, now, 24);
        Assert.assertEquals(0, G, G * 0.02);
        double es = cc.es(data, now, 24);
        Assert.assertEquals(1.997, es, es * 0.01);
        double T = cc.T(data, now, 24);
        Assert.assertEquals(16.9, T, T * 0.01);
        double g = cc.g(data, now, 24);
        Assert.assertEquals(0.0666, g, g * 0.01);
        double D = cc.D(data, now, 24);
        Assert.assertEquals(0.122, D, D * 0.01);
        double Rnl = cc.Rnl(data, now, 24);
        Assert.assertEquals(3.71, Rnl, Rnl * 0.01);
        double Rn = cc.Rn(data, now, 24);
        Assert.assertEquals(13.28, Rn, Rn * 0.01);
        double value = cc.ETo(data, now, 24);
        Assert.assertEquals(3.9, value, value * 0.01);
    }

    @Test
    public void testETo1() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        Calendar c = GregorianCalendar.getInstance();
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);

        c.set(Calendar.DAY_OF_MONTH, 6);
        c.set(Calendar.MONTH, Calendar.JULY);
        c.set(Calendar.YEAR, 2012);

        Date now = c.getTime();

        source.setLatitude(50.8);
        source.setLongitude(145.05);
        source.setElevation(100.0);

        data.setMaximumHumidity(84.0);
        data.setMinimumHumidity(63.0);
        data.setMaximumTemperature(21.5);
        data.setMinimumTemperature(12.3);
        data.setSunshineHours(9.25);
        data.setWindSpeedHeight(10.0);
        data.setAverageWindSpeed(2.78);

        double Ra = cc.Ra(data, now, 24);
        Assert.assertEquals(41.09, Ra, Ra * 0.01);
        double Rs = cc.Rs(data, now, 24);
        Assert.assertEquals(22.07, Rs, Rs * 0.01);
        double Rso = cc.Rso(data, now, 24);
        Assert.assertEquals(30.90, Rso, Rso * 0.01);
        double ea = cc.ea(data, now, 24);
        Assert.assertEquals(1.409, ea, ea * 0.01);
        double Rnl = cc.Rnl(data, now, 24);
        Assert.assertEquals(3.71, Rnl, Rnl * 0.01);
        double Rn = cc.Rn(data, now, 24);
        Assert.assertEquals(13.28, Rn, Rn * 0.01);
        double G = cc.G(data, now, 24);
        Assert.assertEquals(0, G, G * 0.02);
        double es = cc.es(data, now, 24);
        Assert.assertEquals(1.997, es, es * 0.01);
        double T = cc.T(data, now, 24);
        Assert.assertEquals(16.9, T, T * 0.01);
        double g = cc.g(data, now, 24);
        Assert.assertEquals(0.0666, g, g * 0.01);
        double D = cc.D(data, now, 24);
        Assert.assertEquals(0.122, D, D * 0.01);
        double value = cc.ETo(data, now, 24);
        Assert.assertEquals(3.9, value, value * 0.01);
    }

    /*
     * @Test public void testETo2() { ClimateDataSource source = new ClimateDataSource(); DynamicWeatherStation sw = new BOMHistoricalClimateObserver(source);
     * ClimateCalculations cc = new ClimateCalculations(); Calendar c = GregorianCalendar.getInstance(); WritableClimateData data = (WritableClimateData)
     * ClimateDataFactory.newWritableClimateData(sw);
     *
     * c.set(Calendar.DAY_OF_MONTH, 6); c.set(Calendar.MONTH, Calendar.JULY); c.set(Calendar.YEAR, 2012);
     *
     * Date now = c.getTime();
     *
     * source.setLatitude(-50.8); source.setLongitude(145.05); source.setElevation(100.0);
     *
     * data.setMaximumHumidity(86.0); data.setMinimumHumidity(46.0); data.setMaximumTemperature(19.8); data.setMinimumTemperature(10.2); //
     * data.setSunshineHours(9.25); data.setSolarRadiation(10.11); data.setWindSpeedHeight(10.0); data.setAverageWindSpeed(2.78);
     *
     * double value = cc.ETo(data, now, 24); Assert.assertEquals(3.9, value, 0.2); }
     */
    @Test
    public void testEvapotranspration1() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 100);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.72);
        source.setLongitude(144.05);
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        double value = cc.calculateEvapotranspiration(station, now, 24);
        Assert.assertEquals(station.getClimateData(now).getEvapotranspiration(), value, 0.2);
    }

    @Test
    public void testEvapotranspration2() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 6);
        c.set(Calendar.MONTH, Calendar.FEBRUARY);
        c.set(Calendar.YEAR, 2013);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        double eto = station.getClimateData(now).getEvapotranspiration();
        double value = cc.calculateEvapotranspiration(station, now, 24);
        Assert.assertEquals(eto, value, eto * .1);
    }

    @Test
    public void testEvapotransprationByHour() {
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086068");
//		source.setLatitude(39.4);
//		source.setLongitude(146.98);
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        double eto = station.getClimateData(c.getTime()).getEvapotranspiration();
        ClimateCalculations cc = new ClimateCalculations();
        double etov = 0;
        for (int i = 1; i < 24; ++i) {
            c.set(Calendar.HOUR_OF_DAY, i);
            Date now = c.getTime();
            double value = cc.calculateEvapotranspiration(station, now, 1);
            //System.out.println(value + " - " + new SimpleDateFormat().format(now));
            etov += value;
        }
        System.out.println(eto + " - " + etov + " - " + cc.calculateEvapotranspiration(station, c.getTime(), 24));
        Assert.assertEquals(eto, etov, eto);
    }


    @Test
    public void testEvapotransprationToday() {
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        double eto = station.getClimateData(now).getEvapotranspiration();
        double value = cc.calculateEvapotranspiration(station, now, 24);
        Assert.assertEquals(eto, value, 0.1);
    }

    @Test
    public void testEvapotranspration3() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 300);
        c.set(Calendar.YEAR, 2012);
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.70);
        source.setLongitude(145.11);
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        double error = 0;
        int i = 0;
        for (i = 0; i < 100; ++i) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date now = c.getTime();
            double v1 = cc.calculateEvapotranspiration(station, now, 24);
            double v2 = station.getClimateData(now).getEvapotranspiration();
            try {
                double e = 1 - Math.min(v1, v2) / Math.max(v1, v2);
                //System.out.println(e + " - " + v2 + " : " + v1 + " - " + now);
                error += e;
                Assert.assertEquals(v2, v1, v1 * 0.25);
            } catch (Throwable e) {
            }
        }
        Assert.assertEquals(error / i, 0.01, 0.01);
    }

    @Test
    public void testEvapotranspration5() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 300);
        c.set(Calendar.YEAR, 2012);
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086068");
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        double error = 0;
        int i = 0;
        for (i = 0; i < 100; ++i) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date now = c.getTime();
            double v1 = cc.calculateEvapotranspiration(station, now, 24);
            double v2 = station.getClimateData(now).getEvapotranspiration();
            try {
                double e = 1 - Math.min(v1, v2) / Math.max(v1, v2);
                //System.out.println(e + " - " + v2 + " : " + v1 + " - " + now);
                error += e;
                Assert.assertEquals(v2, v1, v1 * 0.25);
            } catch (Throwable e) {
            }
        }
        Assert.assertEquals(error / i, 0.01, 0.01);
    }

    @Test
    public void testEvapotranspration4() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        // source.setId("086351");
        // source.setId("086038");
        // source.setLatitude(37.84);
        // source.setLongitude(144.98);
        source.setLatitude(-38.02);
        source.setLongitude(144.05);
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        // ClimateObserver station = new BOMHistoricalClimateObserver(source);
        // ClimateObserver station = BOMClimateObserverCatalogue.buildClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -3);
        double error = 0;
        int i = 0;
        for (; i < 100; ++i) {
            try {
                c.add(Calendar.DAY_OF_YEAR, -1);
                Date now = c.getTime();
                double v2 = station.getClimateData(now).getEvapotranspiration();
                double v1 = cc.calculateEvapotranspiration(station, now, 24);
                double e = 1 - Math.min(v1, v2) / Math.max(v1, v2);
                //System.out.println("Error: "+e + " - BOM ETo: " + v2 + " - Calc ETo: " + v1 + " : " + new SimpleDateFormat().format(now));
                error += e;
                // Assert.assertEquals(v2, v1, v1*0.25);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        //System.out.println(error / i);
        Assert.assertEquals(0.01, error / i, 0.02);
    }

    @Test
    public void testRa() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 246);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-20.0);
        source.setLongitude(145.05);
        ClimateCalculations cc = new ClimateCalculations();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        ClimateData data = ClimateDataFactory.newWritableClimateData(sw,source);
        double value = cc.Ra(data, now, 24);
        Assert.assertEquals(32.2, value, 0.1);
    }

    @Test
    public void testRs() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 135);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-22.9);
        source.setLongitude(145.05);
        ClimateCalculations cc = new ClimateCalculations();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);
        double Ra = cc.Ra(data, now, 24);
        Assert.assertEquals(25.1, Ra, 0.1);
        data.setSunshineHours(7.1);
        double Rs = cc.Rs(data, now, 24);
        Assert.assertEquals(14.5, Rs, 0.1);

    }

    @Test
    public void testRnl() {
        Calendar c = GregorianCalendar.getInstance();
        ClimateCalculations cc = new ClimateCalculations();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);
        c.set(Calendar.DAY_OF_YEAR, 135);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();

        source.setLatitude(-22.9);
        source.setLongitude(145.05);
        source.setElevation(0d);

        data.setMaximumHumidity(78d);
        data.setMinimumHumidity(78d);
        data.setMaximumTemperature(25.1);
        data.setMinimumTemperature(19.1);
        data.setSunshineHours(7.1);

        double Ra = cc.Ra(data, now, 24);
        Assert.assertEquals(25.1, Ra, 0.1);
        double Rs = cc.Rs(data, now, 24);
        Assert.assertEquals(14.5, Rs, 0.1);
        double Rso = cc.Rso(data, now, 24);
        Assert.assertEquals(18.8, Rso, 0.1);
        double ea = cc.ea(data, now, 24);
        Assert.assertEquals(2.1, ea, 0.1);
        double Rnl = cc.Rnl(data, now, 24);
        Assert.assertEquals(3.5, Rnl, 0.1);

    }

    @Test
    public void testRn() {
        Calendar c = GregorianCalendar.getInstance();
        ClimateCalculations cc = new ClimateCalculations();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);
        c.set(Calendar.DAY_OF_YEAR, 135);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();

        source.setLatitude(-22.9);
        source.setLongitude(145.05);
        source.setElevation(0d);

        data.setMaximumHumidity(78d);
        data.setMinimumHumidity(78d);
        data.setMaximumTemperature(25.1);
        data.setMinimumTemperature(19.1);
        data.setSunshineHours(7.1);

        double Rn = cc.Rn(data, now, 24);
        Assert.assertEquals(7.6, Rn, 0.1);

    }

    @Test
    public void testeaHumidity() {
        ClimateCalculations cc = new ClimateCalculations();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);
        Calendar c = GregorianCalendar.getInstance();

        c.set(Calendar.DAY_OF_YEAR, 135);
        c.set(Calendar.YEAR, 2012);
        Date now = c.getTime();

        data.setMaximumHumidity(82.0);
        data.setMaximumTemperature(25.0);
        data.setMinimumHumidity(54.0);
        data.setMinimumTemperature(18.0);
        double ea = cc.ea(data, now, 24);
        Assert.assertEquals(1.78f, ea, 0.1);

    }

    @Test
    public void testu2() {
        ClimateCalculations cc = new ClimateCalculations();
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        ClimateDataFactory sw = new BOMHistoricalClimateObserver(source);
        WritableClimateData data = (WritableClimateData) ClimateDataFactory.newWritableClimateData(sw,source);

        data.setWindSpeedHeight(10d);
        data.setAverageWindSpeed(3.2);
        double ws = cc.u2(data, new Date(), 24);
        Assert.assertEquals(2.4, ws, 0.1);

    }


    public void testRsToRadiation() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 100);
        c.set(Calendar.YEAR, 2012);
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086038");
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        ClimateObserver station1 = new BOMHistoricalClimateObserver(source);
        ClimateObserver station2 = new BOMSimpleHistoricalClimateObserver(source);
        ClimateObserver station3 = new BOMStatisticalClimateDataObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        double error = 0;
        for (int i = 0; i < 100; ++i) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date now = c.getTime();
            WritableClimateData data1 = (WritableClimateData) station1.getClimateData(now);
            WritableClimateData data2 = (WritableClimateData) station2.getClimateData(now);
            WritableClimateData data3 = (WritableClimateData) station3.getClimateData(now);
            double rad = data1.getSolarRadiation();
            data1.setSolarRadiation(null);
            data1.setSunshineHours(data2.getSunshineHours());
            double Rs1 = cc.Rs(data1, now, 24);
            data1.setSolarRadiation(data3.getSolarRadiation());
            double Rs2 = cc.Rs(data1, now, 24);
            try {
                double e = 1 - Math.min(rad, Rs2) / Math.max(rad, Rs2);
                error += e;
                System.out.println(e + " - " + rad + " : " + Rs1 + " : " + Rs2);
            } catch (Throwable e) {
            }
        }
        System.out.println(error / 100);
    }

    /*
        @Test
        public void testEvapRadRatio() {
            Calendar c = GregorianCalendar.getInstance();
            c.set(Calendar.DAY_OF_YEAR, 100);
            c.set(Calendar.YEAR, 2012);
            ClimateDataSource source = ClimateDataSourceFactory.newInstance();
            source.setId("086038");
            // source.setLatitude(37.84);
            // source.setLongitude(144.98);
            ClimateObserver station = BOMClimateObserverCatalogue.buildClimateObserver(source);
            ClimateCalculations cc = new ClimateCalculations();
            double error = 0;
            for (int i = 0; i < 100; ++i) {
                c.add(Calendar.DAY_OF_YEAR, 1);
                Date now = c.getTime();
                ClimateData data = station.newWritableClimateData(now);
                double v1 = data.getEvaporation();
                double v2 = data.getSolarRadiation();
                double v3 = data.getMaximumHumidity();
                try {
                    double e = v1 / v2;
                    System.out.println(e + " - " + v2 + " : " + v1 + " : " + v3 + " - " + now + " - " + data.getDataSource().getName());
                    error += e;
                } catch (Throwable e) {
                }
            }
            System.out.println(error / 100);
        }
    */
    @Test
    public void dewPointTest() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setId("086068");
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateCalculations cc = new ClimateCalculations();
        ClimateData data = station.getClimateData(new Date());
        ClimateObservation obs = data.getObservation(new Date(), ClimateData.ObservationMatch.CLOSEST);
        while (obs != null) {
            Assert.assertEquals(obs.getHumidity(), cc.eaDew(obs) / ClimateCalculations.getSaturationVapourPressure(obs.getTemperature()) * 100, 2);
            obs = obs.getPreviousObservation();
        }
    }
    /*
     * @Test public void testEvaporationToRadiation() { ClimateDataSource source = ClimateDataSourceFactory.newInstance(); source.setId("086351"); ClimateObserver
	 * station = BOMClimateObserverCatalogue.buildClimateObserver(source); ClimateCalculations cc = new ClimateCalculations(); ClimateData data =
	 * station.newWritableClimateData(new Date()); Assert.assertEquals(ClimateCalculations.getSolarRadiationFromSunlightHours(data, now,
	 * durationLength)(data.getEvaporation()), data.getSolarRadiation(), 0.1); }
	 */

}
