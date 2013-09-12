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

import net.audumla.bean.BeanUtils;
import net.audumla.bean.SupportedFunction;

import java.util.Date;
import java.util.NavigableSet;

public class ClimateDataAdaptor implements ClimateData, BeanUtils.BeanProxy {
    private ClimateData bean;

    public ClimateDataAdaptor(Date time, ClimateDataSource source) {
        WritableClimateData b = BeanUtils.buildBean(WritableClimateData.class);
        b.setDataSource(source);
        b.setTime(time);
        bean = b;
    }

    @SupportedFunction(supported = false)
    public double getMinimumTemperature() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getMaximumTemperature() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getRainfall() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getRainfallProbability() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getSunshineHours() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getEvaporation() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getEvapotranspiration() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getMaximumHumidity() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getMinimumHumidity() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getMaximumSaturationVapourPressure() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getMinimumSaturationVapourPressure() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getMaximumVapourPressure() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getMinimumVapourPressure() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getAverageWindSpeed() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getSolarRadiation() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public Date getSunrise() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public Date getSunset() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = true)
    public Date getTime() {
        return bean.getTime();
    }

    @SupportedFunction(supported = false)
    public double getDewPoint() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getWindSpeedHeight() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public ClimateObservation getObservation(Date time, ObservationMatch match) {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = true)
    public ClimateDataSource getDataSource() {
        return bean.getDataSource();
    }

    @SupportedFunction(supported = false)
    public NavigableSet<ClimateObservation> getObservations() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getAtmosphericPressure() {
        throw new UnsupportedOperationException();
    }

    @SupportedFunction(supported = false)
    public double getDaylightHours() {
        throw new UnsupportedOperationException();
    }

    protected ClimateData getProxy() {
        return bean;
    }

    @Override
    public void setDelegator(Object proxy) {
        this.bean = (ClimateData) proxy;
    }
}
