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

import net.audumla.bean.BeanUtils.BeanProxy;
import net.audumla.bean.SupportedFunction;
import net.audumla.Time;

import java.util.Date;

public class DerivedClimateObserver implements ClimateDataFactory, ClimateObserver {

    protected ClimateDataSource source;

    public DerivedClimateObserver(ClimateDataSource source) {
        this.source = source;
        this.source.setType(ClimateDataSource.ClimateDataSourceType.DERIVED);
    }

    public ClimateData getClimateData(Date date) {
        return new DerivedClimateData(date, getSource());
    }

    public ClimateDataSource getSource() {
        return source;
    }

    public Class<? extends ClimateData> getClimateDataClass() {
        return DerivedClimateData.class;
    }

    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return DerivedClimateObservation.class;
    }

    public boolean supportsDate(Date date) {
        return true;
    }

    private static class DerivedClimateObservation extends ClimateObservationAdaptor {
        public DerivedClimateObservation(ClimateObservation observation) {
            super(observation);
        }
    }

    private static class DerivedClimateData extends ClimateDataAdaptor implements BeanProxy {


        public DerivedClimateData(Date time, ClimateDataSource source) {
            super(time, source);
        }

        @SupportedFunction(supported = true)
        public double getMaximumSaturationVapourPressure() {
            return ClimateCalculations.getSaturationVapourPressure(getProxy().getMaximumTemperature());
        }

        @SupportedFunction(supported = true)
        public double getMinimumSaturationVapourPressure() {
            return ClimateCalculations.getSaturationVapourPressure(getProxy().getMinimumTemperature());
        }

        @SupportedFunction(supported = true)
        public double getMaximumVapourPressure() {
            return getMaximumSaturationVapourPressure() * getProxy().getMaximumHumidity() / 100;
        }

        @SupportedFunction(supported = true)
        public double getMinimumVapourPressure() {
            return getMinimumSaturationVapourPressure() * getProxy().getMinimumHumidity() / 100;
        }

        @SupportedFunction(supported = true)
        public double getEvapotranspiration() {
            return new ClimateCalculations().ETo(getProxy(), getProxy().getTime(), 24);
        }

        @SupportedFunction(supported = true)
        public double getAtmosphericPressure() {
            return ClimateCalculations.getAtmosphericPressure(getProxy().getDataSource().getElevation());
        }

        @SupportedFunction(supported = true)
        public double getDaylightHours() {
            return ClimateCalculations.N(this.getDataSource(), getTime());
        }

        @SupportedFunction(supported = true)
        public Date getSunrise() {
            return Time.getSunrise(getProxy().getTime(), getProxy().getDataSource().getLatitude(), getProxy().getDataSource().getLongitude());
        }

        @SupportedFunction(supported = true)
        public Date getSunset() {
            return Time.getSunset(getProxy().getTime(), getProxy().getDataSource().getLatitude(), getProxy().getDataSource().getLongitude());
        }

        @SupportedFunction(supported = false)
        public ClimateObservation getObservation(Date time, ObservationMatch match) {
            throw new UnsupportedOperationException();
        }
    }


	/*
     * public double getVapourPressure() { return getSaturationVapourPressure()*bean.getHumidity()/100; }
	 * 
	 * public double getSaturationVapourPressure() { return ClimateDataDecorator.getSaturationVapourPressure(bean.getTemperature()); }
	 */

    /**
     * Gets the rainfall.
     *
     * @param minutes
     *            the minutes
     * @return the rainfall
     */
    /*
     * public double getRainfall(int minutes) { if (minutes == 0) { return (Double) bean.get("Rainfall"); } else { double current = bean.getRainfall(0);
	 * Calendar c = Calendar.getInstance(); c.setTime(bean.getTime()); c.add(Calendar.MINUTE, minutes); Date past = c.getTime(); ClimateObservation ob = bean;
	 * ClimateObservation prev = null; while (ob != null) { if (ob.getTime().before(past)) { if (prev != null) { current = current - prev.getRainfall(0); } ob =
	 * null; } else { prev = ob; ob = ob.getPreviousObservation(); } } ; return current; } }
	 */

}
