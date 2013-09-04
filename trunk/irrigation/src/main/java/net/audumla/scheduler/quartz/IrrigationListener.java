package net.audumla.scheduler.quartz;
/**
 * User: audumla
 * Date: 2/08/13
 * Time: 12:28 PM
 */

import net.audumla.irrigation.IrrigationEvent;
import net.audumla.irrigation.IrrigationEventFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;
import org.quartz.listeners.TriggerListenerSupport;

import java.util.Date;

public class IrrigationListener extends TriggerListenerSupport {
    private static final Logger logger = LogManager.getLogger(IrrigationListener.class);

    @Override
    public String getName() {
        return "IrrigationListener";
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        Date now = context.getFireTime();
        IrrigationEventFactory eventFactory = (IrrigationEventFactory) context.getMergedJobDataMap().get(IrrigationJob.EVENT_FACTORY_PROPERTY);
        IrrigationEvent event = eventFactory.generateIrrigationEvent(now);
        if (event != null) {
            Date eventTime = event.getEventTime();
            if (eventTime.after(now)) {

                Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(trigger.getJobKey().getName() + ":" + now, trigger.getJobKey().getGroup()).startAt(eventTime).build();
                try {
                    context.getScheduler().scheduleJob(context.getJobDetail(),newTrigger);
                } catch (SchedulerException e) {
                    logger.error(e);
                }
                return true;
            }
        }
        else {
            return true;
        }
        context.getMergedJobDataMap().put(IrrigationJob.EVENT_PROPERTY,event);
        return false;
    }
}
