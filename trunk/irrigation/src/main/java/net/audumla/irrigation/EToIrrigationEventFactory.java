/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;

import net.audumla.automate.DefaultEvent;
import net.audumla.automate.DurationFactory;
import net.audumla.automate.Event;
import net.audumla.automate.EventFactory;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * User: audumla
 * JulianDate: 21/07/13
 * Time: 6:54 PM
 */
public class EToIrrigationEventFactory implements EventFactory {
    private static final Logger logger = Logger.getLogger(EToIrrigationEventFactory.class);
    private final DurationFactory durationFactory;
    private final EToCalculator etc;
    private double threshold = 0.0;
    private Zone zone;


    public EToIrrigationEventFactory(Zone zone, EToCalculator etc, DurationFactory durationFactory) {
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
    public Event generateEvent(Date now) {
        try {
            long duration = 0;
            double debt = etc.calculateETo(now); //mm
            if (debt > threshold) {
                duration = durationFactory.determineDuration(now);
            }
            if (duration > 0) {
                return new DefaultEvent(now, duration);
            }
        } catch (Throwable th) {
            logger.error(th);
        }
        return null;
    }
}
