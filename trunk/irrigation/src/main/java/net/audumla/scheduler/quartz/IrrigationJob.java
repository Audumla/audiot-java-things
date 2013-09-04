/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.scheduler.quartz;

import net.audumla.irrigation.IrrigationEvent;
import net.audumla.irrigation.IrrigationEventFactory;
import net.audumla.irrigation.Zone;
import org.apache.log4j.LogManager;
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
public class IrrigationJob implements Job {
    private static final Logger logger = LogManager.getLogger(IrrigationJob.class);

    public static final String ZONE_PROPERTY = "zone";
    public static final String EVENT_PROPERTY = "event";
    public static final String EVENT_FACTORY_PROPERTY = "eventFactory";

    public IrrigationJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        IrrigationEvent event = (IrrigationEvent) context.getMergedJobDataMap().get(EVENT_PROPERTY);
        if (event == null) {
            IrrigationEventFactory eventFactory = (IrrigationEventFactory) context.getMergedJobDataMap().get(EVENT_FACTORY_PROPERTY);
            event = eventFactory.generateIrrigationEvent(context.getFireTime());
        }
        if (event != null && event.getEventDuration() > 0) {
            Zone zone = (Zone) context.getMergedJobDataMap().get(ZONE_PROPERTY);
            zone.addIrrigationEvent(event);
            logger.info("Irrigating for " + event.getEventDuration() + " seconds");
        }
        context.getMergedJobDataMap().put(EVENT_PROPERTY, null);
    }

}
