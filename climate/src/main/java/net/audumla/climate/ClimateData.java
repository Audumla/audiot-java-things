package net.audumla.climate;

import net.audumla.bean.SupportedFunction;

import java.util.Date;
import java.util.NavigableSet;

public interface ClimateData {
    @SupportedFunction(supported = false)
    double getMinimumTemperature();

    @SupportedFunction(supported = false)
    double getMaximumTemperature();

    @SupportedFunction(supported = false)
    double getRainfall();

    @SupportedFunction(supported = false)
    double getRainfallProbability();

    @SupportedFunction(supported = false)
    double getSunshineHours();

    @SupportedFunction(supported = false)
    double getEvaporation();

    @SupportedFunction(supported = false)
    double getEvapotranspiration();

    @SupportedFunction(supported = false)
    double getMaximumHumidity();

    @SupportedFunction(supported = false)
    double getMinimumHumidity();

    @SupportedFunction(supported = false)
    double getMaximumSaturationVapourPressure(); //kPA

    @SupportedFunction(supported = false)
    double getMinimumSaturationVapourPressure(); //kPA

    @SupportedFunction(supported = false)
    double getMaximumVapourPressure();

    @SupportedFunction(supported = false)
    double getMinimumVapourPressure();

    @SupportedFunction(supported = false)
    double getAverageWindSpeed();

    @SupportedFunction(supported = false)
    double getSolarRadiation();

    @SupportedFunction(supported = false)
    Date getSunrise();

    @SupportedFunction(supported = false)
    Date getSunset();

    @SupportedFunction(supported = false)
    double getWindSpeedHeight();

    @SupportedFunction(supported = false)
    double getAtmosphericPressure();

    @SupportedFunction(supported = false)
    double getDaylightHours();

    @SupportedFunction(supported = false)
    NavigableSet<ClimateObservation> getObservations();

    @SupportedFunction(supported = false)
    ClimateObservation getObservation(Date time, ObservationMatch match);

    @SupportedFunction(supported = true)
    Date getTime();

    @SupportedFunction(supported = true)
    ClimateDataSource getDataSource();


    public enum ObservationMatch {PREVIOUS, SUBSEQUENT, CLOSEST}


}
