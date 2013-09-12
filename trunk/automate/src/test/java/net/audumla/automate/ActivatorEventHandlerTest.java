package net.audumla.automate;

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
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.devices.activator.ActivatorMock;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class ActivatorEventHandlerTest {

    @Before
    public void setUp() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
    }

    @Test
    public void testSuccessfulEvent() throws Exception {
        ActivatorEventHandler handler = new ActivatorEventHandler();
        handler.setActivator(new ActivatorMock(true, true));

        DefaultEventFactory eventFactory = new DefaultEventFactory();
        FixedDurationFactory durationFactory = new FixedDurationFactory();
        durationFactory.setSeconds(2);
        eventFactory.setDurationFactory(durationFactory);
        Event event = eventFactory.generateEvent(new Date());

        assert event.getStatus() == Event.EventStatus.PENDING;
        handler.handleEvent(event);
        assert event.getStatus() == Event.EventStatus.EXECUTING;
        synchronized (this) {
            this.wait(1000);
            assert event.getStatus() == Event.EventStatus.EXECUTING;
            this.wait(2100);
            assert event.getStatus() == Event.EventStatus.COMPLETE;
        }
    }


}
