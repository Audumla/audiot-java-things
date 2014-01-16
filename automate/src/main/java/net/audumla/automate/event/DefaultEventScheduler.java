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

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultEventScheduler implements EventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventScheduler.class);

    protected Map<Pattern, EventTarget> targetRegistry = new HashMap<>();
    protected ScheduledExecutorService scheduler;

    public class DefaultEventTransaction extends AbstractEventTransaction {

        private EventSchedule schedule;
        private Future<?> future;

        private DefaultEventTransaction(String[] targets, Event[] events, EventSchedule schedule) {
            super(targets, events, DefaultEventScheduler.this);
            this.schedule = schedule;
        }

        @Override
        public boolean begin() {
            if (schedule == null) {
                future = scheduler.submit(toRunnable());
            } else {
                if (schedule instanceof SimpleEventSchedule) {
                    SimpleEventSchedule ss = (SimpleEventSchedule) schedule;
                    if (ss.getRepeatCount() > 0) {
                        throw new UnsupportedOperationException("Scheduler does not support fixed repeat counts");
                    }
                    long initialDelay = Duration.between(Instant.now(), ss.getStartTime()).toMillis();
                    initialDelay = initialDelay < 0 ? 0 : initialDelay;
                    // set up a repeating schedule. We cannot set a fixed repeat count for this scheduler
                    if (ss.getRepeatInterval() != null && !ss.getRepeatInterval().isZero()) {
                        future = scheduler.scheduleAtFixedRate(toRunnable(), initialDelay, ss.getRepeatInterval().toMillis(), MILLISECONDS);
                    } else {
                        future = scheduler.schedule(toRunnable(), initialDelay, MILLISECONDS);
                    }
                } else {
                     throw new UnsupportedOperationException("Scheduler does not support " + schedule.getClass());
                }
            }
            return true;
        }

        @Override
        public void commit() {
            // call all listeners or committers and manage rollback if the commit fails
        }

        @Override
        public boolean rollback() {
            getStatus().setState(EventState.ROLLINGBACK);
            boolean result = true;
            Collection<EventState> transactionStates = new HashSet<>();
            for (Map.Entry<Event, EventTarget> ev : getHandledEventMap().entrySet()) {
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
                for (Event ev : getEvents()) {
                    Collection<EventState> eventStates = new HashSet<>();
                    ev.setScheduler(DefaultEventScheduler.this);
                    ev.getStatus().setExecutedTime(Instant.now());
                    for (EventTarget et : getMappedTargets(topics, EventTarget.class)) {
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
                getStatus().setCompletedTime(Instant.now());
                getStatus().setState(getStatus(transactionStates));

                if (!getStatus().getState().equals(EventState.NOIDENTIFIEDTARGETS)) {
                    if (getStatus().getState().equals(EventState.COMPLETE)) {
                        if (isAutoCommit()) {
                            commit();
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

    public DefaultEventScheduler() {
        initialize();
    }

    @Override
    public EventTransaction scheduleEvent(String topic, EventSchedule schedule, Event... events) {
        return scheduleEvent(events, new String[]{topic.toString()}, schedule);
    }

    @Override
    public EventTransaction scheduleEvent(String topic, Event... events) {
        return scheduleEvent(events, new String[]{topic.toString()});
    }

    @Override
    public EventTransaction scheduleEvent(Event event, String... topics) {
        return scheduleEvent(new Event[]{event}, topics);
    }

    @Override
    public EventTransaction scheduleEvent(Event event, EventSchedule schedule, String... topics) {
        return scheduleEvent(new Event[]{event}, topics, schedule);
    }

    @Override
    public EventTransaction scheduleEvent(Event[] events, String[] topics, EventSchedule schedule) {
        return new DefaultEventTransaction(topics, events, schedule);
    }

    @Override
    public EventTransaction scheduleEvent(Event[] events, String[] topics) {
        return new DefaultEventTransaction(topics, events, null);
    }

    @Override
    public boolean registerEventTarget(EventTarget target) {
        registerEventTarget(target.getName(), target);
        return true;
    }

    @Override
    public boolean registerEventTarget(String[] topics, EventTarget target) {
        for (String topic : topics) {
            registerEventTarget(topic, target);
        }
        return true;
    }

    @Override
    public boolean registerEventTarget(String topic, EventTarget target) {
        topic = topic.replaceAll("\\.", "\\\\.");
        if (topic.endsWith("*")) {
            topic = topic.substring(0, topic.length() - 1).replaceAll("\\*", "[^.]+") + ".*";
        } else {
            topic = topic.replaceAll("\\*", "[^.]+");
        }

        Pattern pattern = Pattern.compile(topic);
        targetRegistry.put(pattern, target);
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
        scheduler = new ScheduledThreadPoolExecutor(1);
        return true;
    }

    @Override
    public boolean shutdown() {
        targetRegistry.clear();
        scheduler.shutdown();
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
