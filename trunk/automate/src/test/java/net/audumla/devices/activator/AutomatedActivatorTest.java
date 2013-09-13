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

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutomatedActivatorTest {

    @Test
    public void testFixedTimer() throws Exception {
        new ClassPathXmlApplicationContext("testFixedTimers.xml");
        synchronized (this) {
            this.wait(3000);
        }
    }

    @Test
    public void testNonSyncTimers() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("testNonSynchronousTimers.xml");
        MockEventFactory factory = (MockEventFactory) context.getBean("eventFactory");
        synchronized (this) {
            assert factory.getCompletedCount() == 0;
            assert factory.getExecutedCount() == 0;
            assert factory.getFailedCount() == 0;
            this.wait(3000);
            assert factory.getCompletedCount() == 5;
            assert factory.getExecutedCount() == 5;
            assert factory.getFailedCount() == 0;
        }
    }
}
