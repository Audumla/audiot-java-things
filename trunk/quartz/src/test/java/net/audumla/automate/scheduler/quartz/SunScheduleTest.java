package net.audumla.automate.scheduler.quartz;

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

import net.audumla.astronomical.Location;
import net.audumla.astronomical.ObjectRiseEvent;
import net.audumla.astronomical.OrbitingObject;
import net.audumla.astronomical.algorithims.Sun;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class SunScheduleTest {
    private static Logger logger = Logger.getLogger("Test");

    @Before
    public void setUp() throws Exception {
        TestJob.count = 0;
        StdSchedulerFactory.getDefaultScheduler().clear();
    }

    public static class TestJob implements Job {

        public static int count = 0;

        public TestJob() {

        }

        public void execute(JobExecutionContext jec) throws JobExecutionException {
            try {
                logger.debug("Job is Starting - " + jec.getJobDetail().getKey().getName());
                synchronized (this) {
                    this.wait(500);
                }
                logger.debug("Job is ending - " + jec.getJobDetail().getKey().getName());
                ++count;
            } catch (InterruptedException e) {
                logger.warn("Job failed to execute - " + jec.getJobDetail().getKey().getName());
                throw new JobExecutionException(e);
            }

        }

    }

    @Test
    public void testSingleRiseOnly() throws Exception {
        AstronomicalScheduleBuilder.astronomicalSchedule().startEvent(new ObjectRiseEvent(OrbitingObject.Sun,new Location(-38, 145, 0), Sun.CIVIL)).startEventOffset(0).withEventCount(1);

    }

    @Test
    public void testEveryRise() throws Exception {
        AstronomicalScheduleBuilder.astronomicalSchedule().startEvent(new ObjectRiseEvent(OrbitingObject.Sun,new Location(-38, 145, 0), Sun.CIVIL)).startEventOffset(0).forEveryEvent();

    }

    @Test
    public void testRise3Times() throws Exception {
        AstronomicalScheduleBuilder.astronomicalSchedule().startEvent(new ObjectRiseEvent(OrbitingObject.Sun,new Location(-38, 145, 0), Sun.CIVIL)).startEventOffset(0).withEventCount(3);

    }

    @Test
    public void test1SecondInterval3TimesEveryRise() throws Exception {
        AstronomicalScheduleBuilder.astronomicalSchedule().startEvent(new ObjectRiseEvent(OrbitingObject.Sun,new Location(-38, 145, 0), Sun.CIVIL)).startEventOffset(0).withCount(3).withSecondInterval(1).forEveryEvent();

    }

}