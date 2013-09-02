/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * User: audumla
 * Date: 21/07/13
 * Time: 6:54 PM
 */
public class EToIrrigationEventFactory implements IrrigationEventFactory {
    private static final Logger logger = LogManager.getLogger(EToIrrigationEventFactory.class);
    private final IrrigationDurationFactory durationFactory;
    private final EToCalculator etc;
    private double threshold = 0.0;
    private Zone zone;


    public EToIrrigationEventFactory(Zone zone, EToCalculator etc, IrrigationDurationFactory durationFactory) {
        this.zone = zone;
        this.durationFactory = durationFactory;
        this.etc = etc;

    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public IrrigationEvent generateIrrigationEvent(Date now) {
        try {
            long duration = 0;
            double debt = etc.calculateETo(now); //mm
            if (debt > threshold) {
                duration = durationFactory.determineIrrigationDuration(now);
            }
            if (duration > 0) {
                return new IrrigationEventImpl(now, duration, debt);
            }
        } catch (Throwable th) {
            logger.error(th);
        }
        return null;
    }
}
