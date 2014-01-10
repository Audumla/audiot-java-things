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

public class EventTargetReference implements EventTarget {
    private static final Logger logger = LoggerFactory.getLogger(EventTargetReference.class);

    private String name;
    private EventTarget target;

    public EventTargetReference(String name) {
        this.name = name;
    }

    public EventTargetReference(String name, EventTarget target) {
        this.name = name;
        this.target = target;
    }

    public EventTarget getTarget() {
        return target;
    }

    public void setTarget(EventTarget target) {
        this.target = target;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean handleEvent(Event event) throws Throwable {
        return target != null && target.handleEvent(event);
    }
}
