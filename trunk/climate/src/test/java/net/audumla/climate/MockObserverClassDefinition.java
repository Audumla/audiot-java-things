package net.audumla.climate;
/**
 * User: audumla
 * Date: 7/08/13
 * Time: 10:42 AM
 */

import net.audumla.bean.SupportedFunction;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MockObserverClassDefinition implements ClimateObserver, ClimateDataFactory {
    private static final Logger logger = LogManager.getLogger(MockObserverClassDefinition.class);
    private final ClimateDataSource source;
    private Map<Long, WritableClimateData> dataList = new TreeMap<Long, WritableClimateData>();

    private static interface MockClimateObservation extends ClimateObservation {
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
        double getRainfall();

        @SupportedFunction(supported = true)
        double getRainfallProbability();

        @SupportedFunction(supported = true)
        List<ClimateConditions> getClimateConditions();

    }

    private static interface MockClimateData extends ClimateData {
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
        Date getSunrise();

        @SupportedFunction(supported = true)
        Date getSunset();

        @SupportedFunction(supported = true)
        double getWindSpeedHeight();

        @SupportedFunction(supported = true)
        double getAtmosphericPressure();

        @SupportedFunction(supported = true)
        double getDaylightHours();

        @SupportedFunction(supported = true)
        NavigableSet<ClimateObservation> getObservations();
    }

    public MockObserverClassDefinition() {
        Date now = new Date();
        now = DateUtils.setDays(now, 1);
        now = DateUtils.setMonths(now, 1);
        now = DateUtils.setYears(now, 2010);
        now = DateUtils.setHours(now, 0);
        now = DateUtils.setMinutes(now, 0);

        source = ClimateDataSourceFactory.newInstance();
        source.setName("Mock");
        source.setElevation(20d);
        source.setLatitude(-37.84);
        source.setLongitude(144.98);

        for (int i = 0; i < 28; ++i) {
            WritableClimateData data = ClimateDataFactory.newWritableClimateData(this,getSource());
            dataList.put(DateUtils.getFragmentInDays(now, Calendar.MONTH), data);
            data.setTime(now);

            if (i < 25) {
                data.setDataSource(ClimateDataSourceFactory.decorateInstance(source));
                data.getDataSource().setType(ClimateDataSource.ClimateDataSourceType.DAILY_OBSERVATION);
                data.setAverageWindSpeed(10.0d + (i / 10d));
                data.setMaximumTemperature(10.0d + (i / 10d));
                data.setMinimumTemperature(10.0d - (i / 10d));
                data.setMinimumHumidity(60d - i);
                data.setMaximumHumidity(60d + i);
                data.setRainfall(i / 10d);
                data.setRainfallProbability(data.getRainfall() > 0 ? 100d : 0d);
                data.setSunshineHours(6d + (i / 15d));
               // data.setSolarRadiation(5 + (i / 5d));
                data.setWindSpeedHeight(10.0);
            }
            else {
                data.setDataSource(ClimateDataSourceFactory.decorateInstance(source));
                data.getDataSource().setType(ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST);
                data.setAverageWindSpeed(10.0 + (i / 10d));
                data.setMaximumTemperature(10.0 + (i / 10d));
                data.setMinimumTemperature(10.0 - (i / 10d));
                data.setRainfall(i / 10d);
                data.setRainfallProbability(data.getRainfall() > 0 ? 80d : 0d);
                data.setSunshineHours(6d + (i / 15d));
                data.setWindSpeedHeight(10.0d);
                data.setMinimumHumidity(60d - i);
                data.setMaximumHumidity(60d + i);
            }

            if (i > 22 && i < 25) {
                now = DateUtils.setHours(now, 0);
                now = DateUtils.setMinutes(now, 0);
                for (int n = 1; n < 48; ++n) {
                    WritableClimateObservation obs = ClimateDataFactory.newWritableClimateObservation(this,getSource());
                    obs.setDataSource(ClimateDataSourceFactory.decorateInstance(source));
                    obs.getDataSource().setType(ClimateDataSource.ClimateDataSourceType.PERIODIC_OBSERVATION);
                    obs.setTime(now);
                    obs.setWindSpeedHeight(10.0);
                    obs.setWindSpeed(data.getAverageWindSpeed()/48d);
                    if (n > 10 && n < 21) {
                        obs.setRainfall(data.getRainfall() * (n/20d));
                    }
                    else {
                        obs.setRainfall(0d);
                    }
                    obs.setTemperature(data.getMinimumTemperature() + (((data.getMinimumTemperature() - data.getMaximumTemperature())/48d)*n));
                    obs.setHumidity(data.getMinimumHumidity() + (((data.getMinimumHumidity() - data.getMaximumHumidity())/48d)*n));
                    obs.setRainfallProbablity(obs.getRainfall() > 0 ? 100d : 0d);
                    data.addObservation(obs);
                    now = DateUtils.addMinutes(now, 30);
                }
            }

            now = DateUtils.addDays(now, 1);

        }
    }

    @Override
    public ClimateData getClimateData(Date date) {
        return dataList.get(DateUtils.getFragmentInDays(date, Calendar.MONTH));
    }

    @Override
    public ClimateDataSource getSource() {
        return source;
    }

    @Override
    public Class<? extends ClimateData> getClimateDataClass() {
        return MockClimateData.class;
    }

    @Override
    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return MockClimateObservation.class;
    }

    @Override
    public boolean supportsDate(Date date) {
        return true;
    }
}
