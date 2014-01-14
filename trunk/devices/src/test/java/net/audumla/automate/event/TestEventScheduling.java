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
    public void testWildCardTopic() throws Exception {
        AtomicReference<Integer> count = new AtomicReference<Integer>(0);

        EventScheduler.getDefaultEventScheduler().registerEventTarget("event.*",event -> {
            count.set(count.get()+1);
            return true;
        });
        EventScheduler.getDefaultEventScheduler().scheduleEvent("event.1", new AbstractEvent()).begin();
        EventScheduler.getDefaultEventScheduler().scheduleEvent("event.2", new AbstractEvent()).begin();
        EventScheduler.getDefaultEventScheduler().scheduleEvent("event1", new AbstractEvent()).begin();

        synchronized (this) {
            this.wait(1000);
        }

        assert count.get() == 2;


    }
}
