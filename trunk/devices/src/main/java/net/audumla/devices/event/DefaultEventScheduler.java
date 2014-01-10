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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
                    result &= target.handleEvent(et);
                }
                return result;
            }
        };

    }

    @Override
    public boolean scheduleEvent(EventTarget target, EventSchedule schedule, Event... events) {
        return false;
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
