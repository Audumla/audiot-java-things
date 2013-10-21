/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.automate;

import java.util.Date;

/**
 * User: audumla
 * Date: 23/07/13
 */
public interface EventFactory {
    /**
     * @return an irrigation event
     *         @param now The time that the event should be triggered
     *         Returns null if no event should be generated for the given time
     */
    Event generateEvent(Date now);

}
