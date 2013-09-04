/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 30/07/13
 * Time: 7:10 PM
 */

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Date;

public class FixedIrrigationDurationFactory implements IrrigationDurationFactory {
    private static final Logger logger = LogManager.getLogger(FixedIrrigationDurationFactory.class);
    private final long seconds;

    public FixedIrrigationDurationFactory(long seconds) {
        this.seconds = seconds;
    }

    @Override
    public long determineIrrigationDuration(Date now) {
        return seconds;
    }
}
