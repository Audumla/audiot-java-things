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

import net.audumla.automate.EventFactory;
import net.audumla.automate.EventHandler;
import net.audumla.automate.scheduler.quartz.AutomateJob;
import net.audumla.bean.BeanUtils;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;

public abstract class ScheduleAdaptor implements Schedule {
    private static Logger logger = Logger.getLogger(ScheduleAdaptor.class);
    private final Scheduler scheduler;

    private String group;
    private String name = BeanUtils.generateName(Schedule.class);
    private JobDetail job;
    private EventFactory factory;
    private EventHandler handler;
    private Class<? extends Job> jobClazz = AutomateJob.class;
    private Date startTime = new Date();

    protected abstract ScheduleBuilder getScheduleBuilder() throws ParseException;

    protected ScheduleAdaptor(Scheduler scheduler) {
        scheduler.addSchedule(this);
        this.scheduler = scheduler;
    }

    public void setJobClass(Class<? extends Job> jobClazz) {
        this.jobClazz = jobClazz;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    protected JobDetail start() {
        JobDetail jd = JobBuilder.newJob(jobClazz).withIdentity(name, group).build();
        try {

            jd.getJobDataMap().put(AutomateJob.SCHEDULE_PROPERTY,this);


            ScheduleBuilder builder = getScheduleBuilder();
            Trigger trigger = null;
            if (startTime.before(new Date())) {
                trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + getName()).startNow().withSchedule(builder).build();
            } else {
                trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + getName()).startAt(startTime).withSchedule(builder).build();
            }

            StdSchedulerFactory.getDefaultScheduler().scheduleJob(jd, trigger);
        } catch (Exception e) {
            logger.error(e);
        }
        return jd;
    }

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            job = start();
        } else {
            if (job != null) {
                try {
                    StdSchedulerFactory.getDefaultScheduler().deleteJob(job.getKey());
                } catch (SchedulerException e) {
                    logger.error(e);
                }
            }
        }
    }

    public void setFactory(EventFactory factory) {
        this.factory = factory;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    protected JobDetail getJob() {
        return job;
    }

    public EventFactory getFactory() {
        return factory;
    }

    public EventHandler getHandler() {
        return handler;
    }

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }
}
