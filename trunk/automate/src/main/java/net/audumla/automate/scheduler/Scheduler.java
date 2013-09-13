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

import org.apache.log4j.Logger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collection;
import java.util.HashSet;

public class Scheduler {
    private static final Logger logger = Logger.getLogger(Scheduler.class);
    private org.quartz.Scheduler scheduler;
    private Collection<Schedule> schedules = new HashSet<Schedule>();
    private boolean autoStartSchedules = true;

    synchronized public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setEnabled(autoStartSchedules);
    }

    public Collection<Schedule> getSchedules() {
        return schedules;
    }

    public void setAutoStartSchedules(boolean autoStartSchedules) {
        this.autoStartSchedules = autoStartSchedules;
    }

    synchronized public void removeSchedule(Schedule schedule) {
        schedule.setEnabled(false);
        schedules.remove(schedule);
    }

    public void setEnableAll(boolean enable) {
        schedules.forEach(t -> t.setEnabled(enable));
    }

    public void initialize() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            logger.info("Audumla Irrigation - started");

        } catch (Exception e) {
            logger.error(e);
        }

    }

    public void shutdown() {
        try {
            if (scheduler != null) {
                scheduler.shutdown();
                logger.info("Audumla Irrigation - stopped");
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

}
