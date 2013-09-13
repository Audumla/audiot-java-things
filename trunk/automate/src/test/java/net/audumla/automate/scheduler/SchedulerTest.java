package net.audumla.automate.scheduler;

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

import net.audumla.automate.Event;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class SchedulerTest {

    @Before
    public void setUp() throws Exception {
        org.quartz.Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
    }

    @Test
    public void testAtomicSchedules() throws Exception {
        Scheduler scheduler = new Scheduler();
        final AtomicReference<Boolean> active = new AtomicReference<Boolean>(false);

        AtomicSchedule timer1 = new AtomicSchedule(scheduler);
        timer1.setStartTime(new Date(new Date().getTime() + 200));
        timer1.setSeconds(2);
        timer1.setHandler((Event e) -> {
            try {
                assert scheduler.getSchedules().size() == 1;
                assert StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs().size() == 1;
            } catch (SchedulerException e1) {
            }
            active.set(true);}
        );
        AtomicSchedule timer2 = new AtomicSchedule(scheduler);
        timer2.setStartTime(new Date(new Date().getTime() + 500));
        timer2.setSeconds(3);
        timer2.setHandler((Event e) -> {
            try {
                assert scheduler.getSchedules().size() == 0;
                assert StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs().size() == 1;
            } catch (SchedulerException e1) {
            }
            active.set(true);}
        );


        timer1.setEnabled(true);
        timer2.setEnabled(true);

        assert scheduler.getSchedules().size() == 2;
        synchronized (this) {
            assert !active.get();
            this.wait(300);
            assert active.get();
            active.set(false);
            assert StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs().size() == 0;
            assert scheduler.getSchedules().size() == 1;
            this.wait(300);
            assert active.get();
        }

        assert StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs().size() == 0;
        assert scheduler.getSchedules().size() == 0;
    }

    @Test
    public void testAtomicSchedule() throws Exception {
        Scheduler scheduler = new Scheduler();
        final AtomicReference<Boolean> active = new AtomicReference<Boolean>(false);

        AtomicSchedule timer = new AtomicSchedule(scheduler);
        timer.setStartTime(new Date(new Date().getTime() + 200));
        timer.setSeconds(2);
        timer.setHandler((Event e) -> {
            try {
                assert StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs().size() == 1;
            } catch (SchedulerException e1) {
            }
            active.set(true);}
        );

        timer.setEnabled(true);

        assert scheduler.getSchedules().size() == 1;
        synchronized (this) {
            assert !active.get();
            this.wait(300);
            assert active.get();
        }

        assert StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs().size() == 0;
        assert scheduler.getSchedules().size() == 0;
    }

    @Test
    public void testStoppingEvent() throws Exception {
    }
}
