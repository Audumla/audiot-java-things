/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.automate.scheduler.quartz;

import net.audumla.automate.Event;
import net.audumla.automate.EventFactory;
import net.audumla.automate.EventHandler;
import net.audumla.automate.scheduler.Schedule;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * User: audumla
 * JulianDate: 20/07/13
 * Time: 8:55 PM
 */

@DisallowConcurrentExecution
public class AutomateJob implements Job {
    //    public static final String EVENT_HANDLER_PROPERTY = "handler";
    public static final String EVENT_PROPERTY = "event";
    //    public static final String EVENT_FACTORY_PROPERTY = "eventFactory";
    public static final String SCHEDULE_PROPERTY = "schedule";
    private static final Logger logger = Logger.getLogger(AutomateJob.class);

    public AutomateJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        Event event = (Event) context.getMergedJobDataMap().get(EVENT_PROPERTY);
        Schedule schedule = (Schedule) context.getMergedJobDataMap().get(SCHEDULE_PROPERTY);
        if (event == null) {
            EventFactory eventFactory = schedule.getFactory();
            if (eventFactory != null) {
                event = eventFactory.generateEvent(context.getFireTime());
                context.getMergedJobDataMap().put(EVENT_PROPERTY,event);
            } else {
                logger.error("No event factory has been set for job [" + context.getJobDetail().getKey().getName() + ":" + context.getJobDetail().getKey().getName() + "]");
            }
        }
        if (event != null && event.getEventDuration() > 0) {
            EventHandler handler = schedule.getHandler();
            if (handler != null) {
                if (handler.handleEvent(event)) {
                    logger.info("Executing automation [" + event.getName() + " - Status:"+event.getStatus()+"] for " + event.getEventDuration() + " seconds - for job [" + context.getJobDetail().getKey().getName() + ":" + context.getJobDetail().getKey().getName() + "]");
                } else {
                    logger.info("Unable to execute automation [" + event.getName() + "] for " + event.getEventDuration() + " seconds - for job [" + context.getJobDetail().getKey().getName() + ":" + context.getJobDetail().getKey().getName() + "]");

                }
            } else {
                logger.error("No event handler has been set for job [" + context.getJobDetail().getKey().getName() + ":" + context.getJobDetail().getKey().getName() + "]");
            }
        }
        context.getMergedJobDataMap().put(EVENT_PROPERTY, null);
    }

}
