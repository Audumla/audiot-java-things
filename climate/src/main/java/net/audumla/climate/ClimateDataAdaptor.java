package net.audumla.climate;

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
