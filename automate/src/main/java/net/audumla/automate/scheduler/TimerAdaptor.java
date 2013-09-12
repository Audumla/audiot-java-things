package net.audumla.automate.scheduler;

import net.audumla.automate.EventFactory;
import net.audumla.automate.EventHandler;
import net.audumla.automate.scheduler.quartz.AutomateJob;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 11:31 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TimerAdaptor implements Timer {
    private static Logger logger = Logger.getLogger(TimerAdaptor.class);

    private String group;
    private String name;
    private JobDetail job;
    private EventFactory factory;
    private EventHandler handler;

    protected abstract ScheduleBuilder getScheduleBuilder() throws ParseException;

    protected JobDetail start() {
        JobDetail jd = JobBuilder.newJob(AutomateJob.class).withIdentity(name, group).build();
        try {

            jd.getJobDataMap().put(AutomateJob.EVENT_FACTORY_PROPERTY, factory);
            jd.getJobDataMap().put(AutomateJob.EVENT_HANDLER_PROPERTY, handler);


            ScheduleBuilder builder = getScheduleBuilder();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + name, group).startNow()
                    .withSchedule(builder).build();

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

    protected EventFactory getFactory() {
        return factory;
    }

    public EventHandler getHandler() {
        return handler;
    }

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }
}
