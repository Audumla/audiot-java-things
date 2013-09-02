package net.audumla.climate;

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
