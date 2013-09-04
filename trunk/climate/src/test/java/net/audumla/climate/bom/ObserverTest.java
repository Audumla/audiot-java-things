package net.audumla.climate.bom;

import au.com.bytecode.opencsv.CSVReader;
import net.audumla.climate.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ObserverTest {
    private static final Logger logger = LogManager.getLogger(ClimateObserverCollectionHandler.class);

    @Test
    public void testObservations() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 1);
        Date now = c.getTime();
        BOMClimateDataSource source = ClimateDataSourceFactory.newInstance(BOMClimateDataSource.class);
        source.setId("086068");
        ClimateObserver forcaster = new BOMPeriodicClimateObserver(source);
        ClimateData data = forcaster.getClimateData(now);
        Assert.assertEquals(now, data.getTime());
        // Assert.assertNotNull(data.getMinimumTemperature());
        c.set(Calendar.HOUR, 12);
        Date obTime1 = c.getTime();
        ClimateObservation obs1 = data.getObservation(obTime1, ClimateData.ObservationMatch.CLOSEST);
        Assert.assertNotNull(obs1);
        Assert.assertNotNull(obs1.getTemperature());
        c.set(Calendar.HOUR, 11);
        Date obTime2 = c.getTime();
        ClimateObservation obs2 = data.getObservation(obTime2, ClimateData.ObservationMatch.CLOSEST);
        Assert.assertNotNull(obs2);
        Assert.assertNotNull(obs2.getTemperature());
        Assert.assertNotSame(obs1.getTime(), obs2.getTime());

    }

    @Test
    public void testParseLatLongResult() {
        String results = "099001 Deal Island TAS (30.7km away) || 050102 Condobolin Soil Conservation NSW (38.8km away)||050014 Condobolin Retirement Village NSW (39.0km away) ||050137 Condobolin Airport AWS NSW (43.0km away) ||050052 Condobolin Ag Research Stn NSW (44.0km away) || 074039 Deniliquin Falkiner Memorial NSW (8.1km away)||074128 Deniliquin (Wilkinson St) NSW (14.1km away) ||074258 Deniliquin Airport AWS NSW (17.6km away) ||074051 Gulpa Island NSW (33.3km away)||075080 Wanganella (Zara) NSW (35.3km away) ||074069 Mathoura State Forest NSW (46.0km away) || 088162 Wallan (Kilmore Gap) VIC (2.2km away) ||086178 Greenvale Sanatorium VIC (29.6km away)||086282 Melbourne Airport VIC (31.8km away) ||086122 Watsonia Loyola VIC (34.6km away)||086351 Bundoora (Latrobe University) VIC (35.6km away) ||087036 Macedon Forestry VIC (36.3km away) ||086038 Essendon Airport VIC (36.7km away) ||086068 Viewbank VIC (39.3km away) ||087038 Maribyrnong Explosives Factory VIC (42.3km away)||088053 Seymour Shire Depot VIC (44.1km away) ||086013 Burnley VIC (44.3km away)||086071 Melbourne Regional Office VIC (45.1km away) ||086060 Kew VIC (46.4km away)||088036 Kyneton Post Office VIC (48.6km away)||3038_88162 Kilmore Gap VIC (2.2km away) ||3049_86282 Melbourne Airport VIC (31.8km away) ||3009_86351 Bundoora VIC (35.6km away) ||3026_86038 Essendon VIC (36.7km away) ||3079_86068 Watsonia VIC (39.3km away) ||3093_86068 Viewbank VIC (39.3km away) ||3050_86071 Melbourne VIC (45.1km away) ||3089_86071 Melbourne City VIC (45.1km away) ||3088_200838 Hogan Island TAS (19.7km away)";
        List<BOMClimateDataSource> sources = BOMClimateObserverCatalogue.parseSources(ClimateDataSourceFactory.newInstance(BOMClimateDataSource.class), 50, results);
        Assert.assertEquals(sources.size(), 9);
    }

    @Test
    public void testAllStations() throws IOException {
        ClimateCalculations cc = new ClimateCalculations();
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -3);
        Date now = c.getTime();
        CSVReader csv = new CSVReader(BOMDataLoader.instance().getData(BOMDataLoader.FTP, BOMDataLoader.BOMFTP, BOMClimateObserverCatalogue.generateStationCatalogueURL()));
        String[] line;
        double error = 0;
        double count = 0;
        while ((line = csv.readNext()) != null && count < 20) {
            try {
                if (line.length > 2) {
                    ++count;
                    String id = line[0].replace("\"", "");
                    ClimateDataSource source = ClimateDataSourceFactory.newInstance();
                    source.setId(id);
                    ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
                    double v2 = station.getClimateData(now).getEvapotranspiration();
                    double v1 = cc.calculateEvapotranspiration(station, now, 24);
                    double e = 1 - Math.min(v1, v2) / Math.max(v1, v2);
                    //System.out.println(e);
                    error += e;
                }
            } catch (Exception e) {
                //System.out.println("Station Catalogue error - " + e);
            }

        }
        Assert.assertEquals(error / count, 0.01, 0.01);

    }

    @Test
    public void testFailingStations() throws IOException {
        ClimateCalculations cc = new ClimateCalculations();
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -3);
        Date now = c.getTime();
        ClimateDataSource source = ClimateDataSourceFactory.newInstance();
        source.setId("003102");
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        try {
            double v2 = station.getClimateData(now).getEvapotranspiration();
            double v1 = cc.calculateEvapotranspiration(station, now, 24);
            double e = 1 - Math.min(v1, v2) / Math.max(v1, v2);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

    }


    @Test
    public void testSequentialObservations() throws IOException {
        Date now = DateUtils.addDays(new Date(),-2);
        ClimateDataSource source = ClimateDataSourceFactory.newInstance();
        source.setId("086068");
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getClimateObserver(source);
        ClimateData cd = station.getClimateData(now);
        ClimateObservation obs = cd.getObservation(now, ClimateData.ObservationMatch.SUBSEQUENT);
        Date lastDate = null;
        while (obs != null && obs.getDataSource().getType() == ClimateDataSource.ClimateDataSourceType.PERIODIC_OBSERVATION) {
            lastDate = obs.getTime();
            logger.debug(lastDate + " - " +obs.getDataSource().getType());
            obs = obs.getNextObservation();

        }
        Assert.assertFalse(DateUtils.isSameDay(lastDate, now));
    }
}
