/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;

import net.audumla.bean.SupportedFunction;
import net.audumla.climate.*;

import java.util.Date;

public class ZonedClimateObserver implements ClimateDataFactory, ClimateObserver {
    private final ClimateDataSource source;
    protected Zone zone;

    public ZonedClimateObserver(ClimateDataSource source, Zone zone) {
        this.zone = zone;
        this.source = source;

    }

    public ClimateData getClimateData(Date date) {
        return new ZonedClimateData(date, getSource());
    }

    public ClimateDataSource getSource() {
        return source;
    }

    @Override
    public Class<? extends ClimateData> getClimateDataClass() {
        return ZonedClimateData.class;
    }

    @Override
    public Class<? extends ClimateObservation> getClimateObservationClass() {
        return ZonedClimateObservation.class;
    }

    @Override
    public boolean supportsDate(Date date) {
        return true;
    }

    private class ZonedClimateObservation extends ClimateObservationAdaptor {

        public ZonedClimateObservation(ClimateObservation proxy) {
            super(proxy);
        }

        public double getWindSpeed() {
            double speed = getProxy().getWindSpeed();
            speed = speed - (((double) zone.getEnclosureRating()) * speed);
            return speed;
        }

        public double getRainfall() {
            double rain = getProxy().getRainfall();
            rain = rain - (((double) zone.getCoverRating()) * rain);
            return rain;
        }

    }

    private class ZonedClimateData extends ClimateDataAdaptor {

        protected ZonedClimateData(Date time, ClimateDataSource source) {
            super(time, source);
        }

        @SupportedFunction(supported = true)
        public double getRainfall() {
            double rain = getProxy().getRainfall();
            rain = rain - (((double) zone.getCoverRating()) * rain);
            return rain;
        }

        @SupportedFunction(supported = true)
        public double getAverageWindSpeed() {
            double speed = getProxy().getAverageWindSpeed();
            speed = speed - (((double) zone.getEnclosureRating()) * speed);
            return speed;
        }

        @SupportedFunction(supported = true)
        public double getSolarRadiation() {
            double rad = getProxy().getSolarRadiation();
            rad = rad - (((double) zone.getShadeRating()) * rad);
            return rad;
        }

        @Override
        @SupportedFunction(supported = true)
        public double getMaximumHumidity() {
            double rad = getProxy().getMaximumHumidity();
            rad = rad - (((double) zone.getEnclosureRating()) * rad);
            return rad;
        }

        @SupportedFunction(supported = true)
        public ClimateObservation getObservation(Date time, ObservationMatch match) {
            return new ZonedClimateObservation(getProxy().getObservation(time, match));
        }

    }

}
