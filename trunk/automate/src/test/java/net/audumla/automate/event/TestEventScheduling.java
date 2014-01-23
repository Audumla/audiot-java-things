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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class TestEventScheduling {
    private static final Logger logger = LoggerFactory.getLogger(TestEventScheduling.class);

    @After
    public void tearDown() throws Exception {
        EventScheduler.getDefaultEventScheduler().shutdown();
    }

    @Before
    public void setUp() throws Exception {
        EventScheduler.getDefaultEventScheduler().initialize();
    }

    @Test
    public void testWildCardTopic() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        EventScheduler.getDefaultEventScheduler().registerEventTarget(event -> {
            count.set(count.get() + 1);
        }, "event.*");
        EventScheduler.getDefaultEventScheduler().publishEvent("event.1", new AbstractEvent()).begin();
        EventScheduler.getDefaultEventScheduler().publishEvent("event.2", new AbstractEvent()).begin();
        EventScheduler.getDefaultEventScheduler().publishEvent("event1", new AbstractEvent()).begin();

        synchronized (this) {
            this.wait(1000);
        }

        assert count.get() == 2;
    }

    @Test
    public void testFailingTransactionModification() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        EventScheduler.getDefaultEventScheduler().registerEventTarget(event -> {
            synchronized (this) {
                count.set(count.get() + 1);
                this.wait(500);
            }
        }, "event.*");


        EventTransaction tr = EventScheduler.getDefaultEventScheduler().publishEvent("event.1", new AbstractEvent());
        tr.publishEvent("event.2", new AbstractEvent());
        tr.publishEvent("event1", new AbstractEvent());
        tr.begin();


        synchronized (this) {
            this.wait(200);
            try {
                tr.publishEvent("event.3", new AbstractEvent());
                assert false;
            } catch (Exception ex) {

            }
            this.wait(2000);
        }


        assert tr.getHandledEvents().size() == 2;
        assert tr.getStatus().getState() == EventState.COMPLETE;
        assert count.get() == 2;
    }
}
