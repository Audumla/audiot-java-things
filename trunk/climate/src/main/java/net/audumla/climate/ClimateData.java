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
