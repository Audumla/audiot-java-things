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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ThreadPoolDispatcher extends AbstractDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolDispatcher.class);

    protected ScheduledExecutorService schedulerService;

    protected class ThreadPoolEventTransaction extends AbstractEventTransaction {

        private Future<?> future;

        public ThreadPoolEventTransaction(Dispatcher scheduler, EventSchedule schedule) {
            super(scheduler, schedule);
        }

        public ThreadPoolEventTransaction(Dispatcher scheduler) {
            super(scheduler);
        }

        @Override
        public void begin() {
            if (getSchedule() == null) {
                future = schedulerService.submit(toRunnable());
            } else {
                if (getSchedule() instanceof SimpleEventSchedule) {
                    SimpleEventSchedule ss = (SimpleEventSchedule) getSchedule();
                    if (ss.getRepeatCount() > 0) {
                        throw new UnsupportedOperationException("Scheduler does not support fixed repeat counts");
                    }
                    long initialDelay = Duration.between(Instant.now(), ss.getStartTime()).toMillis();
                    initialDelay = initialDelay < 0 ? 0 : initialDelay;
                    // set up a repeating schedule. We cannot set a fixed repeat count for this schedulerService
                    if (ss.getRepeatInterval() != null && !ss.getRepeatInterval().isZero()) {
                        future = schedulerService.scheduleAtFixedRate(toRunnable(), initialDelay, ss.getRepeatInterval().toMillis(), MILLISECONDS);
                    } else {
                        future = schedulerService.schedule(toRunnable(), initialDelay, MILLISECONDS);
                    }
                } else {
                    throw new UnsupportedOperationException("Scheduler does not support " + getSchedule().getClass());
                }
            }
        }
    }

    public ThreadPoolDispatcher() {
        initialize();
    }

    @Override
    public boolean initialize() {
        schedulerService = new ScheduledThreadPoolExecutor(1);
        return super.initialize();
    }

    @Override
    public boolean shutdown() {
        schedulerService.shutdown();
        return super.shutdown();
    }

    @Override
    public EventTransaction createTransaction() {
        return new ThreadPoolEventTransaction(this);
    }

    @Override
    public EventTransaction createTransaction(EventSchedule schedule) {
        return new ThreadPoolEventTransaction(this,schedule);
    }
}
