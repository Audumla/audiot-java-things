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

import java.util.concurrent.atomic.AtomicReference;

public interface EventScheduler {
    boolean scheduleEvent(EventTarget target, Event... events);

    boolean scheduleEvent(EventTarget target, EventSchedule schedule, Event... events);

    boolean registerEventTarget(EventTarget target);

    boolean shutdown();

    static AtomicReference<EventScheduler> scheduler = new AtomicReference<EventScheduler>();

    static void setEventScheduler(EventScheduler s) {
        if (scheduler.get() != null) {
            // if there is already a scheduler registered then we need to swap over to the new one.
            scheduler.get().shutdown();
            // need to transfer all registered handlers to the new scheduler
        }
        scheduler.set(s);
    }

    static EventScheduler getInstance() {
        if (scheduler.get() == null) {
            setEventScheduler(new DefaultEventScheduler());
        }
        return scheduler.get();
    }
}
