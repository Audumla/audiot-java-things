package net.audumla.climate;

import java.util.Date;
import java.util.NavigableSet;

/*
*
 */
public interface WritableClimateObservation extends ClimateObservation {

    void setDewPoint(Double dewpt);

    void setTime(Date local_date_time_full);

    void setHumidity(Double humidity);

    void setRainfall(Double rain_trace);

    void setRainfallProbablity(Double prob);

    void setWindDirection(String wind_dir);

    void setWindSpeed(Double wind_spd_kmh);

    void setWindSpeedHeight(Double height);

    void setApparentTemperature(Double temp);

    void setTemperature(Double temp);

    void setObservationSet(NavigableSet<ClimateObservation> obs);

    void setWetBulbTemperature(Double t);

    void setAtmosphericPressure(Double p);

    void setDataSource(ClimateDataSource source);
}