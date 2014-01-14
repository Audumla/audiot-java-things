package net.audumla.automate.event;

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

import java.time.Instant;

public interface EventTransactionStatus extends Cloneable {

    /**
     * @return The current status of the event in its lifecycle
     */
    EventState getState();

    /**
     * @param status Sets the status of the event to the given parameter
     */
    void setState(EventState status);

    /**
     * @return The time that the event was executed
     */
    Instant getExecutedTime();

    /**
     * @return The time that the event completed execution or failed
     */
    Instant getCompletedTime();

    /**
     * @param executedTime The time that the event started execution
     */
    void setExecutedTime(Instant executedTime);

    /**
     * @param completedTime The time that the event completed execution or failed
     */
    void setCompletedTime(Instant completedTime);

}
