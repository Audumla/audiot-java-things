package net.audumla.climate.bom;

import net.audumla.bean.SupportedFunction;
import net.audumla.climate.*;
import net.audumla.util.SafeParse;
import net.audumla.util.Time;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BOMPeriodicClimateObserver implements ClimateDataFactory, ClimateObserver {
    private static final Logger logger = Logger.getLogger(BOMPeriodicClimateObserver.class);
    private static final int MAX_ENTRIES = 20;
    private static final SimpleDateFormat longLocalTime = new SimpleDateFormat("yyyyMMddHHmmss");// 20130130180000
    private String[] BOMSampleProductIDs = {"IDV60901", "IDV60801"};
    private Map<Date, ClimateData> observations;
    private BOMClimateDataSource source;

    public BOMPeriodicClimateObserver(BOMClimateDataSource source) {
        this.source = source;
        this.source.setType(ClimateDataSource.ClimateDataSourceType.PERIODIC_OBSERVATION);
        BOMClimateObserverCatalogue.loadBOMStationInfo(source);
        if (source.getBOMSampleID() == null || source.getBOMSampleID().length() == 0) {
            throw new UnsupportedOperationException("No BOM Sample ID set for observer [" + getSource().toString() + "]");
        }
        initialize();
    }

    protected void initialize() {
        observations = new LinkedHashMap<Date, ClimateData>(MAX_ENTRIES + 1, .75F, true) {
            private static final long serialVersionUID = -2478618100141569239L;

            public boolean removeEldestEntry(Map.Entry<Date, ClimateData> eldest) {
                return size() > MAX_ENTRIES;
            }
        };
        logger.debug("Initialized BOM PERIODIC CLIMATE Observer [" + source.toString() + "]");

    }

    synchronized public ClimateData getClimateData(Date date) {
        ClimateData data = observations.get(Time.getDayAndYear(date));
        if (data != null) {
            return data;
        } else {
            loadSampleData();
            data = observations.get(Time.getDayAndYear(date));
            if (data != null) {
                return data;
            }
            throw new UnsupportedOperationException("No climate observations found for " + date);
        }
    }

    private void parseJSONObjects(JSONObject jsonData) throws JSONException, ParseException, Exception {
        try {
            JSONArray data = jsonData.getJSONObject("observations").getJSONArray("data");
            WritableClimateObservation bomdata = null;
            Map<Date, WritableClimateData> tempObservations = new HashMap<Date, WritableClimateData>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject dobj = data.getJSONObject(i);
                try {
                    bomdata = ClimateDataFactory.newWritableClimateObservation(this, getSource());
                    bomdata.setRainfall(SafeParse.parseDouble(dobj.get("rain_trace")));
                    Double rt = SafeParse.parseDouble(dobj.get("rain_trace"));
                    if (rt != null) {
                        bomdata.setRainfallProbablity(rt > 0 ? 100.0d : 0.0d);
                    }
                    bomdata.setHumidity(SafeParse.parseDouble(dobj.get("rel_hum")));
                    bomdata.setWindDirection((String) dobj.get("wind_dir"));
                    bomdata.setWindSpeedHeight(10.0d);
                    bomdata.setWindSpeed(SafeParse.parseDouble(dobj.get("wind_spd_kmh")));
                    bomdata.setTemperature(SafeParse.parseDouble(dobj.get("air_temp")));
                    bomdata.setApparentTemperature(SafeParse.parseDouble(dobj.get("apparent_t")));
                    try {
                        bomdata.setTime(longLocalTime.parse(dobj.get("local_date_time_full").toString()));
                    } catch (Exception e) {
                        logger.error("Error loading observation data [" + getSource().toString() + "] - " + dobj.get("local_date_time_full").toString(), e);

                    }
                    bomdata.setDewPoint(SafeParse.parseDouble(dobj.get("dewpt")));
                    bomdata.setAtmosphericPressure(SafeParse.parseDouble("press_qnh"));
                    Double wbDelta = SafeParse.parseDouble(dobj.get("delta_t"));
                    if (wbDelta == null) {
                        bomdata.setWetBulbTemperature(bomdata.getTemperature());
                    } else {
                        bomdata.setWetBulbTemperature(bomdata.getTemperature() - wbDelta);
                    }

                    Date day = Time.getDayAndYear(bomdata.getTime());
                    WritableClimateData convertedData = tempObservations.get(day);
                    if (convertedData == null) {
                        convertedData = ClimateDataFactory.newWritableClimateData(this, getSource());
                        convertedData.setTime(day);
                        tempObservations.put(day, convertedData);
                    }
                    convertedData.addObservation(bomdata);
                } catch (Exception ex) {
                    logger.error("Error loading observation data [" + getSource().toString() + "] - " + dobj.toString(), ex);
                }
            }
            for (Date key : tempObservations.keySet()) {
                observations.put(key, ClimateDataFactory.convertToReadOnlyClimateData(tempObservations.get(key)));
            }
        } catch (Exception ex) {
            logger.error("Error loading observation data [" + getSource().toString() + "]", ex);
        }
    }

    public boolean validateBOMConnection() throws Exception {
        for (String id : BOMSampleProductIDs) {
            String loc = getSampleURL(id);
            BOMDataLoader.instance().getData(BOMDataLoader.HTTP, BOMDataLoader.BOMHTTP, loc);
        }
        return true;
    }

    public String getSampleURL(String id) {
        return BOMDataLoader.FWO + id + "/" + id + "." + source.getBOMSampleID() + ".json";
    }

    public boolean loadSampleData() {
        // search based on sample product id
        for (String id : BOMSampleProductIDs) {
            try {
                String loc = getSampleURL(id);
                if (observations.isEmpty() || BOMDataLoader.instance().hasDataExpired(BOMDataLoader.HTTP, BOMDataLoader.BOMHTTP, loc)) {
                    Reader reader = null;
                    try {
                        reader = BOMDataLoader.instance().getData(BOMDataLoader.HTTP, BOMDataLoader.BOMHTTP, loc);
                    } catch (Exception ignored) {

                    }
                    if (reader != null) {
                        JSONObject json = new JSONObject(IOUtils.toString(reader));
                        parseJSONObjects(json);
                        if (BOMSampleProductIDs.length > 1) {
                            BOMSampleProductIDs = new String[]{id};
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return false;
    }

    public Class<? extends ClimateData> getClimateDataClass() {
        return BOMObservationClimateData.class;
    }

    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return BOMClimateObservation.class;
    }

    public ClimateDataSource getSource() {

        return source;
    }

    public boolean supportsDate(Date date) {
        Date limit = DateUtils.addDays(Time.getDayAndYear(date), -2); // This observer can support observations for 3 days including today.
        return !(date.before(limit) || date.after(Time.getNow()));
    }

    private static interface BOMClimateObservation extends ClimateObservation {
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

        @SupportedFunction(supported = true)
        double getRainfall(int minutes);

        @SupportedFunction(supported = true)
        double getRainfallProbability(int minutes);

        @SupportedFunction(supported = true)
        Date getTime();

        @SupportedFunction(supported = true)
        ClimateObservation getPreviousObservation();

        @SupportedFunction(supported = true)
        ClimateObservation getNextObservation();

        @SupportedFunction(supported = true)
        ClimateDataSource getDataSource();

    }

    private static interface BOMObservationClimateData extends WritableClimateData {
        @SupportedFunction(supported = true)
        ClimateObservation getObservation(Date time, ObservationMatch match);

        @SupportedFunction(supported = true)
        NavigableSet<ClimateObservation> getObservations();

        @SupportedFunction(supported = true)
        ClimateDataSource getDataSource();


    }

}
