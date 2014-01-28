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