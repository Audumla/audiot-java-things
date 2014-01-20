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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractEventScheduler implements EventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEventScheduler.class);

    protected Map<Pattern, EventTarget> targetRegistry = new HashMap<>();

    protected abstract class SimpleEventTransaction extends AbstractEventTransaction {

        protected SimpleEventTransaction() {
            super(AbstractEventScheduler.this);
        }

        protected SimpleEventTransaction(EventSchedule schedule) {
            super(AbstractEventScheduler.this, schedule);
        }

        @Override
        public boolean rollback() {
            getStatus().setState(EventState.ROLLINGBACK);
            boolean result = true;
            Collection<EventState> transactionStates = new HashSet<>();
            for (Map.Entry<? extends Event, ? extends EventTarget> ev : getHandledEvents().entrySet()) {
                // only role back command events and events that actually completed their execution
                ev.getKey().getStatus().setState(EventState.ROLLINGBACK);
                try {
                    if (ev.getValue() instanceof RollbackEventTarget) {
                        RollbackEventTarget ret = (RollbackEventTarget) ev.getValue();
                        if (!ret.rollbackEvent(ev.getKey())) {
                            ev.getKey().getStatus().setFailed(null, "Event Handler Failed to roll back");
                            ev.getKey().getStatus().setState(EventState.FAILEDROLLBACK);
                        } else {
                            ev.getKey().getStatus().setState(EventState.ROLLEDBACK);
                        }
                    } else {
                        ev.getKey().getStatus().setFailed(null, "Event target does not handle roll back");
                        ev.getKey().getStatus().setState(EventState.FAILEDROLLBACK);
                    }
                } catch (Throwable th) {
                    ev.getKey().getStatus().setFailed(th, "Failed to roll back event");
                    ev.getKey().getStatus().setState(EventState.FAILEDROLLBACK);
                } finally {
                    transactionStates.add(ev.getKey().getStatus().getState());
                }
            }
            getStatus().setState(getRollbackStatus(transactionStates));
            return getStatus().getState() == EventState.ROLLEDBACK;
        }

        protected EventState getStatus(Collection<EventState> eventStates) {
            return eventStates.isEmpty() ? EventState.NOIDENTIFIEDTARGETS :
                    eventStates.contains(EventState.COMPLETE) ?
                            eventStates.size() == 1 ? EventState.COMPLETE :
                                    EventState.PARTIALLYCOMPLETE : EventState.FAILED;
        }

        protected EventState getRollbackStatus(Collection<EventState> eventStates) {
            return eventStates.isEmpty() ? EventState.FAILEDROLLBACK :
                    eventStates.contains(EventState.ROLLEDBACK) ?
                            eventStates.size() == 1 ? EventState.ROLLEDBACK :
                                    EventState.PARTIALLYROLLEDBACK : EventState.FAILEDROLLBACK;
        }

        protected Runnable toRunnable() {
            return () -> {
                getStatus().setExecutedTime(Instant.now());
                getStatus().setState(EventState.EXECUTING);
                Collection<EventState> transactionStates = new HashSet<>();
                for (Map.Entry<String[], Event[]> mapItem : getTopicEventMap().entrySet()) {
                    for (Event ev : mapItem.getValue()) {
                        Collection<EventState> eventStates = new HashSet<>();
                        ev.setScheduler(AbstractEventScheduler.this);
                        ev.getStatus().setExecutedTime(Instant.now());
                        for (EventTarget et : getMappedTargets(mapItem.getKey(), EventTarget.class)) {
                            // default the attempted cloned event to the actual event. This allows us to update the event correctly in the case of
                            // a clone failure.
                            Event nev = ev;
                            try {
                                // clone the original event so that we can keep track of each status for each handler
                                nev = ev.clone();
                                addHandledEvent(et, nev);
                                if (!et.handleEvent(nev)) {
                                    throw new Exception("Failed to execute Event");
                                }
                                nev.getStatus().setState(EventState.COMPLETE);
                            } catch (CloneNotSupportedException ex) {
                                nev.getStatus().setFailed(ex, "Failed to clone Event");
                                // add the event to the handled list as this would not have been called otherwise
                                addHandledEvent(et, nev);
                            } catch (Throwable th) {
                                nev.getStatus().setFailed(th, "Failed to execute Event");
                            } finally {
                                nev.getStatus().setCompletedTime(Instant.now());
                                eventStates.add(nev.getStatus().getState());
                                transactionStates.add(nev.getStatus().getState());
                            }
                        }
                        // update the original event as there may be references to it that can monitor the state of the overall event
                        ev.getStatus().setCompletedTime(Instant.now());
                        ev.getStatus().setState(getStatus(eventStates));
                    }
                }
                getStatus().setCompletedTime(Instant.now());
                getStatus().setState(getStatus(transactionStates));

                if (!getStatus().getState().equals(EventState.NOIDENTIFIEDTARGETS)) {
                    if (getStatus().getState().equals(EventState.COMPLETE)) {
                        if (isAutoCommit()) {
                            try {
                                commit();
                            } catch (Exception e) {
                                logger.error("Transaction commit failed", e);
                                if (getRollBackOnError()) {
                                    rollback();
                                }
                            }
                        }
                    } else {
                        if (getRollBackOnError()) {
                            rollback();
                        }
                    }
                }
            };
        }

    }

    public AbstractEventScheduler() {
        initialize();
    }

    @Override
    public EventTransaction scheduleEvent(EventSchedule schedule, String topic, Event... events) throws Exception {
        return scheduleEvent(schedule, events, new String[]{topic.toString()});
    }

    @Override
    public EventTransaction publishEvent(String topic, Event... events) throws Exception {
        return publishEvent(events, new String[]{topic.toString()});
    }

    @Override
    public EventTransaction publishEvent(Event event, String... topics) throws Exception {
        return publishEvent(new Event[]{event}, topics.length == 0 ? new String[]{event.getName()} : topics);
    }

    @Override
    public EventTransaction scheduleEvent(EventSchedule schedule, Event event, String... topics) throws Exception {
        return scheduleEvent(schedule, new Event[]{event}, topics);
    }

    @Override
    public EventTransaction scheduleEvent(EventSchedule schedule, Event[] events, String[] topics) throws Exception {
        EventTransaction et = createTransaction(schedule);
        et.publishEvent(events, topics);
        return et;
    }

    @Override
    public EventTransaction publishEvent(Event[] events, String[] topics) throws Exception {
        EventTransaction et = createTransaction();
        et.publishEvent(events, topics);
        return et;
    }

    @Override
    public boolean registerEventTarget(EventTarget target) {
        registerEventTarget(target, target.getName());
        return true;
    }

    @Override
    public boolean registerEventTarget(EventTarget target, String[] topics) {
        for (String topic : topics) {
            registerEventTarget(target, topic);
        }
        return true;
    }

    @Override
    public boolean registerEventTarget(EventTarget target, String topic) {
        topic = topic.replaceAll("\\.", "\\\\.");
        if (topic.endsWith("*")) {
            topic = topic.substring(0, topic.length() - 1).replaceAll("\\*", "[^.]+") + ".*";
        } else {
            topic = topic.replaceAll("\\*", "[^.]+");
        }

        Pattern pattern = Pattern.compile(topic);
        targetRegistry.put(pattern, target);
        target.setScheduler(this);
        return true;
    }

    @Override
    public boolean unregisterEventTarget(EventTarget target) {
        for (Map.Entry<Pattern, EventTarget> e : targetRegistry.entrySet()) {
            if (e.getValue().getName().equals(target.getName())) {
                targetRegistry.remove(e.getKey());
            }
        }
        return true;
    }

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean shutdown() {
        targetRegistry.clear();
        return true;
    }

    protected <T extends EventTarget, String> Collection<T> getMappedTargets(String[] topics, Class<T> targetBase) {
        Collection<T> targets = new HashSet<T>();
        for (String topic : topics) {
            for (Map.Entry<Pattern, EventTarget> e : targetRegistry.entrySet()) {
                if (targetBase.isAssignableFrom(e.getValue().getClass()) && e.getKey().matcher(topic.toString()).matches()) {
                    targets.add((T) e.getValue());
                }
            }
        }
        return targets;
    }

}
