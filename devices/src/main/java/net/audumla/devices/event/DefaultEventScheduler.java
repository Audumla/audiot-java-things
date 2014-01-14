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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultEventScheduler implements EventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventScheduler.class);

    protected Map<String, EventTarget<Event>> targetRegistry = new HashMap<String, EventTarget<Event>>();
    protected ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    public class DefaultEventTransaction extends AbstractEventTransaction {

        private EventSchedule schedule;
        private Future<?> future;

        private DefaultEventTransaction(EventTarget[] targets, Event[] events, EventSchedule schedule) {
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
        public void commit() throws Exception {

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
                        for (EventTarget<Event> et : getTargets()) {
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
                getStatus().setState(result ? EventState.COMPLETE : EventState.FAILED);
            };

        }
    }



    @Override
    public EventTransaction scheduleEvent(EventTarget target, EventSchedule schedule, Event... events) {
        return scheduleEvent(events, new EventTarget[]{target}, schedule);
    }

    @Override
    public EventTransaction scheduleEvent(EventTarget target, Event... events) {
        return scheduleEvent(events, new EventTarget[]{target});
    }


    @Override
    public EventTransaction scheduleEvent(Event event, EventTarget... targets) {
        return scheduleEvent(new Event[]{event}, targets);
    }

    @Override
    public EventTransaction scheduleEvent(Event event, EventSchedule schedule, EventTarget... targets) {
        return scheduleEvent(new Event[]{event}, targets, schedule);
    }

    @Override
    public EventTransaction scheduleEvent(Event[] events, EventTarget[] targets, EventSchedule schedule) {
        return new DefaultEventTransaction(targets, events, schedule);
    }

    @Override
    public EventTransaction scheduleEvent(Event[] events, EventTarget[] targets) {
        return new DefaultEventTransaction(targets, events, null);
    }

    @Override
    public boolean registerEventTarget(EventTarget target) {
        targetRegistry.put(target.getName(), target);
        return true;
    }

    @Override
    public boolean shutdown() {
        scheduler.shutdown();
        return true;
    }
}
