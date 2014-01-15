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

public class RolbackTest {
    private static final Logger logger = LoggerFactory.getLogger(RolbackTest.class);


    public static class SimpleRollbackHandler extends AbstractEventTarget implements RollbackEventTarget {

        @Override
        public boolean handleEvent(Event event) throws Throwable {
            return false;
        }

        @Override
        public boolean rollbackEvent(Event event) throws Throwable {
            return true;
        }
    }

    @Test
    public void testSimpleRollback() throws Exception {
        SimpleRollbackHandler handler = new SimpleRollbackHandler();
        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*",handler);
        EventTransaction transaction = EventScheduler.getDefaultEventScheduler().scheduleEvent(new AbstractEvent(), "event.1");
        assert transaction.getStatus().getState() == EventState.PENDING;
        assert transaction.getHandledEvents().size() == 0;
        assert transaction.getEvents().size() == 1;

        transaction.begin();

        synchronized (this) {
            this.wait(50000);
        }

        assert transaction.getHandledEvents().size() == 1;
        assert transaction.getEvents().size() == 1;
        transaction.getEvents().stream().forEach((e) -> {assert e.getStatus().getState() == EventState.FAILED;});
        transaction.getHandledEvents().stream().forEach((e) -> {assert e.getStatus().getState() == EventState.ROLLEDBACK;});
        assert transaction.getStatus().getState() == EventState.ROLLEDBACK;

    }
}
