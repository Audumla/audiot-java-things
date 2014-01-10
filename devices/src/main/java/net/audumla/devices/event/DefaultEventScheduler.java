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
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultEventScheduler implements EventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventScheduler.class);
    private Map<String, EventTarget<Event>> targetRegistry = new HashMap<String, EventTarget<Event>>();

    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    @Override
    public boolean scheduleEvent(EventTarget target, Event... events) {
        EventTarget<Event> t = targetRegistry.get(target.getName());
        if (t != null) {
            scheduler.submit(wrapEvents(t, events));
            return true;
        }
        else {
            return false;
        }
    }

    protected Callable<Boolean> wrapEvents(EventTarget<Event> t, Event[] e) {
        return new Callable<Boolean>() {
            EventTarget<Event> target = t;
            Event[] events = e;

            @Override
            public Boolean call() throws Exception {
                boolean result = true;
                for (Event et : events ) {
                    try {
                        et.setScheduler(DefaultEventScheduler.this);
                        et.setExecutedTime(Instant.now());
                        et.setStatus(Event.EventStatus.EXECUTING);
                        result &= target.handleEvent(et);
                        et.setStatus(Event.EventStatus.COMPLETE);
                    } catch (Throwable throwable) {
                        et.setFailed(throwable, "Failed to execute Event");
                        et.setStatus(Event.EventStatus.FAILED);
                    }
                    et.setCompletedTime(Instant.now());
                }
                return result;
            }
        };

    }

    @Override
    public boolean scheduleEvent(EventTarget target, EventSchedule schedule, Event... events) {
        if (schedule instanceof SimpleEventSchedule) {
            SimpleEventSchedule ss = (SimpleEventSchedule) schedule;
            if (ss.getRepeatCount() > 0) {
                throw new UnsupportedOperationException("Scheduler does not support fixed repeat counts");
            }
            long initialDelay = Duration.between(Instant.now(),ss.getStartTime()).toMillis();
            initialDelay = initialDelay < 0 ? 0 : initialDelay;
            // set up a repeating schedule. We cannot set a fixed repeat count for this scheduler
            if (ss.getRepeatInterval() != null && !ss.getRepeatInterval().isZero() ) {
                ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
                    try {
                        wrapEvents(target,events).call();
                    } catch (Exception e) {
                        logger.error("Failed to execute scheduled task",e);
                    }
                }, initialDelay, ss.getRepeatInterval().toMillis(), MILLISECONDS);
            }
            else {
                scheduler.schedule(wrapEvents(target,events),initialDelay,MILLISECONDS);
            }
        }
        throw new UnsupportedOperationException("Scheduler does not support "+schedule.getClass());
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
