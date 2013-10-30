package net.audumla.automate.scheduler;
/**
 * User: audumla
 * JulianDate: 2/08/13
 * Time: 12:28 PM
 */

import net.audumla.automate.Event;
import net.audumla.automate.EventFactory;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.listeners.TriggerListenerSupport;

import java.util.Date;

public class AutomateListener extends TriggerListenerSupport {
    private static final Logger logger = Logger.getLogger(AutomateListener.class);

    @Override
    public String getName() {
        return "AutomateListener";
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        Date now = context.getFireTime();
        Schedule schedule= (Schedule) context.getMergedJobDataMap().get(AutomateJob.SCHEDULE_PROPERTY);

        EventFactory eventFactory = schedule.getFactory();
        Event event = eventFactory.generateEvent(now);
        if (event != null) {
            Date eventTime = event.getEventStartTime();
            if (eventTime.after(now)) {

                Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(trigger.getJobKey().getName() + ":" + now, trigger.getJobKey().getGroup()).startAt(eventTime).build();
                try {
                    context.getScheduler().scheduleJob(context.getJobDetail(), newTrigger);
                } catch (SchedulerException e) {
                    logger.error(e);
                }
                return true;
            }
        } else {
            return true;
        }
        context.getMergedJobDataMap().put(AutomateJob.EVENT_PROPERTY, event);
        return false;
    }
}
