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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class TestEventScheduling {
    private static final Logger logger = LoggerFactory.getLogger(TestEventScheduling.class);

    @Test
    public void testMultiEventOnSingleTopicWithSeperatelyExecutedTransactions() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        ThreadPoolEventScheduler scheduler = new ThreadPoolEventScheduler();
        scheduler.registerEventTarget(event -> {
            count.set(count.get() + 1);
        }, "event.*");
        scheduler.publishEvent("event.1", new AbstractEvent()).begin();
        scheduler.publishEvent("event.1", new AbstractEvent()).begin();
        scheduler.publishEvent("event.1", new AbstractEvent()).begin();
        scheduler.publishEvent("event1", new AbstractEvent()).begin();
        scheduler.publishEvent("bla", new AbstractEvent()).begin();

        synchronized (this) {
            this.wait(1000);
        }

        assert count.get() == 3;
    }

    @Test
    public void testMultiEventOnSingleTopicWithSingleAtomicTransaction() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        ThreadPoolEventScheduler scheduler = new ThreadPoolEventScheduler();
        scheduler.registerEventTarget(new EventTarget<ValueEvent>() {
            int lastValue = 0;
            @Override
            public void handleEvent(ValueEvent event) throws Throwable {
                assert lastValue < event.value;
                lastValue = event.value;
                count.set(count.get() + 1);
            }
        }, "event.*");
        EventTransaction tr = scheduler.publishEvent("event.1", new ValueEvent(1), new ValueEvent(2), new ValueEvent(3));
        tr.publishEvent("event1", new ValueEvent(1), new ValueEvent(1), new ValueEvent(1));
        tr.begin();

        synchronized (this) {
            this.wait(1000);
        }

        assert count.get() == 3;
    }

    @Test
    public void testMultiEventOnSingleTopicWithSingleTransaction() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        ThreadPoolEventScheduler scheduler = new ThreadPoolEventScheduler();
        scheduler.registerEventTarget(new EventTarget<ValueEvent>() {
            int lastValue = 0;
            @Override
            public void handleEvent(ValueEvent event) throws Throwable {
                assert lastValue < event.value;
                lastValue = event.value;
                count.set(count.get() + 1);
            }
        }, "event.*");
        EventTransaction tr = scheduler.publishEvent("event.1", new ValueEvent(1));
        tr.publishEvent("event.1", new ValueEvent(2));
        tr.publishEvent("event.1", new ValueEvent(3));
        tr.publishEvent("event1", new ValueEvent(0));
        tr.begin();

        synchronized (this) {
            this.wait(1000);
        }

        assert count.get() == 3;
    }


    @Test
    public void testWildCardTopic() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        ThreadPoolEventScheduler scheduler = new ThreadPoolEventScheduler();
        scheduler.registerEventTarget(event -> {
            count.set(count.get() + 1);
        }, "event.*");
        scheduler.publishEvent("event.1", new AbstractEvent()).begin();
        scheduler.publishEvent("event.2", new AbstractEvent()).begin();
        scheduler.publishEvent("event1", new AbstractEvent()).begin();

        synchronized (this) {
            this.wait(1000);
        }

        assert count.get() == 2;
    }

    @Test
    public void testFailingTransactionModification() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        ThreadPoolEventScheduler scheduler = new ThreadPoolEventScheduler();
        scheduler.registerEventTarget(event -> {
            synchronized (this) {
                count.set(count.get() + 1);
                this.wait(300);
            }
        }, "event.*");


        EventTransaction tr = scheduler.publishEvent("event.1", new AbstractEvent());
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
            this.wait(1000);
        }


        assert tr.getHandledEvents().size() == 2;
        assert tr.getStatus().getState() == EventState.COMPLETE;
        assert count.get() == 2;
    }
}
