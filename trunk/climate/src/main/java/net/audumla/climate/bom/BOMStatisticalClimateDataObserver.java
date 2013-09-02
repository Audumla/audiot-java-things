package net.audumla.climate.bom;

import au.com.bytecode.opencsv.CSVReader;
import net.audumla.bean.SupportedFunction;
import net.audumla.climate.*;
import net.audumla.util.SafeParse;
import net.audumla.util.Time;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.NavigableSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BOMStatisticalClimateDataObserver implements ClimateDataFactory, ClimateObserver {
    private static final Logger logger = LogManager.getLogger(BOMStatisticalClimateDataObserver.class);
    private static final Pattern numberPattern = Pattern.compile("(\\d+\\.?\\d*)");
    private static final Pattern wordPattern = Pattern.compile("\\w*\\s*:\\s*(\\w*)");
    private static final String BOMStatisticsURL = "clim_data/cdio/tables/text/";
    private String[] BOMStatisitcsFilePrefix = {"IDCJCM0035_", "IDCJCM0033_"};
    private LinkedHashMap<Date, WritableClimateData> statData = new LinkedHashMap<Date, WritableClimateData>();
    private ClimateDataSource source;
    private boolean active = true;

    public BOMStatisticalClimateDataObserver(ClimateDataSource source) {
        this.source = source;
        this.source.setType(ClimateDataSource.ClimateDataSourceType.MONTHLY_STATISTICAL);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(0));
        c.set(Calendar.MONTH, Calendar.JANUARY);
        for (int i = 0; i < 12; ++i) {
            WritableClimateData cd = ClimateDataFactory.newWritableClimateData(this,getSource());
            cd.setTime(c.getTime());
            statData.put(c.getTime(), cd);
            c.add(Calendar.MONTH, 1);
        }
        if (source.getId() != null && source.getId().length() > 0) {
            loadHistoricalData();
        }
        logger.debug("Initialized BOM MONTHLY STATISTICAL Observer[" + source.toString() + "]");
    }

    private WritableClimateObservation getObservation(WritableClimateData bomdata, int hour) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(Calendar.HOUR_OF_DAY, hour);
        Date requiredTime = c.getTime();
        WritableClimateObservation obs = null;
        try {
            obs = ClimateDataFactory.convertToWritableClimateObservation(bomdata.getObservation(requiredTime, ClimateData.ObservationMatch.CLOSEST));
            if (obs != null && DateUtils.getFragmentInHours(obs.getTime(),Calendar.DAY_OF_YEAR) == hour) {
                return obs;
            }
        } catch (Exception ignored) {
            logger.error(ignored);
            ignored.printStackTrace();
        }
        obs = ClimateDataFactory.newWritableClimateObservation(this,getSource());
        obs.setTime(requiredTime);
        bomdata.addObservation(obs);
        return obs;
    }

    private void loadHistoricalData() {
        for (String prefix : BOMStatisitcsFilePrefix) {
            try {
                CSVReader reader = new CSVReader(BOMDataLoader.instance().getData(BOMDataLoader.HTTP, BOMDataLoader.BOMHTTP, BOMStatisticsURL + prefix + source.getId() + ".csv"));
                loadHistoricalData(reader);
                return;
            } catch (Exception ignored) {
            }
        }
        active = false;
        //logger.error("Cannot load statistic data for station - " + getSource().getId());

    }

    private void loadHistoricalData(CSVReader reader) {
        // http://www.bom.gov.au/clim_data/cdio/tables/text/IDCJCM0035_086351.csv
        try {
            String[] data;
            while ((data = reader.readNext()) != null) {
                if (data.length > 0) {
                    if (data[0].contains("Elevation")) {
                        Matcher m = numberPattern.matcher(data[0]);
                        if (m.find()) {
                            source.setElevation(SafeParse.parseDouble(m.group(1)));
                        }
                        continue;
                    }
                    if (data[0].contains("Latitude")) {
                        Matcher m = numberPattern.matcher(data[0]);
                        if (m.find()) {
                            source.setLatitude(-1 * SafeParse.parseDouble(m.group(1)));
                        }
                        continue;
                    }
                    if (data[0].contains("Longitude")) {
                        Matcher m = numberPattern.matcher(data[0]);
                        if (m.find()) {
                            source.setLongitude(SafeParse.parseDouble(m.group(1)));
                        }
                        continue;
                    }
                    if (data[0].contains("Commenced")) {
                        Matcher m = numberPattern.matcher(data[0]);
                        if (m.find()) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(Time.getZeroDate());
                            c.set(Calendar.YEAR, SafeParse.parseInteger(m.group(1)));
                            source.setFirstRecord(c.getTime());
                        }
                        continue;
                    }
                    if (data[0].contains("Last Record")) {
                        Matcher m = numberPattern.matcher(data[0]);
                        if (m.find()) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(Time.getZeroDate());
                            c.set(Calendar.YEAR, SafeParse.parseInteger(m.group(1)));
                            c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
                            source.setLastRecord(c.getTime());
                        }
                        continue;
                    }
                    if (data[0].contains("State")) {
                        Matcher m = wordPattern.matcher(data[0]);
                        if (m.find()) {
                            source.setState(m.group(1));
                        }
                        continue;
                    }
                    if (data[0].contains("Mean maximum temperature")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            bomdata.setMaximumTemperature(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean minimum temperature")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            bomdata.setMinimumTemperature(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean rainfall")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            bomdata.setAvgRainfall(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean number of days of rain >= 1 mm")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            bomdata.setRainfallDaysAbove1mm(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean daily sunshine")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            bomdata.setSunshineHours(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean daily solar exposure")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            bomdata.setSolarRadiation(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean daily evaporation")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            bomdata.setEvaporation(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean daily wind run")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            Double ws = SafeParse.parseDouble(data[i]);
                            if (ws != null) {
                                bomdata.setAverageWindSpeed(ws / 24);
                                bomdata.setWindSpeedHeight(10.0);
                            }
                            ++i;
                        }
                        continue;
                    }

					/*
                     * Mean 3pm temperature (Degrees C) for years 1979 to 2002 Mean 3pm dew point temperature (Degrees C) for years 1979 to 2002 Mean 3pm
					 * relative humidity (%) for years 1979 to 2002 Mean 3pm wind speed (km/h) for years 1979 to 2002
					 */
                    if (data[0].contains("Mean 3pm temperature")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 15);
                            obs.setTemperature(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean 3pm dew point temperature")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 15);
                            obs.setDewPoint(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean 3pm relative humidity")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 15);
                            obs.setHumidity(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean 3pm wind speed")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 15);
                            obs.setWindSpeed(SafeParse.parseDouble(data[i]));
                            obs.setWindSpeedHeight(10.0);
                            ++i;
                        }
                        continue;
                    }

                    if (data[0].contains("Mean 9am temperature")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 9);
                            obs.setTemperature(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean 9am dew point temperature")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 9);
                            obs.setDewPoint(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean 9am relative humidity")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 9);
                            obs.setHumidity(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                    if (data[0].contains("Mean 9am wind speed")) {
                        int i = 1;
                        for (WritableClimateData statdata : statData.values()) {
                            BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                            WritableClimateObservation obs = getObservation(bomdata, 9);
                            obs.setWindSpeed(SafeParse.parseDouble(data[i]));
                            ++i;
                        }
                        continue;
                    }
                }
            }
            for (WritableClimateData statdata : statData.values()) {
                BOMClimateStatisticalData bomdata = (BOMClimateStatisticalData) statdata;
                Calendar c = Calendar.getInstance();
                c.setTime(bomdata.getTime());
                int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                bomdata.setRainfall(bomdata.getAvgRainfall() / bomdata.getRainfallDaysAbove1mm());
                bomdata.setRainfallProbability((bomdata.getRainfallDaysAbove1mm() / daysInMonth) * 100);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            // logger.error("Error reading statistic data", e);
            logger.error("Error loading statistic data for station - " + getSource().getId() + " : " + e.getMessage());
        }
    }

    synchronized public ClimateData getClimateData(Date date) {
        return statData.get(Time.getMonth(date));
    }

    public Class<? extends ClimateData> getClimateDataClass() {
        return BOMClimateStatisticalData.class;
    }

    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return BOMClimateStatisticalObservation.class;
    }

    public ClimateDataSource getSource() {
        return source;
    }

    public boolean supportsDate(Date date) {
        return true;
    }

    protected static interface BOMClimateStatisticalObservation extends WritableClimateObservation {
        @SupportedFunction(supported = true)
        double getTemperature();

        @SupportedFunction(supported = true)
        double getWetBulbTemperature();

        @SupportedFunction(supported = true)
        double getAtmosphericPressure();

        @SupportedFunction(supported = true)
        double getVapourPressure();

        @SupportedFunction(supported = true)
        double getSaturationVapourPressure();

        @SupportedFunction(supported = true)
        double getHumidity();

        @SupportedFunction(supported = true)
        double getDewPoint();

        @SupportedFunction(supported = true)
        double getWindSpeed();

        @SupportedFunction(supported = true)
        double getWindSpeedHeight();
    }

    protected static interface BOMClimateStatisticalData extends WritableClimateData {
        @SupportedFunction(supported = true)
        double getMinimumTemperature();

        @SupportedFunction(supported = true)
        double getMaximumTemperature();

        @SupportedFunction(supported = true)
        double getRainfall();

        @SupportedFunction(supported = true)
        double getRainfallProbability();

        @SupportedFunction(supported = true)
        double getSunshineHours();

        @SupportedFunction(supported = true)
        double getEvaporation();

        @SupportedFunction(supported = true)
        double getAverageWindSpeed();

        @SupportedFunction(supported = true)
        double getSolarRadiation();

        @SupportedFunction(supported = true)
        double getWindSpeedHeight();

        @SupportedFunction(supported = true)
        ClimateObservation getObservation(Date time, ObservationMatch match);

        @SupportedFunction(supported = true)
        NavigableSet<ClimateObservation> getObservations();

        @SupportedFunction(supported = true)
        double getAtmosphericPressure();

        @SupportedFunction(supported = true)
        double getDaylightHours();

        @SupportedFunction(supported = true)
        double getAvgRainfall();

        @SupportedFunction(supported = true)
        void setAvgRainfall(double r);

        @SupportedFunction(supported = true)
        double getRainfallDaysAbove1mm();

        @SupportedFunction(supported = true)
        void setRainfallDaysAbove1mm(double r);
    }
}
