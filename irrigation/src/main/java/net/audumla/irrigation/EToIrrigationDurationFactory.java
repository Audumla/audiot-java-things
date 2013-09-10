/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 27/07/13
 * Time: 3:40 PM
 */

import org.apache.log4j.Logger;

import java.util.Date;

public class EToIrrigationDurationFactory implements IrrigationDurationFactory {
    private static final Logger logger = Logger.getLogger(EToIrrigationDurationFactory.class);
    private final Zone zone;
    private final EToCalculator etc;

    public EToIrrigationDurationFactory(Zone zone, EToCalculator etc) {
        this.zone = zone;
        this.etc = etc;
    }

    @Override
    public long determineIrrigationDuration(Date now) {
        return zone.calculateIrrigationDuration(etc.calculateETo(now));
    }
}
