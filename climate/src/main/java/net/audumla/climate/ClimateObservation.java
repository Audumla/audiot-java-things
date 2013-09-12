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
