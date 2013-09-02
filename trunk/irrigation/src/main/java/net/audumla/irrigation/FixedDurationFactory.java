/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 27/07/13
 * Time: 3:59 PM
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class FixedDurationFactory implements IrrigationDurationFactory {
    private static final Logger logger = LogManager.getLogger(FixedDurationFactory.class);
    private final long duration;

    public FixedDurationFactory(long duration) {
        this.duration = duration;
    }

    @Override
    public long determineIrrigationDuration(Date now) {
        return duration;
    }
}
