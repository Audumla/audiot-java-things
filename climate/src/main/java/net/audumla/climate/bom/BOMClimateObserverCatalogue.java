package net.audumla.climate.bom;

import au.com.bytecode.opencsv.CSVReader;
import net.audumla.climate.*;
import net.audumla.util.SafeParse;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BOMClimateObserverCatalogue implements ClimateObserverFactory {

    protected static final Logger LOG = LogManager.getLogger(BOMClimateObserverCatalogue.class);
    protected static String BOMLatLongLookup = "http://www.bom.gov.au/jsp/ncc/cdio/weatherData/av?";
    protected static Pattern stationLookupPattern = Pattern.compile("(?:(\\d{4})_)?([^ ]*)\\s*((?:\\w*\\s)*)(\\w*\\s).([\\d.]*)");
    // .compile("(\\d{4})_(\\d{5})\\s*((?:\\w*\\s)*)(\\w*\\s).([\\d.]*)");
    protected static String statisticKey = "200&";
    protected static String historyKey = "201&";
    protected static String BOMStationCatalogueFile = "IDY02126.dat";

    public BOMClimateObserverCatalogue() {

    }

    protected static String generationCoordinateURL(ClimateDataSource llsource, String key) {
        // http://www.bom.gov.au/jsp/ncc/cdio/weatherData/av?p_stn_num=86071&p_display_type=nearest10_tab2&p_nccObsCode=139&p_match=50,,37.70,145.0972,,LATLON,201&sid=0.7166697874199599
        // 3079_86068 Watsonia VIC (4.5km away)
        String p_stn_num = "p_stn_num=86071&";
        String p_display_type = "p_display_type=nearest10_tab2&";
        String p_nccObsCode = "p_nccObsCode=139&";
        String p_match = "p_match=50,," + (-1 * llsource.getLatitude()) + "," + llsource.getLongitude() + ",,LATLON," + key;
        String sid = String.valueOf(Math.random());
        String url = BOMLatLongLookup + p_stn_num + p_display_type + p_nccObsCode + p_match + sid;
        return url;

    }

    protected static boolean loadBOMStationInfo(BOMClimateDataSource source) {
        if (source.getBOMSampleID() == null || source.getBOMSampleID().length() == 0) {
            String[] entry = BOMClimateObserverCatalogue.getStationEntry(source.getId());
            if (entry != null) {
                if (entry.length > 6) {
                    source.setBOMSampleID(entry[6].trim());
                }
                if (entry.length > 1) {
                    source.setName(entry[1].trim());
                }
                if (entry.length > 4) {
                    source.setLatitude(SafeParse.parseDouble(entry[3].trim()));
                    source.setLongitude(SafeParse.parseDouble(entry[4].trim()));
                }
                LOG.debug("Loaded BOM Station info [" + source.toString() + "]");

                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    protected static List<BOMClimateDataSource> parseSources(ClimateDataSource source, int maxResults, String
            results) {
        List<BOMClimateDataSource> stationSources = new ArrayList<BOMClimateDataSource>();
        // g1 = ?, g2 = station id, g3 = Station Name, g4 = State, g5 = distance from location
        String[] rrss = results.split("\\|\\|");
        for (String result : rrss) {
            Matcher m = stationLookupPattern.matcher(result.trim());
            if (m.find()) {
                BOMClimateDataSource bomSource = ClimateDataSourceFactory.newInstance(BOMClimateDataSource.class);
                bomSource.setId(m.group(2));
                for (int i = 0; i < 6 - m.group(2).length(); ++i) {
                    bomSource.setId("0" + bomSource.getId());
                }
                bomSource.setName(m.group(3));
                if (!stationSources.contains(bomSource)) {
                    if (loadBOMStationInfo(bomSource)) {
                        stationSources.add(bomSource);
                        if (stationSources.size() == maxResults) {
                            break;
                        }
                    }
                }
            }
        }

        List<BOMClimateDataSource> decoratedStationSources = new ArrayList<BOMClimateDataSource>();
        for (BOMClimateDataSource ss : stationSources) {
            decoratedStationSources.add(ClimateDataSourceFactory.decorateInstance(source, ss));
        }
        return decoratedStationSources;
    }

    protected static ClimateObserver getClimateObserverByCoordinates(ClimateDataSource coordinateSource) {

        ClimateObserverCollection aggregateObserver = new ClimateObserverCollectionHandler(coordinateSource);
        try {
            String url = generationCoordinateURL(coordinateSource, historyKey);
            List<BOMClimateDataSource> stationSources = new ArrayList<BOMClimateDataSource>();
            InputStream is = new URL(url).openStream();
            InputStreamReader sr = new InputStreamReader(is);
            char[] buffer = new char[10240];
            int c = 0;
            if ((c = sr.read(buffer, 0, 10240)) > 0) {
                stationSources = parseSources(coordinateSource, 5, new String(buffer, 0, c));
            }

            for (BOMClimateDataSource source : stationSources) {
                aggregateObserver.addClimateObserverTail(getClimateObserverById(BOMPeriodicClimateObserver.class, source));
            }
            for (BOMClimateDataSource source : stationSources) {
                aggregateObserver.addClimateObserverTail(getClimateObserverById(BOMHistoricalClimateObserver.class, source));
            }
            for (BOMClimateDataSource source : stationSources) {
                aggregateObserver.addClimateObserverTail(getClimateObserverById(BOMSimpleHistoricalClimateObserver.class, source));
            }
            for (BOMClimateDataSource source : stationSources) {
                aggregateObserver.addClimateObserverTail(getClimateObserverById(BOMSimpleClimateForcastObserver.class, source));
            }

            url = generationCoordinateURL(coordinateSource, statisticKey);
            stationSources = new ArrayList<BOMClimateDataSource>();
            is = new URL(url).openStream();
            sr = new InputStreamReader(is);
            buffer = new char[10240];
            c = 0;
            if ((c = sr.read(buffer, 0, 10240)) > 0) {
                stationSources = parseSources(coordinateSource, 5, new String(buffer, 0, c));
            }

            for (BOMClimateDataSource source : stationSources) {
                aggregateObserver.addClimateObserverTail(getClimateObserverById(BOMStatisticalClimateDataObserver.class, source));
            }
            return aggregateObserver.buildClimateObserver();

        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    protected static ClimateObserver getClimateObserverById(Class<?> clazz, ClimateDataSource source) throws Exception {
        ClimateDataSource name = ClimateDataSourceFactory.newInstance();
        name.setId(source.getId() + ":" + clazz.getName());
        ClimateObserver station = ClimateObserverCatalogue.getInstance().getRegisteredClimateObserver(name);
        if (station == null) {
            // delegated to allow ClimateObservers to not be compiled into factory
            BOMClimateDataSource bomSource = ClimateDataSourceFactory.decorateInstance(source, BOMClimateDataSource.class);
            loadBOMStationInfo(bomSource);
            station = (ClimateObserver) ConstructorUtils.invokeConstructor(clazz, bomSource);
            ClimateObserverCatalogue.getInstance().registerClimateObserver(name, station);
        }
        return station;
    }

    protected static ClimateObserver getClimateObserverById(ClimateDataSource source) throws Exception {
        ClimateObserverCollection bomstation = new ClimateObserverCollectionHandler(source);
        bomstation.addClimateObserverTail(getClimateObserverById(BOMPeriodicClimateObserver.class, source));
        bomstation.addClimateObserverTail(getClimateObserverById(BOMHistoricalClimateObserver.class, source));
        bomstation.addClimateObserverTail(getClimateObserverById(BOMSimpleHistoricalClimateObserver.class, source));
        bomstation.addClimateObserverTail(getClimateObserverById(BOMSimpleClimateForcastObserver.class, source));
        bomstation.addClimateObserverTail(getClimateObserverById(BOMStatisticalClimateDataObserver.class, source));
        return bomstation.buildClimateObserver();
    }

    protected static String generateStationCatalogueURL() {
        return BOMDataLoader.BOMBaseFTPDir + BOMDataLoader.FWO + BOMStationCatalogueFile;
    }

    protected static String[] getStationEntry(String id) {
        try {
            CSVReader csv = new CSVReader(BOMDataLoader.instance().getData(BOMDataLoader.FTP, BOMDataLoader.BOMFTP, generateStationCatalogueURL()));
            String[] line;
            /* "014305","KATHERINE COUNT","AAAA",-14.4728,132.2612, 106,"94132","NT " */
            while ((line = csv.readNext()) != null) {
                if (id.equals(line[0].replace("\"", ""))) {
                    return line;
                }
            }
        } catch (IOException e) {
            LOG.error("Station Catalogue error", e);
        }
        return null;
    }

    public boolean validateBOMConnection(ClimateDataSource llsource) throws Exception {
        InputStream is = new URL(generationCoordinateURL(llsource, statisticKey)).openStream();
        is.close();
        BOMDataLoader.instance().getData(BOMDataLoader.FTP, BOMDataLoader.BOMFTP, generateStationCatalogueURL());
        return true;
    }

    @Override
    public synchronized ClimateObserver getClimateObserver(ClimateDataSource source) {
        ClimateObserver station = null;// ClimateObserverCatalogue.getInstance().buildClimateObserver(source);
        try {
            if (station == null) {
                if (source.getId() != null && source.getId().length() > 0) {
                    station = getClimateObserverById(source);
                } else {
                    if (source.getLatitude() != 0 && source.getLongitude() != 0) {
                        station = getClimateObserverByCoordinates(source);
                    }
                }
            }
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
        return station;
    }

}
