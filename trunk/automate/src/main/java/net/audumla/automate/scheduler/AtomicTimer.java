package net.audumla.automate.scheduler;
/**
 * User: audumla
 * Date: 11/09/13
 * Time: 10:28 PM
 */

import net.audumla.automate.DefaultEventFactory;
import net.audumla.automate.Event;
import net.audumla.automate.EventHandler;
import net.audumla.automate.FixedDurationFactory;
import net.audumla.automate.scheduler.quartz.AutomateJob;
import net.audumla.bean.BeanUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class AtomicTimer implements Timer {
    private static final Logger logger = LogManager.getLogger(AtomicTimer.class);
    private Scheduler scheduler;
    private int seconds;
    private Date when;
    private JobDetail job;
    private String name = BeanUtils.generateName(Timer.class);
    private EventHandler handler;

    public AtomicTimer(int seconds, Date when, Scheduler scheduler) {
        this.seconds = seconds;
        this.when = when;
        scheduler.addSchedule(this);
        this.scheduler = scheduler;

    }

    public AtomicTimer(Scheduler scheduler) {
        scheduler.addSchedule(this);
        this.scheduler = scheduler;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventHandler getHandler() {
        return handler;
    }

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    /**
     * Used to remove the instance of the timer from the scheduler once it has activated as this timer is designed as a single shot
     * instance only
     */
    private class AtomicHandler implements EventHandler {

        private final EventHandler handler;

        public AtomicHandler(EventHandler handler) {
            this.handler = handler;
        }
        @Override
        public void handleEvent(Event event) {
            scheduler.removeSchedule(AtomicTimer.this);
            handler.handleEvent(event);
        }
    }

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            job = JobBuilder.newJob(AutomateJob.class).withIdentity(getName()).build();
            try {

                job.getJobDataMap().put(AutomateJob.EVENT_FACTORY_PROPERTY, new DefaultEventFactory(new FixedDurationFactory(seconds)));
                job.getJobDataMap().put(AutomateJob.EVENT_HANDLER_PROPERTY, new AtomicHandler(getHandler()));

                ScheduleBuilder builder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(1).withMisfireHandlingInstructionFireNow();


                Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + getName()).startAt(when)
                        .withSchedule(builder).build();

                StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
            } catch (Exception e) {
                logger.error(e);
            }
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
}
