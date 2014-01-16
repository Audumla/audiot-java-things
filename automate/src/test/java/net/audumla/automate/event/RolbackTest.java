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

import java.time.Instant;

public class RolbackTest {
    private static final Logger logger = LoggerFactory.getLogger(RolbackTest.class);


    public static class SimpleRollbackHandler extends AbstractEventTarget implements RollbackEventTarget {

        private boolean event = true;
        private boolean rollback = true;
        private boolean eventException = false;
        private boolean rollbackException = false;

        public SimpleRollbackHandler(boolean event, boolean rollback, boolean eventException, boolean rollbackException) {
            this.event = event;
            this.rollback = rollback;
            this.eventException = eventException;
            this.rollbackException = rollbackException;
        }

        @Override
        public boolean handleEvent(Event event) throws Throwable {
            if (eventException) {
                throw new Exception();
            }
            return this.event;
        }

        @Override
        public boolean rollbackEvent(Event event) throws Throwable {
            if (rollbackException) {
                throw new Exception();
            }
            return this.rollback;
        }
    }

    @After
    public void tearDown() throws Exception {
        EventScheduler.getDefaultEventScheduler().shutdown();
    }

    @Before
    public void setUp() throws Exception {
        EventScheduler.getDefaultEventScheduler().initialize();
    }

    @Test
    public void testRollbackTrue() throws Exception {
        SimpleRollbackHandler handler = new SimpleRollbackHandler(false, true, false, false);
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*", handler);
        EventTransaction transaction = EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), "event.1");
        assert transaction.getStatus().getState() == EventState.PENDING;
        assert transaction.getHandledEvents().size() == 0;
        assert transaction.getEvents().size() == 1;
        transaction.begin();

        synchronized (this) {
            this.wait(500);
        }

        assert transaction.getHandledEvents().size() == 1;
        assert transaction.getEvents().size() == 1;
        transaction.getEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.FAILED;
        });
        transaction.getHandledEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.ROLLEDBACK;
        });
        assert transaction.getStatus().getState() == EventState.ROLLEDBACK;

    }

    @Test
    public void testRollbackException() throws Exception {
        SimpleRollbackHandler handler = new SimpleRollbackHandler(false, true, true, false);
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*", handler);
        EventTransaction transaction = EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), "event.1");
        assert transaction.getStatus().getState() == EventState.PENDING;
        assert transaction.getHandledEvents().size() == 0;
        assert transaction.getEvents().size() == 1;
        transaction.begin();

        synchronized (this) {
            this.wait(500);
        }

        assert transaction.getHandledEvents().size() == 1;
        assert transaction.getEvents().size() == 1;
        transaction.getEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.FAILED;
        });
        transaction.getHandledEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.ROLLEDBACK;
        });
        assert transaction.getStatus().getState() == EventState.ROLLEDBACK;

    }

    @Test
    public void testRollbackFailException() throws Exception {
        SimpleRollbackHandler handler = new SimpleRollbackHandler(false, true, true, true);
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*", handler);
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.handle.bla.*", handler);
        EventTransaction transaction = EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), "event.1");
        assert transaction.getStatus().getState() == EventState.PENDING;
        assert transaction.getHandledEvents().size() == 0;
        assert transaction.getEvents().size() == 1;
        transaction.begin();

        synchronized (this) {
            this.wait(500);
        }

        assert transaction.getHandledEvents().size() == 1;
        assert transaction.getEvents().size() == 1;
        transaction.getEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.FAILED;
        });
        transaction.getHandledEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.FAILEDROLLBACK;
        });
        assert transaction.getStatus().getState() == EventState.FAILEDROLLBACK;

    }

    @Test
    public void testRollbackFail() throws Exception {
        SimpleRollbackHandler handler = new SimpleRollbackHandler(false, false, false, false);
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*", handler);
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.4", handler);
        EventTransaction transaction = EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), "event.2");
        assert transaction.getStatus().getState() == EventState.PENDING;
        assert transaction.getHandledEvents().size() == 0;
        assert transaction.getEvents().size() == 1;
        transaction.begin();

        synchronized (this) {
            this.wait(500);
        }

        assert transaction.getHandledEvents().size() == 1;
        assert transaction.getEvents().size() == 1;
        transaction.getEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.FAILED;
        });
        transaction.getHandledEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.FAILEDROLLBACK;
        });
        assert transaction.getStatus().getState() == EventState.FAILEDROLLBACK;

    }

    @Test
    public void testRollbackPartialFail() throws Exception {
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*", new SimpleRollbackHandler(false, false, false, false));
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*", new SimpleRollbackHandler(false, true, false, false));
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.3.*", new SimpleRollbackHandler(false, true, false, false));
        EventTransaction transaction = EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), new SimpleEventSchedule(Instant.now().plusMillis(100)),"event.1");
        transaction.begin();
        assert transaction.getStatus().getState() == EventState.PENDING;
        assert transaction.getHandledEvents().size() == 0;
        assert transaction.getEvents().size() == 1;

        synchronized (this) {
            this.wait(500);
        }

        assert transaction.getHandledEvents().size() == 2;
        assert transaction.getEvents().size() == 1;
        transaction.getEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.FAILED;
        });
        assert transaction.getStatus().getState() == EventState.PARTIALLYROLLEDBACK;

    }

    @Test
    public void testRollbackMultiHandle() throws Exception {
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*", new SimpleRollbackHandler(true, true, false, false));
        EventScheduler.getDefaultEventScheduler().registerEventTarget("*", new SimpleRollbackHandler(true, true, false, false));
        EventScheduler.getDefaultEventScheduler().registerEventTarget("*.1", new SimpleRollbackHandler(false, true, false, false));
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.2", new SimpleRollbackHandler(false, true, false, false));
        EventScheduler.getDefaultEventScheduler().registerEventTarget("*.2", new SimpleRollbackHandler(false, true, false, false));
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event", new SimpleRollbackHandler(false, true, false, false));
        EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), new SimpleEventSchedule(Instant.now().plusMillis(10)),"event.2").begin();
        EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), new SimpleEventSchedule(Instant.now().plusMillis(100)),"event.3").begin();
        EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), new SimpleEventSchedule(Instant.now().plusMillis(300)),"event.4").begin();
        EventTransaction transaction = EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), new SimpleEventSchedule(Instant.now().plusMillis(290)),"event.1");
        assert transaction.getStatus().getState() == EventState.PENDING;
        assert transaction.getHandledEvents().size() == 0;
        assert transaction.getEvents().size() == 1;
        transaction.begin();

        synchronized (this) {
            this.wait(500);
        }

        assert transaction.getHandledEvents().size() == 3;
        assert transaction.getEvents().size() == 1;
        transaction.getEvents().stream().forEach((e) -> {
            assert e.getStatus().getState() == EventState.PARTIALLYCOMPLETE;
        });
        assert transaction.getStatus().getState() == EventState.ROLLEDBACK;

    }
}
