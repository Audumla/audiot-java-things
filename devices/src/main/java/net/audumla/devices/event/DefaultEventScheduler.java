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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultEventScheduler implements EventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventScheduler.class);
    private Map<String, EventTarget<Event>> targetRegistry = new HashMap<String, EventTarget<Event>>();

    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    protected Callable<Boolean> wrapEvents(EventTarget<Event>[] t, Event[] e) {
        return () -> {
            boolean result = true;
            // use the passed in targets as references and lookup the concrete target within the registry
            Collection<EventTarget<Event>> targets = new ArrayList<EventTarget<Event>>();
            for (EventTarget<Event> et : t) {
                EventTarget<Event> target = targetRegistry.get(et.getName());
                if (target != null) {
                    targets.add(target);
                } else {
                    logger.warn("Failed to locate target [" + et.getName() + "]");
                }
            }
            for (Event ev : e) {
                try {
                    ev.setScheduler(DefaultEventScheduler.this);
                    ev.setExecutedTime(Instant.now());
                    ev.setStatus(Event.EventStatus.EXECUTING);
                    for (EventTarget<Event> et : targets) {
                        result &= et.handleEvent(ev);
                    }
                    ev.setStatus(Event.EventStatus.COMPLETE);
                } catch (Throwable throwable) {
                    ev.setFailed(throwable, "Failed to execute Event");
                    ev.setStatus(Event.EventStatus.FAILED);
                }
                ev.setCompletedTime(Instant.now());
            }
            return result;
        };

    }

    @Override
    public boolean scheduleEvent(EventTarget target, EventSchedule schedule, Event... events) {
        return scheduleEvent(events,new EventTarget[] {target},schedule);
    }

    @Override
    public boolean scheduleEvent(EventTarget target, Event... events) {
        return scheduleEvent(events,new EventTarget[] {target});
    }


    @Override
    public boolean scheduleEvent(Event event, EventTarget... targets) {
        return scheduleEvent(new Event[] {event},targets);
    }

    @Override
    public boolean scheduleEvent(Event event, EventSchedule schedule, EventTarget... targets) {
        return scheduleEvent(new Event[] {event},targets,schedule);
    }

    @Override
    public boolean scheduleEvent(Event[] events, EventTarget[] targets, EventSchedule schedule) {
        if (schedule instanceof SimpleEventSchedule) {
            SimpleEventSchedule ss = (SimpleEventSchedule) schedule;
            if (ss.getRepeatCount() > 0) {
                throw new UnsupportedOperationException("Scheduler does not support fixed repeat counts");
            }
            long initialDelay = Duration.between(Instant.now(), ss.getStartTime()).toMillis();
            initialDelay = initialDelay < 0 ? 0 : initialDelay;
            // set up a repeating schedule. We cannot set a fixed repeat count for this scheduler
            if (ss.getRepeatInterval() != null && !ss.getRepeatInterval().isZero()) {
                ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
                    try {
                        wrapEvents(targets, events).call();
                    } catch (Exception e) {
                        logger.error("Failed to execute scheduled task", e);
                    }
                }, initialDelay, ss.getRepeatInterval().toMillis(), MILLISECONDS);
            } else {
                scheduler.schedule(wrapEvents(targets, events), initialDelay, MILLISECONDS);
            }
            return true;
        }
        throw new UnsupportedOperationException("Scheduler does not support " + schedule.getClass());
    }

    @Override
    public boolean scheduleEvent(Event[] events, EventTarget[] targets) {
        scheduler.submit(wrapEvents(targets, events));
        return true;
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
