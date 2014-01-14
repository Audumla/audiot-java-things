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
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultEventScheduler implements EventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventScheduler.class);

    protected Map<Pattern, EventTarget> targetRegistry = new HashMap<>();
    protected ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

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
                }
                throw new UnsupportedOperationException("Scheduler does not support " + schedule.getClass());
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
            for (Event ev : getEvents()) {
                // only role back command events and events that actually completed their execution
                if (ev.getStatus().getState().equals(EventState.COMPLETE) && ev instanceof CommandEvent) {
                    CommandEvent cev = (CommandEvent) ev;
                    cev.getStatus().setState(EventState.ROLLINGBACK);
                    try {
                        for (CommandEventTarget cet : getMappedTargets(topics, CommandEventTarget.class)) {
                            cet.rollbackEvent(cev);
                        }
                        cev.getStatus().setState(EventState.ROLLEDBACK);
                    } catch (Throwable throwable) {
                        logger.error("Failed to roll back Event [" + ev.getId() + "]", throwable);
                        result = false;
                    }
                }
            }
            getStatus().setState(EventState.ROLLEDBACK);
            return result;
        }

        protected Runnable toRunnable() {
            return () -> {
                boolean result = true;
                getStatus().setExecutedTime(Instant.now());
                getStatus().setState(EventState.EXECUTING);
                for (Event ev : getEvents()) {
                    try {
                        ev.setScheduler(DefaultEventScheduler.this);
                        ev.getStatus().setExecutedTime(Instant.now());
                        ev.getStatus().setState(EventState.EXECUTING);
                        for (EventTarget et : getMappedTargets(topics, EventTarget.class)) {
                            result &= et.handleEvent(ev);
                        }
                        ev.getStatus().setState(EventState.COMPLETE);
                    } catch (Throwable throwable) {
                        ev.getStatus().setFailed(throwable, "Failed to execute Event");
                        result = false;
                    }
                    ev.getStatus().setCompletedTime(Instant.now());
                }
                getStatus().setCompletedTime(Instant.now());
                if (result) {
                    getStatus().setState(EventState.COMPLETE);
                    if (isAutoCommit()) {
                        commit();
                    }
                } else {
                    if (getRollBackOnError()) {
                        rollback();
                    } else {
                        getStatus().setState(EventState.FAILED);
                    }
                }
            };

        }
    }


    @Override
    public  EventTransaction scheduleEvent(String topic, EventSchedule schedule, Event... events) {
        return scheduleEvent(events, new String[]{topic.toString()}, schedule);
    }

    @Override
    public  EventTransaction scheduleEvent(String topic, Event... events) {
        return scheduleEvent(events, new String[]{topic.toString()});
    }

    @Override
    public  EventTransaction scheduleEvent(Event event, String... topics) {
        return scheduleEvent(new Event[]{event}, topics);
    }

    @Override
    public  EventTransaction scheduleEvent(Event event, EventSchedule schedule, String... topics) {
        return scheduleEvent(new Event[]{event}, topics, schedule);
    }

    @Override
    public  EventTransaction scheduleEvent(Event[] events, String[] topics, EventSchedule schedule) {
        return new DefaultEventTransaction(topics, events, schedule);
    }

    @Override
    public  EventTransaction scheduleEvent(Event[] events, String[] topics) {
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
    public  boolean registerEventTarget(String topic, EventTarget target) {
        Pattern pattern = Pattern.compile(topic.toString().replaceAll("\\.", "\\\\.").replaceAll("\\*", "[^.]+"));
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
    public boolean shutdown() {
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
