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

import java.util.Collection;

public interface EventTransaction {
    /**
     * @return The current status of the event in its lifecycle
     */
    EventTransactionStatus getStatus();

    /**
     * @return The name of the event
     */
    String getId();

    /**
     * @return the event scheduler that this event was scheduled to.
     * If the event has not been assigned to a scheduled yet then this will return null
     */
    EventScheduler getEventScheduler();

    /**
     * begins the transaction
     */
    boolean begin() throws Exception;

    /**
     * commits the transaction. The exact nature of the commit needs to be handled by implementing events as there is no
     * understanding of the underlying event implementations within the actual transaction.
     *
     * @throws Exception thrown if the commit fails. This may result in a roll back.
     */
    void commit() throws Exception;


    /**
     * Attempts to roll back each event that is part of the transaction
     *
     * @throws Exception thrown if the transaction cannot be rolled back
     */
    boolean rollback() throws Exception;

    /**
     * Sets whether the transaction is automatically rolled back when an error is encountered
     */
    void setRollbackOnError(boolean roe);

    /**
     * Gets whether the transaction is automatically rolled back when an error is encountered
     */
    boolean getRollBackOnError();

    /**
     * This original list of events passed into this transaction may not represent the actual executed events as
     * each event handler will be given its own instance. This may result in more events being handled than actually
     * passed into the transaction initially. To get the list of events that were actually handled by the transaction
     * use getHandledEvents
     *
     * @return the events originally associated with this transaction
     */
    Collection<Event> getEvents();

    /**
     * The handled events represents the list of events that have been passed to an event handler. This list
     * may include multiple copies of the original events depending on how many handlers were matched to handle
     * the event. Each instance may have a different status and error messages. It is therefore preferable to
     * use this method to get the actual state of the events executed by this transaction
     *
     * @return the events that have been handled by this transaction
     */
    Collection<Event> getHandledEvents();


    /**
     *
     * @return the topics associated with this transaction
     */
    Collection getTopics();
}
