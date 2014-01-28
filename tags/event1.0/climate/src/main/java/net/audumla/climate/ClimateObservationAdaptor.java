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

import java.util.Date;
import java.util.List;
import java.util.NavigableSet;

public class ClimateObservationAdaptor implements ClimateObservation, BeanUtils.BeanProxy {

    private ClimateObservation bean;

    public ClimateObservationAdaptor(ClimateObservation proxy) {
        bean = proxy;
    }

    public double getTemperature() {
        throw new UnsupportedOperationException();
    }

    public double getWetBulbTemperature() {
        throw new UnsupportedOperationException();
    }

    public double getAtmosphericPressure() {
        throw new UnsupportedOperationException();
    }

    public double getVapourPressure() {
        throw new UnsupportedOperationException();
    }

    public double getSaturationVapourPressure() {
        throw new UnsupportedOperationException();
    }

    public double getApparentTemperature() {
        throw new UnsupportedOperationException();
    }

    public double getHumidity() {
        throw new UnsupportedOperationException();
    }

    public double getDewPoint() {
        throw new UnsupportedOperationException();
    }

    public double getWindSpeed() {
        throw new UnsupportedOperationException();
    }

    public double getWindSpeedHeight() {
        throw new UnsupportedOperationException();
    }

    public String getWindDirection() {
        throw new UnsupportedOperationException();
    }

    public double getRainfall() {
        throw new UnsupportedOperationException();
    }

    public double getRainfallSince(ClimateObservation previousObservation) {
        throw new UnsupportedOperationException();
    }

    public double getRainfallProbability() {
        throw new UnsupportedOperationException();
    }

    public List<ClimateConditions> getClimateConditions() {
        throw new UnsupportedOperationException();
    }

    public Date getTime() {
        throw new UnsupportedOperationException();
    }

    public ClimateObservation getPreviousObservation() {
        throw new UnsupportedOperationException();
    }

    public ClimateObservation getNextObservation() {
        throw new UnsupportedOperationException();
    }

    public ClimateDataSource getDataSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<ClimateObservation> getObservationSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDelegator(Object proxy) {
        this.bean = (ClimateObservation) proxy;
    }

    protected ClimateObservation getProxy() {
        return bean;
    }
}
