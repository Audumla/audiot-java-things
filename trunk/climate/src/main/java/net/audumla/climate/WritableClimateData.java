package net.audumla.climate;

import java.util.Date;
import java.util.NavigableSet;

public interface WritableClimateData extends ClimateData {

    void setRainfall(Double rainfall);

    void setSunshineHours(Double sunshineHours);

    void setEvaporation(Double evaporation);

    void setEvapotranspiration(Double evapotranspiration);

    void setRainfallProbability(Double probabilityOfRainfall);

    void setMaximumHumidity(Double maximimHumidity);

    void setMinimumHumidity(Double minimumHumidity);

    void setSunrise(Date sunrise);

    void setSunset(Date sunset);

    void setAverageWindSpeed(Double windspeed);

    void setSolarRadiation(Double solar);

    void setTime(Date time);

    void setMinimumTemperature(Double minimumTemperature);

    void setMaximumTemperature(Double maximumTemperature);

    void addObservation(WritableClimateObservation obs);

    void setObservations(NavigableSet<ClimateObservation> list);

    void setWindSpeedHeight(Double height);

    void setDataSource(ClimateDataSource source);
}