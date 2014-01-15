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

public interface Event extends Cloneable {

    /**
     * @return The current status of the event in its lifecycle
     */
    EventStatus getStatus();

    /**
     * @return The name of the event
     */
    String getId();

    /**
     * @return the event scheduler that this event was scheduled to.
     * If the event has not been assigned to a scheduled yet then this will return null
     */
    EventScheduler getScheduler();

    /**
     *
     * @param scheduler the scheduler associated with this event
     */
    void setScheduler(EventScheduler scheduler);

    /**
     *
      * @return the transaction associated with this event. If the event has not been submitted to a scheduler then
     * this will return null
     */
    EventTransaction getEventTransaction();

    /**
     *
     * @param et the transaction that is associated with this events execution
     */
    void setEventTransaction(EventTransaction et);

    /**
     * Override protected clone method
     * @return a clone of the event
     */
    Event clone() throws CloneNotSupportedException;

}
