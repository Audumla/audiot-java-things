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

import net.audumla.bean.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractEventTransaction implements EventTransaction {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEventTransaction.class);
    protected EventTransactionStatus status = new DefaultEventStatus();
    private EventScheduler eventScheduler;
    private boolean rollbackOnError = true;
    private boolean autoCommit = true;
    private String id = BeanUtils.generateName(this);
    protected String[] topics;
    protected Event[] events;

    protected AbstractEventTransaction() {
    }

    protected AbstractEventTransaction(String[] topics, Event[] events, EventScheduler scheduler) {
        this.topics = topics;
        this.events = events;
        this.eventScheduler = scheduler;
        for (Event ev : events) {
            ev.setEventTransaction(this);
        }
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
    public void setEventScheduler(EventScheduler eventScheduler) {
        this.eventScheduler = eventScheduler;
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
    public Collection<Event> getEvents() {
        return Arrays.asList(events);
    }

    @Override
    public Collection<String> getTopics() {
        return Arrays.asList(topics);
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

}
