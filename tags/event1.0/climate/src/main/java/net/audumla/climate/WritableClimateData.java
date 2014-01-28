package net.audumla.climate;

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