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

import net.audumla.automate.DefaultEvent;
import net.audumla.automate.Event;
import net.audumla.automate.EventFactory;
import org.apache.log4j.Logger;

import java.util.Date;

public class MockEventFactory implements EventFactory {
    private static final Logger logger = Logger.getLogger(MockEventFactory.class);
    private boolean synchronous = false;
    private boolean eventActive = false;
    private int duration = 2;
    private int executedCount = 0;
    private int completedCount = 0;
    private int failedCount = 0;


    public MockEventFactory() {
    }

    public int getExecutedCount() {
        return executedCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public Event generateEvent(Date now) {
        return new MockEvent(now, duration);
    }

    private class MockEvent extends DefaultEvent {

        public MockEvent(Date time, long duration) {
            super(time, duration);
        }

        @Override
        public void setStatus(EventStatus status) {
            switch (status) {
                case EXECUTING:
                    if (synchronous && eventActive) {
                        //assert false;
                    } else {
                        eventActive = true;
                        ++executedCount;
                        logger.debug("Executed Event Count = " + executedCount);
                    }
                    break;
                case COMPLETE:
                    eventActive = false;
                    ++completedCount;
                    logger.debug("Completed Event Count = " + completedCount);
                    break;
                case FAILED:
                    eventActive = false;
                    ++failedCount;
                    logger.debug("Failed Event Count = " + failedCount);
                    break;
            }
            super.setStatus(status);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
