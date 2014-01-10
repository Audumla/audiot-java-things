package net.audumla.devices.event;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import java.util.Date;

public interface Event extends Cloneable {
    /**
     * An enum representing each stage of an events lifecycle
     */
    public enum EventStatus {
        PENDING, EXECUTING, COMPLETE, FAILED
    }

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

    /**
     * @return The name of the event
     */
    String getName();


    /**
     * @return The time that the event was executed
     */
    Date getExecutedTime();

    /**
     * @return The time that the event completed execution or failed
     */
    Date getCompletedTime();

    /**
     * @param executedTime The time that the event started execution
     */
    void setExecutedTime(Date executedTime);

    /**
     * @param completedTime The time that the event completed execution or failed
     */
    void setCompletedTime(Date completedTime);

    /**
     * @return the event scheduler that this event was scheduled to.
     * If the event has not been assigned to a scheduled yet then this will return null
     */
    EventScheduler getScheduler();

}
