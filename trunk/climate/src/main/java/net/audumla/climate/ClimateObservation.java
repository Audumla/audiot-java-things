package net.audumla.climate;

import net.audumla.bean.SupportedFunction;

import java.util.Date;
import java.util.List;
import java.util.NavigableSet;

public interface ClimateObservation {
    @SupportedFunction(supported = false)
    double getTemperature();

    @SupportedFunction(supported = false)
    double getWetBulbTemperature();

    @SupportedFunction(supported = false)
    double getAtmosphericPressure();

    @SupportedFunction(supported = false)
    double getVapourPressure();

    @SupportedFunction(supported = false)
    double getSaturationVapourPressure();

    @SupportedFunction(supported = false)
    double getApparentTemperature();

    @SupportedFunction(supported = false)
    double getHumidity();

    @SupportedFunction(supported = false)
    double getDewPoint();

    @SupportedFunction(supported = false)
    double getWindSpeed();

    @SupportedFunction(supported = false)
    double getWindSpeedHeight();

    @SupportedFunction(supported = false)
    String getWindDirection();

    @SupportedFunction(supported = false)
    double getRainfall();

    @SupportedFunction(supported = false)
    double getRainfallProbability();

    @SupportedFunction(supported = false)
    List<ClimateConditions> getClimateConditions();

    @SupportedFunction(supported = true)
    double getRainfallSince(ClimateObservation previousObservation);

    @SupportedFunction(supported = true)
    ClimateObservation getPreviousObservation();

    @SupportedFunction(supported = true)
    ClimateObservation getNextObservation();

    @SupportedFunction(supported = true)
    Date getTime();

    @SupportedFunction(supported = true)
    ClimateDataSource getDataSource();

    @SupportedFunction(supported = true)
    NavigableSet<ClimateObservation> getObservationSet();
}
