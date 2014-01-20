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

import net.audumla.bean.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class AbstractEventTransaction implements EventTransaction {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEventTransaction.class);
    private EventTransactionStatus status = new DefaultEventStatus();
    private final EventScheduler eventScheduler;
    private boolean rollbackOnError = true;
    private boolean autoCommit = true;
    private String id = BeanUtils.generateName(this);
    private Map<Event, EventTarget> handledEvents = new HashMap<>();
    private Collection<EventTransactionListener> listeners = new HashSet<EventTransactionListener>();
    private Map<String[], Event[]> topicEventMap = new HashMap<>();
    private EventSchedule schedule;

    protected AbstractEventTransaction(EventScheduler scheduler, EventSchedule schedule) {
        this.eventScheduler = scheduler;
        this.schedule = schedule;
    }

    protected AbstractEventTransaction(EventScheduler scheduler) {
        this.eventScheduler = scheduler;
    }

    @Override
    public EventTransactionStatus getStatus() {
        return status;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public EventScheduler getEventScheduler() {
        return eventScheduler;
    }

    @Override
    public boolean rollback() throws Exception {
        return false;
    }

    @Override
    public void setRollbackOnError(boolean roe) {
        rollbackOnError = roe;
    }

    @Override
    public boolean getRollBackOnError() {
        return rollbackOnError;
    }


    @Override
    public void addTransactionListener(EventTransactionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTransactionListener(EventTransactionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void publishEvent(String topic, Event... events) throws Exception {
        validateState(EventState.PENDING);
        publishEvent(events, new String[]{topic});
    }

    @Override
    public void publishEvent(Event event, String... topics) throws Exception {
        validateState(EventState.PENDING);
        publishEvent(new Event[]{event}, topics);
    }

    @Override
    public void publishEvent(Event[] events, String[] topics) throws Exception {
        validateState(EventState.PENDING);
        topicEventMap.put(topics, events);
    }

    @Override
    public void setSchedule(EventSchedule schedule) throws Exception {
        validateState(EventState.PENDING);
        this.schedule = schedule;
    }

    @Override
    public EventSchedule getSchedule() {
        return schedule;
    }

    protected void commit() throws Exception {
        for (EventTransactionListener etl : listeners) {
            etl.onTransactionCommit(this,this.getHandledEvents() );
        }
    }

    protected boolean isAutoCommit() {
        return autoCommit;
    }

    protected void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    protected void addHandledEvent(EventTarget target, Event event) {
        handledEvents.put(event, target);
    }

    protected Collection<EventTransactionListener> getListeners() {
        return listeners;
    }

    protected Map<String[], Event[]> getTopicEventMap() {
        return topicEventMap;
    }

    protected void validateState(EventState state) throws Exception {
        if (getStatus().getState() != state) {
            throw new Exception("Operation cannot be performed when Transaction is "+getStatus().getState());
        }
    }

    public Map<? extends Event, ? extends EventTarget> getHandledEvents() {
        return handledEvents;
    }

}
