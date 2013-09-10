/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 30/07/13
 * Time: 7:16 PM
 */

import net.audumla.climate.ClimateData;
import net.audumla.climate.ClimateObservation;
import net.audumla.climate.ClimateObserver;
import org.apache.log4j.Logger;

import java.util.Date;

public class ForecastIrrigationDurationFactory implements IrrigationDurationFactory {
    private static final Logger logger = Logger.getLogger(ForecastIrrigationDurationFactory.class);
    private IrrigationDurationFactory factory;
    private Zone zone;

    public ForecastIrrigationDurationFactory() {
    }

    public void setFactory(IrrigationDurationFactory factory) {
        this.factory = factory;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    public long determineIrrigationDuration(Date now) {
        long duration = factory.determineIrrigationDuration(now);
        ClimateData data = zone.getClimateObserver().getClimateData(now);
        double predictedRainfall = data.getRainfall();
        double chanceOfRainfall = data.getRainfallProbability();
        double rainSince9am = 0;
        ClimateObservation obs = data.getObservation(now, ClimateData.ObservationMatch.SUBSEQUENT);
        if (obs != null) {
            //rainSince9am = obs.getRainfall();
        }

        return 0;
    }
}
