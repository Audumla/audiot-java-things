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

import net.audumla.bean.SupportedFunction;
import net.audumla.climate.*;
import net.audumla.bean.SafeParse;
import net.audumla.spacetime.Time;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BOMHistoricalClimateObserver implements ClimateDataFactory, ClimateObserver {
    private static final Logger logger = Logger.getLogger(BOMHistoricalClimateObserver.class);
    private static final String indexFile = "stations_db.txt";
    private static final String productID = "IDCKWCDEA0";
    private static final String subDir = BOMDataLoader.CLIMATE + productID + "/tables/";
    private static final Pattern indexPattern = Pattern.compile("(.{6})\\s*(.{3})\\s*(.{4})\\s*((?:\\D*[' '])*)\\s*(.{12})\\s*(.{8})\\s*(.{8})");
    private static final SimpleDateFormat yearMonth = new SimpleDateFormat("yyyyMM");// 201301
    private static final SimpleDateFormat yearMonthDay = new SimpleDateFormat("dd/MM/yyyy");// 12/01/2013
    private static final int MAX_ENTRIES = 100;
    private Set<Date> invalidMonths = new LinkedHashSet<Date>();
    //    boolean active = true;
    private ClimateDataSource source;
    private String lastLoadedFile = null;
    private LinkedHashMap<Date, ClimateData> historicalData;
    // private String stationDataFilename = null;
    private String stationDataSubDir = null;
    private String stationName = null;

    @SuppressWarnings("serial")
    public BOMHistoricalClimateObserver(ClimateDataSource source) {
        this.source = source;
        this.source.setType(ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);
        historicalData = new LinkedHashMap<Date, ClimateData>(MAX_ENTRIES + 1, .75F, true) {
            // This method is called just after a new entry has been added
            public boolean removeEldestEntry(Map.Entry<Date, ClimateData> eldest) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    protected boolean loadStationDataFile(String stationSubDir, String fileName, String stationName) {
        try {
            String fn = stationSubDir + fileName;
            if (!fn.equals(lastLoadedFile)) {
                BufferedReader br = BOMDataLoader.instance().getData(BOMDataLoader.FTP, BOMDataLoader.BOMFTP, BOMDataLoader.BOMBaseFTPDir + fn);
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        String[] data = line.split(",");
                        if (data[0].toLowerCase().equals(stationName)) {
                            // Station Name,Date,EvapoTranspiration,Rain,Pan Evaporation,Maximum Temp,Minimum Temp,Max
                            // Relative Hum,Min Relative Hum,Avg 10m Wind Sp,Solar Radiation
                            // VIEWBANK,12/01/2013,4.5,0.0, ,25.2,15.7,77,43,3.53,16.10
                            Date date = yearMonthDay.parse(data[1].trim());
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

                            try {
                                cdNow.setEvapotranspiration(SafeParse.parseDouble(data[2].trim()));
                            } catch (Exception ignored) {
                            }
                            try {
                                Double rain = SafeParse.parseDouble(data[3]);
                                if (rain != null) {
                                    cdNowM1.setRainfall(rain);
                                    cdNowM1.setRainfallProbability(cdNowM1.getRainfall() > 0 ? 100d : 0d);
                                }
                            } catch (Exception ignored) {
                            }
                            try {
                                cdNow.setEvaporation(SafeParse.parseDouble(data[4].trim()));
                            } catch (Exception ignored) {
                            }
                            try {
                                cdNow.setMaximumTemperature(SafeParse.parseDouble(data[5].trim()));
                            } catch (Exception ignored) {
                            }
                            try {
                                cdNow.setMinimumTemperature(SafeParse.parseDouble(data[6].trim()));
                            } catch (Exception ignored) {
                            }
                            try {
                                cdNow.setMaximumHumidity(SafeParse.parseDouble(data[7].trim()));
                            } catch (Exception ignored) {
                            }
                            try {
                                cdNow.setMinimumHumidity(SafeParse.parseDouble(data[8].trim()));
                            } catch (Exception ignored) {
                            }
                            try {
                                cdNow.setAverageWindSpeed(SafeParse.parseDouble(data[9].trim()));
                                cdNow.setWindSpeedHeight(10.0);
                            } catch (Exception ignored) {
                            }
                            try {
                                cdNow.setSolarRadiation(SafeParse.parseDouble(data[10].trim()));
                            } catch (Exception ignored) {
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                lastLoadedFile = fn;
            }

        } catch (UnsupportedOperationException ex) {
            // cannot load the csv file.
            return false;
        } catch (Exception ex) {
            logger.error(ex);
        }
        return true;
    }

    protected boolean loadStationData(String stationid, Date date) {
        String stationDataFilename = null;
        if (stationName == null || stationDataSubDir == null) {
            // ftp://ftp2.bom.gov.au/anon/gen/clim_data/IDCKWCDEA0/tables/
            try {
                BufferedReader br = BOMDataLoader.instance().getData(BOMDataLoader.FTP, BOMDataLoader.BOMFTP, BOMDataLoader.BOMBaseFTPDir + subDir + indexFile);
                while (stationName == null) {
                    String line = br.readLine();
                    if (line != null) {
                        Matcher m = indexPattern.matcher(line);
                        if (m.find()) {
                            String id = m.group(1).trim();
                            if (stationid.equals(id)) {
                                String state = m.group(2).trim().toLowerCase();
                                stationName = m.group(4).trim().toLowerCase().replace("aws", "").trim();
                                stationDataSubDir = (subDir + state + "/" + stationName + "/").replace(' ', '_');
                            }
                        }
                    } else {
                        break;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (stationDataSubDir != null && stationName != null) {
            stationDataFilename = (stationName + "-" + yearMonth.format(date) + ".csv").replace(' ', '_');
            return loadStationDataFile(stationDataSubDir, stationDataFilename, stationName);
        } else {
//            logger.warn("Cannot locate historical data " + yearMonthDay.format(date) + " for station - " + stationid);
            return false;
        }
    }

    synchronized public ClimateData getClimateData(Date date) {
        if (!invalidMonths.contains(Time.getMonthAndYear(date))) {
            if (!date.before(Time.getToday())) {
                throw new UnsupportedOperationException("Date requested is in the future");
            }
            try {
                if (date.after(source.getLastRecord())) {
                    throw new UnsupportedOperationException("Date requested is after the last record");
                }
            } catch (Exception ignored) {

            }
            ClimateData data = null;
            try {
                Date dayDate = yearMonthDay.parse(yearMonthDay.format(date));
                data = historicalData.get(dayDate);
                if (data == null) {
                    if (loadStationData(source.getId(), date)) {
                        data = historicalData.get(dayDate);
                    }
                }
            } catch (ParseException ignored) {
            }
            if (data != null) {
                return data;
            }
        }
        invalidMonths.add(Time.getMonthAndYear(date));
        throw new UnsupportedOperationException("Cannot locate historical data " + yearMonthDay.format(date) + " for for station - " + source.getId());
    }

    public Class<? extends ClimateData> getClimateDataClass() {
        return BOMHistoricalClimateData.class;
    }

    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return null;
    }

    public ClimateDataSource getSource() {
        return source;
    }

    public boolean supportsDate(Date date) {
        return !invalidMonths.contains(Time.getMonthAndYear(date)) && date.before(Time.getToday());
    }

    private static interface BOMHistoricalClimateData extends ClimateData {
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
        double getEvapotranspiration();

        @SupportedFunction(supported = true)
        double getMaximumHumidity();

        @SupportedFunction(supported = true)
        double getMinimumHumidity();

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
        double getSolarRadiation();

        @SupportedFunction(supported = true)
        Date getTime();

        @SupportedFunction(supported = true)
        double getWindSpeedHeight();

        @SupportedFunction(supported = true)
        ClimateDataSource getDataSource();

        @SupportedFunction(supported = true)
        double getAtmosphericPressure();

        @SupportedFunction(supported = true)
        double getDaylightHours();


    }
}
