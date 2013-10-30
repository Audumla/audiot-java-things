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

import au.com.bytecode.opencsv.CSVReader;
import net.audumla.bean.SupportedFunction;
import net.audumla.climate.*;
import net.audumla.bean.SafeParse;
import net.audumla.Time;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BOMSimpleHistoricalClimateObserver implements ClimateDataFactory, ClimateObserver {
    private static final Logger LOG = Logger.getLogger(BOMSimpleHistoricalClimateObserver.class);
    private final int MAX_ENTRIES = 100;
    private SimpleDateFormat historyFormatter = new SimpleDateFormat("yyyyMM");
    private SimpleDateFormat historyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private Pattern historyURLPattern = Pattern.compile(".*URL=/([^\\.]*)");
    private static final String BOMHistoryURL = "jsp/ncc/cdio/weatherData/av?p_nccObsCode=201&p_display_type=dwo&p_startYear=&p_c=&p_stn_num=";
    private LinkedHashMap<Date, ClimateData> historicalData;
    private ClimateDataSource source;
    private Set<Date> invalidMonths = new LinkedHashSet<Date>();

    @SuppressWarnings("serial")
    public BOMSimpleHistoricalClimateObserver(ClimateDataSource source) {
        this.source = source;
        this.source.setType(ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);
        historicalData = new LinkedHashMap<Date, ClimateData>(MAX_ENTRIES + 1, .75F, true) {
            // This method is called just after a new entry has been added
            public boolean removeEldestEntry(Map.Entry<Date, ClimateData> eldest) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    synchronized public ClimateData getClimateData(Date date) {
        if (!invalidMonths.contains(Time.getMonthAndYear(date))) {
            try {
                Date simpleDate = historyDateFormatter.parse(historyDateFormatter.format(date));
                ClimateData data = historicalData.get(simpleDate);
                if (data == null) {
                    String url = getHistoryURL(date);
                    if (url.length() > 0) {
                        loadHistoricalData(url);
                        data = historicalData.get(simpleDate);
                    } else {
                        throw new UnsupportedOperationException("DAILY_OBSERVATION data not available for station " + source.getId());
                    }/*
                    if (data == null) {
						LOG.error("Cannot load historical data for station " + source.getId() + " at date " + date.toString());
					}
					*/
                }
                return data;
            } catch (ParseException ignored) {
            } catch (UnsupportedOperationException ex) {
                invalidMonths.add(Time.getMonthAndYear(date));
            }
        }
        return null;
    }

    private void loadHistoricalData(String url) {
        CSVReader reader = new CSVReader(BOMDataLoader.instance().getData(BOMDataLoader.HTTP, BOMDataLoader.BOMHTTP, url));
        // ArrayList<HistoricalData> history = new ArrayList<HistoricalData>();
        String[] data;
        boolean pastHeader = false;
        try {
            while ((data = reader.readNext()) != null) {
                if (pastHeader) {
                    // [, JulianDate, Minimum temperature (�C), Maximum temperature (�C), Rainfall (mm), Evaporation (mm),
                    // Sunshine (hours), Direction of maximum wind gust , Speed of maximum wind gust (km/h), Time of
                    // maximum wind gust, 9am Temperature (�C), 9am relative humidity (%), 9am cloud amount (oktas), 9am
                    // wind direction, 9am wind speed (km/h), 9am MSL pressure (hPa), 3pm Temperature (�C), 3pm relative
                    // humidity (%), 3pm cloud amount (oktas), 3pm wind direction, 3pm wind speed (km/h), 3pm MSL
                    // pressure (hPa)]
                    try {
                        Date date = historyDateFormatter.parse(data[1]);
                        Date dateM1 = DateUtils.addDays(date, -1);
                        WritableClimateData cdNow = ClimateDataFactory.convertToWritableClimateData(historicalData.get(date));
                        if (cdNow == null) {
                            cdNow = ClimateDataFactory.newWritableClimateData(this, getSource()); // now
                            cdNow.setTime(date);
                            historicalData.put(date, ClimateDataFactory.convertToReadOnlyClimateData(cdNow));
                        }
                        WritableClimateData cdNowM1 = ClimateDataFactory.convertToWritableClimateData(historicalData.get(dateM1));
                        if (cdNowM1 == null) {
                            cdNowM1 = ClimateDataFactory.newWritableClimateData(this, getSource()); // now
                            cdNowM1.setTime(dateM1);
                            historicalData.put(dateM1, ClimateDataFactory.convertToReadOnlyClimateData(cdNowM1));
                        }

                        cdNow.setTime(date); // 2013-02-1
                        try {
                            cdNow.setMinimumTemperature(SafeParse.parseDouble(data[2]));
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            cdNow.setMaximumTemperature(SafeParse.parseDouble(data[3]));
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            Double rain = SafeParse.parseDouble(data[4]);
                            if (rain != null) {
                                cdNowM1.setRainfall(rain);
                                cdNowM1.setRainfallProbability(cdNowM1.getRainfall() > 0 ? 100d : 0d);
                            }
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            cdNow.setEvaporation(SafeParse.parseDouble(data[5]));
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            cdNow.setSunshineHours(SafeParse.parseDouble(data[6]));
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        WritableClimateObservation obs9 = ClimateDataFactory.newWritableClimateObservation(this, getSource());
                        WritableClimateObservation obs15 = ClimateDataFactory.newWritableClimateObservation(this, getSource());
                        obs9.setTime(DateUtils.setHours(date, 9));
                        obs15.setTime(DateUtils.setHours(date, 15));
                        int count = 0;
                        try {
                            obs9.setTemperature(SafeParse.parseDouble(data[10]));
                            ++count;
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            obs9.setHumidity(SafeParse.parseDouble(data[11]));
                            ++count;
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            obs9.setWindSpeed(SafeParse.parseDouble(data[14]));
                            obs9.setWindDirection(data[13]);
                            obs9.setWindSpeedHeight(10.0);

                            ++count;
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        if (count > 0) {
                            cdNow.addObservation(obs9);
                        }
                        count = 0;
                        try {
                            obs15.setTemperature(SafeParse.parseDouble(data[16]));
                            ++count;
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            obs15.setHumidity(SafeParse.parseDouble(data[17]));
                            ++count;
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        try {
                            obs15.setWindSpeed(SafeParse.parseDouble(data[20]));
                            obs15.setWindSpeedHeight(10.0);
                            obs15.setWindDirection(data[19]);
                            ++count;
                        } catch (Exception e) {
                            LOG.debug("Error setting historical field", e);
                        }
                        if (count > 0) {
                            cdNow.addObservation(obs15);
                        }
                    } catch (ParseException e) {
                        LOG.error("Unable to parse date for historical record [" + getSource().toString() + "] - " + Arrays.toString(data), e);
                    }
                } else {
                    if (data.length > 1 && data[1].contains("JulianDate")) {
                        pastHeader = true;
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Unable to load DAILY_OBSERVATION Data [" + getSource().toString() + "]", e);
        }
    }

    private String getHistoryURL(Date date) {
        String url = "";
        try {
            BufferedReader rd = BOMDataLoader.instance().getData(BOMDataLoader.HTTP, BOMDataLoader.BOMHTTP, BOMHistoryURL + source.getId());
            Matcher m = historyURLPattern.matcher(IOUtils.toString(rd));
            if (m.find()) {
                String dirs[] = m.group(1).split("/");
                for (int i = 0; i < dirs.length - 1; ++i) {
                    url += dirs[i] + "/";
                }
                String ym = historyFormatter.format(date);
                url += ym + "/text/";
                url += dirs[dirs.length - 1] + "." + ym + "." + "csv";
            }
        } catch (Exception e) {
            LOG.error("Error reading historical data", e);
        }
        return url;
    }

    public Class<? extends ClimateData> getClimateDataClass() {
        return BOMClimateHistoryData.class;
    }

    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return BOMClimateHistoryObservation.class;
    }

    public ClimateDataSource getSource() {
        return source;
    }

    public boolean supportsDate(Date date) {
        return !invalidMonths.contains(Time.getMonthAndYear(date)) && (date.before(Time.getToday()) || DateUtils.isSameDay(date, Time.getToday()));
    }

    private static interface BOMClimateHistoryObservation extends ClimateObservation {

        @SupportedFunction(supported = true)
        double getWetBulbTemperature();

        @SupportedFunction(supported = true)
        double getTemperature();

        @SupportedFunction(supported = true)
        double getAtmosphericPressure();

        @SupportedFunction(supported = true)
        double getVapourPressure();

        @SupportedFunction(supported = true)
        double getSaturationVapourPressure();

        @SupportedFunction(supported = true)
        double getApparentTemperature();

        @SupportedFunction(supported = true)
        double getHumidity();

        @SupportedFunction(supported = true)
        double getDewPoint();

        @SupportedFunction(supported = true)
        double getWindSpeed();

        @SupportedFunction(supported = true)
        double getWindSpeedHeight();

        @SupportedFunction(supported = true)
        String getWindDirection();

    }

    private static interface BOMClimateHistoryData extends ClimateData {
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
        double getMaximumSaturationVapourPressure(); //kPA

        @SupportedFunction(supported = true)
        double getMinimumSaturationVapourPressure(); //kPA

        @SupportedFunction(supported = true)
        double getMaximumVapourPressure();

        @SupportedFunction(supported = true)
        double getMinimumVapourPressure();

        @SupportedFunction(supported = true)
        double getAverageWindSpeed();

        @SupportedFunction(supported = true)
        double getWindSpeedHeight();

        @SupportedFunction(supported = true)
        NavigableSet<ClimateObservation> getObservations();

        @SupportedFunction(supported = true)
        ClimateObservation getObservation(Date time, ObservationMatch match);

        @SupportedFunction(supported = true)
        double getAtmosphericPressure();

        @SupportedFunction(supported = true)
        double getDaylightHours();


    }
}
