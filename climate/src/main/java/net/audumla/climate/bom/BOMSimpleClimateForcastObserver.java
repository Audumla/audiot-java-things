package net.audumla.climate.bom;

import au.com.bytecode.opencsv.CSVReader;
import net.audumla.bean.SupportedFunction;
import net.audumla.climate.*;
import net.audumla.util.Time;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BOMSimpleClimateForcastObserver implements ClimateDataFactory, ClimateObserver {
    private static final Logger LOG = LogManager.getLogger(BOMSimpleClimateForcastObserver.class);
    private static String[] BOMForcastFiles = new String[]{"IDY02128.dat", "IDY02123.dat", "IDY02122.dat", "IDY02129.dat"};
    private final int MAX_ENTRIES = 20;
    private final ClimateDataSource source;
    private Pattern forcastDatePattern = Pattern.compile("date=(........)");
    private SimpleDateFormat forcastDateFormat = new SimpleDateFormat("yyyyMMdd");
    private LinkedHashMap<Date, ClimateData> forcastData;

    @SuppressWarnings("serial")
    public BOMSimpleClimateForcastObserver(ClimateDataSource source) {
        this.source = source;
        this.source.setType(ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST);
        forcastData = new LinkedHashMap<Date, ClimateData>(MAX_ENTRIES + 1, .75F, true) {
            public boolean removeEldestEntry(Map.Entry<Date, ClimateData> eldest) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    protected String getLatestForcastFile() {
        BOMDataLoader loader = BOMDataLoader.instance();
        Date time = new Date(0);
        String found = null;
        String filename = "";
        for (String name : BOMForcastFiles) {
            try {
                filename = BOMDataLoader.BOMBaseFTPDir + BOMDataLoader.FWO + name;
                FTPFile file = loader.getFTPFile(BOMDataLoader.BOMFTP, filename);
                if (file != null) {
                    if (file.getTimestamp().getTime().after(time)) {
                        time = file.getTimestamp().getTime();
                        found = filename;
                    }
                } else {
                    LOG.error("Error locating file " + BOMDataLoader.BOMFTP + filename);
                }
            } catch (Exception ex) {
                LOG.error("Error locating file " + BOMDataLoader.BOMFTP + filename, ex);
            }
        }
        return filename;
    }

    public void loadLatestForcast() {
        try {
            CSVReader reader = new CSVReader(BOMDataLoader.instance().getData(BOMDataLoader.FTP, BOMDataLoader.BOMFTP, getLatestForcastFile()));
            String[] line = reader.readNext();
            Matcher m = forcastDatePattern.matcher(line[0]);
            if (m.find()) {
                Date forcastDate = forcastDateFormat.parse(m.group(1));
                while ((line = reader.readNext()) != null) {
                    if (source.getId().equals(line[0].replace("\"", ""))) {
                        for (int c = 0; c < line.length; ++c) {
                            line[c] = line[c].replace("-9999", "error").trim();
                        }
                        // stn[7] , per, evap, amax, amin, gmin, suns, rain, prob
                        WritableClimateData bforcast = ClimateDataFactory.newWritableClimateData(this,getSource());
                        int day = Integer.parseInt(line[1]);
                        Calendar c = GregorianCalendar.getInstance();
                        c.setTime(new Date(forcastDate.getTime()));
                        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + day);
                        bforcast.setTime(c.getTime());

                        try {
                            bforcast.setEvaporation(Double.parseDouble(line[2]));
                        } catch (Exception ignored) {
                        }
                        try {
                            bforcast.setMaximumTemperature(Double.parseDouble(line[3]));
                        } catch (Exception ignored) {
                        }
                        try {
                            bforcast.setMinimumTemperature(Double.parseDouble(line[4]));
                        } catch (Exception ignored) {
                        }
                        try {
                            bforcast.setSunshineHours(Double.parseDouble(line[6]));
                        } catch (Exception ignored) {
                        }
                        try {
                            bforcast.setRainfall(Double.parseDouble(line[7]));
                        } catch (Exception ignored) {
                        }
                        try {
                            bforcast.setRainfallProbability(Double.parseDouble(line[8]));
                        } catch (Exception ignored) {
                        }
                        forcastData.put(c.getTime(), bforcast);
                    }
                }
            } else {
                throw new UnsupportedOperationException("Cannot find forecast date in file");
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error reading BOM forcast", e);
        }
    }

    synchronized public ClimateData getClimateData(Date date) {
        if (date.before(Time.getToday())) {
            throw new UnsupportedOperationException("Date requested is in the past and cannot be forecast");
        }
        Date newDate;
        try {
            newDate = forcastDateFormat.parse(forcastDateFormat.format(date));
            ClimateData forcast = forcastData.get(newDate);
            if (forcast == null) {
                loadLatestForcast();
                forcast = forcastData.get(newDate);
            }
            if (forcast == null) {
                throw new UnsupportedOperationException("Date is not in forecast data");
            } else {
                return forcast;
            }
        } catch (ParseException e) {
            throw new UnsupportedOperationException("Error getting BOM forcast", e);
        }
    }

    public Class<? extends ClimateData> getClimateDataClass() {
        return BOMClimateForcastData.class;
    }

    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return null;
    }

    public ClimateDataSource getSource() {
        return source;
    }

    public boolean supportsDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(Time.getToday());
        return date.after(c.getTime());
    }

    private static interface BOMClimateForcastData extends ClimateData {

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
        Date getTime();

        @SupportedFunction(supported = true)
        ClimateDataSource getDataSource();

        @SupportedFunction(supported = true)
        double getAtmosphericPressure();

        @SupportedFunction(supported = true)
        double getDaylightHours();

    }
}