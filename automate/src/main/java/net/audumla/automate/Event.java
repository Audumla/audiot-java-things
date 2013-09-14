/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.automate;

import java.util.Date;

public interface Event {
    /**
     * An enum representing each stage of an events lifecycle
     */
    public enum EventStatus {
        PENDING, EXECUTING, COMPLETE, FAILED
    }

    /**
     * @return The date and time that the event should or did start at
     */
    Date getEventStartTime();

    /**
     * @return The data and time that the event stopped. If the event has not yet run null will be returned
     */
    Date getEventEndTime();

    /**
     * @return The number of seconds that the event will or did run for
     */
    long getEventDuration();

    /**
     * @return The current status of the event in its lifecycle
     */
    EventStatus getStatus();

    /**
     * @param status Sets the status of the event to the given parameter
     */
    void setStatus(EventStatus status);

    /**
     * Applies a failure status to the event and stores the given failure reasons
     *
     * @param ex      The Exception that caused the failure if any
     * @param message A message that describes the failure
     */
    void setFailed(Throwable ex, String message);

    /**
     * @return The message associated with the event failure
     */
    String getFailureMessage();

    /**
     * @return The Exception associated with the event failure
     */
    Throwable getFailureException();

    String getName();

}
