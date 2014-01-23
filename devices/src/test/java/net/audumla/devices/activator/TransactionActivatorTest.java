package net.audumla.devices.activator;

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

import net.audumla.automate.event.EventTransaction;
import net.audumla.automate.event.ThreadLocalEventScheduler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionActivatorTest {
    private static final Logger logger = LoggerFactory.getLogger(TransactionActivatorTest.class);


    @Test
    public void testBlockingTransaction() throws Exception {

        TransactionActivatorMockProvider provider = new TransactionActivatorMockProvider();
        ThreadLocalEventScheduler scheduler = new ThreadLocalEventScheduler();
        scheduler.registerEventTarget(provider.getActivator(null), "activator.1");
        scheduler.registerEventTarget(provider.getActivator(null), "activator.2");
        scheduler.registerEventTarget(provider.getActivator(null), "activator.3");
        scheduler.registerEventTarget(provider.getActivator(null), "activator.4");
        scheduler.registerEventTarget(provider.getActivator(null), "activator.5");

        assert provider.getActivators().size() == 5;

        EventTransaction tr = scheduler.publishEvent(new EnableActivatorCommand(), "activator.1", "activator.2", "activator.3", "activator.4");
        tr.begin();
        assert tr.getHandledEvents().size() == 4;
        int i = 0;
        for (Activator a : provider.getActivators()) {
            if (a.getState().equals(ActivatorState.ACTIVATED)) {
                ++i;
            }
        }
        assert i == 4;


    }
}
