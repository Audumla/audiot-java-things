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

import net.audumla.collections.Pair;

import java.util.Collection;
import java.util.Map;

public interface EventTransaction {

    /**
     *
     * @param schedule the schedule that this transaction will run on
     */
    void setSchedule(EventSchedule schedule) throws Exception;

    /**
     *
     * @return the schedule that this transaction is running on
     */
    EventSchedule getSchedule();

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
    void begin() throws Exception;

    /**
     * commits the transaction. The exact nature of the commit needs to be handled by implementing events as there is no
     * understanding of the underlying event implementations within the actual transaction.
     *
     * @throws Exception thrown if the commit fails. This may result in a roll back.
     */
//    void commit() throws Exception;


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
     * The handled events represents the list of events that have been handled by an event handler. Each event
     * is matched to the target that handled it.
     * This list may include multiple copies of the original events depending on how many handlers were matched to handle
     * the event. Each instance may have a different status and error messages. It is therefore preferable to
     * use this method to get the actual state of the events executed by this transaction
     *
     * @return the events that have been handled by this transaction
     */
    Collection<Pair<EventTarget,Event>> getHandledEvents();

    /**
     *
     * @param listener the listener to be notified of events for this transaction
     */
    void addTransactionListener(EventTransactionListener listener);

    /**
     *
     * @param listener the listener to be removed
     */
    void removeTransactionListener(EventTransactionListener listener);

    /**
     *
     * @param topic the topic to receive the events
     * @param events the events to be published on the specified topic
     */
    void publishEvent(String topic, Event... events) throws Exception;

    /**
     *
     * @param topics the topics to receive the event
     * @param event the event to be published on the specified topics
     */
    void publishEvent(Event event, String... topics) throws Exception;

    /**
     *
     * @param topics the topics to receive the events
     * @param events the events to be published on the specified topics
     */
    void publishEvent(Event[] events, String[] topics) throws Exception;


}
