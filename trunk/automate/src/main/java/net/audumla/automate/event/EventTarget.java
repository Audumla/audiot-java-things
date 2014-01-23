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

public interface EventTarget<T extends Event> {

    /**
     * @return the name of the event target. This can sometimes be used as the topic for scheduling
     */
    default String getName() {
        return BeanUtils.generateName(this);
    }

    /**
     * @param event the event that should be handled or executed
     * @throws Throwable
     */
    void handleEvent(T event) throws Throwable;

    /**
     * @return the scheduler that this target has been registered with
     */
    default EventScheduler getScheduler() {
        return EventScheduler.getDefaultEventScheduler();
    }

    /**
     * @param scheduler the scheduler that this target has been registered with
     */
    default void setScheduler(EventScheduler scheduler) {
    }

}
