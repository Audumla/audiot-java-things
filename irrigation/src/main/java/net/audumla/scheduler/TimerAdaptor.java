package net.audumla.scheduler;

import net.audumla.irrigation.IrrigationEventFactory;
import net.audumla.irrigation.Zone;
import net.audumla.scheduler.quartz.IrrigationJob;
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
    private IrrigationEventFactory factory;
    private Zone zone;

    protected abstract ScheduleBuilder getScheduleBuilder() throws ParseException;

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            try {
                job = JobBuilder.newJob(IrrigationJob.class).withIdentity(name, group).build();

                job.getJobDataMap().put(IrrigationJob.EVENT_FACTORY_PROPERTY, factory);
                job.getJobDataMap().put(IrrigationJob.ZONE_PROPERTY, zone);

                ScheduleBuilder builder = getScheduleBuilder();

                Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + name, group).startNow()
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

    public void setFactory(IrrigationEventFactory factory) {
        this.factory = factory;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setName(String name) {
        this.name = name;
    }
}
