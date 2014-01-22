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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ThreadLocalEventScheduler extends AbstractEventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalEventScheduler.class);

    protected Map<Pattern, EventTarget> targetRegistry = new HashMap<>();

    protected class ThreadLocalEventTransaction extends SimpleEventTransaction {

        public ThreadLocalEventTransaction(EventScheduler scheduler, EventSchedule schedule) {
            super(scheduler, schedule);
        }

        public ThreadLocalEventTransaction(EventScheduler scheduler) {
            super(scheduler);
        }


        @Override
        public void begin() {
            if (getSchedule() == null) {
                toRunnable().run();
            } else {
                if (getSchedule() instanceof SimpleEventSchedule) {
                    SimpleEventSchedule ss = (SimpleEventSchedule) getSchedule();
                    if (ss.getRepeatCount() > 0 || (ss.getRepeatInterval() != null && !ss.getRepeatInterval().isZero())) {
                        throw new UnsupportedOperationException("Scheduler does not support repeat scheduling");
                    }
                    long initialDelay = Duration.between(Instant.now(), ss.getStartTime()).toMillis();
                    initialDelay = initialDelay < 0 ? 0 : initialDelay;
                    synchronized (this) {
                        try {
                            this.wait(initialDelay);
                            toRunnable().run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("Scheduler does not support " + getSchedule().getClass());
                }
            }
        }

    }

    public ThreadLocalEventScheduler() {
        super();
    }


    @Override
    public EventTransaction createTransaction() {
        return new ThreadLocalEventTransaction(this);
    }

    @Override
    public EventTransaction createTransaction(EventSchedule schedule) {
        return new ThreadLocalEventTransaction(this,schedule);
    }
}
