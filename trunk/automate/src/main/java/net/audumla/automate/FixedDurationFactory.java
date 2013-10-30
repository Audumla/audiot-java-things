/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.automate;
/**
 * User: audumla
 * JulianDate: 30/07/13
 * Time: 7:10 PM
 */

import org.apache.log4j.Logger;

import java.util.Date;

public class FixedDurationFactory implements DurationFactory {
    private static final Logger logger = Logger.getLogger(FixedDurationFactory.class);
    private long seconds;

    public FixedDurationFactory(long seconds) {
        this.seconds = seconds;
    }

    public FixedDurationFactory() {
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    @Override
    public long determineDuration(Date now) {
        return seconds;
    }
}
