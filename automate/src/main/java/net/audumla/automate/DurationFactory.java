/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.automate;

import java.util.Date;

/**
 * User: audumla
 * Date: 23/07/13
 * Time: 8:41 AM
 */
public interface DurationFactory {

    /**
     * @return the irrigation duration in seconds
     */
    long determineDuration(Date now);

}
