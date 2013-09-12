/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.automate.scheduler.quartz;

import net.audumla.automate.Event;
import net.audumla.automate.EventFactory;
import net.audumla.automate.EventHandler;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * User: audumla
 * Date: 20/07/13
 * Time: 8:55 PM
 */

@DisallowConcurrentExecution
public class AutomateJob implements Job {
    private static final Logger logger = Logger.getLogger(AutomateJob.class);

    public static final String EVENT_HANDLER_PROPERTY = "handler";
    public static final String EVENT_PROPERTY = "event";
    public static final String EVENT_FACTORY_PROPERTY = "eventFactory";

    public AutomateJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        Event event = (Event) context.getMergedJobDataMap().get(EVENT_PROPERTY);
        if (event == null) {
            EventFactory eventFactory = (EventFactory) context.getMergedJobDataMap().get(EVENT_FACTORY_PROPERTY);
            event = eventFactory.generateEvent(context.getFireTime());
        }
        if (event != null && event.getEventDuration() > 0) {
            EventHandler handler= (EventHandler) context.getMergedJobDataMap().get(EVENT_HANDLER_PROPERTY);
            handler.handleEvent(event);
            logger.info("Executing automation function for " + event.getEventDuration() + " seconds");
        }
        context.getMergedJobDataMap().put(EVENT_PROPERTY, null);
    }

}
