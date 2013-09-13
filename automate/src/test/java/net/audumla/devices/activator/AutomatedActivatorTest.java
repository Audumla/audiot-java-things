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

/**
 * User: audumla
 * Date: 23/08/13
 * Time: 9:30 AM
 */

import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutomatedActivatorTest {
    ClassPathXmlApplicationContext context;

    @After
    public void tearDown() throws Exception {
        if (context != null) {
            context.close();
        }

    }

    @Test
    public void testFixedTimer() throws Exception {
        new ClassPathXmlApplicationContext("testFixedTimers.xml");
        synchronized (this) {
            this.wait(3000);
        }
    }

    @Test
    public void testNonSyncTimersTime5() throws Exception {
        for (int i = 0; i < 5;++i)
            testNonSyncTimers();
    }

    @Test
    public void testNonSyncTimers() throws Exception {
        context = new ClassPathXmlApplicationContext("testNonSynchronousTimers.xml");
        MockEventFactory factory = (MockEventFactory) context.getBean("eventFactory");
        synchronized (this) {
            int count = 0;
            while (count < 30 && factory.getCompletedCount() != 5) {
                this.wait(1000);
                ++count;
            }
            assert factory.getCompletedCount() == 5;
            assert factory.getExecutedCount() == 5;
            assert factory.getFailedCount() == 0;
        }
        context.close();
    }


    public void testSyncTimers() throws Exception {
        context = new ClassPathXmlApplicationContext("testSynchronousTimers.xml");
        MockEventFactory factory = (MockEventFactory) context.getBean("eventFactory");
        synchronized (this) {
            assert factory.getCompletedCount() == 0;
            assert factory.getExecutedCount() == 0;
            assert factory.getFailedCount() == 0;
            int count = 0;
            while (count < 20 && factory.getCompletedCount() != 5) {
                this.wait(1000);
                ++count;
            }
            assert factory.getCompletedCount() == 5;
            assert factory.getExecutedCount() == 5;
            assert factory.getFailedCount() == 0;
        }
    }
}
