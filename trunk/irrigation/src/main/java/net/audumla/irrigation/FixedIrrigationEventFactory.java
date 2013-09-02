/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;

import java.util.Date;

/**
 * User: audumla
 * Date: 23/07/13
 * Time: 8:42 AM
 */
public class FixedIrrigationEventFactory implements IrrigationEventFactory {
    private final IrrigationDurationFactory durationFactory;

    public FixedIrrigationEventFactory(IrrigationDurationFactory durationFactory) {
        this.durationFactory = durationFactory;
    }

    @Override
    public IrrigationEvent generateIrrigationEvent(Date now) {
        return new IrrigationEventImpl(now, durationFactory.determineIrrigationDuration(now), 0);
    }
}
