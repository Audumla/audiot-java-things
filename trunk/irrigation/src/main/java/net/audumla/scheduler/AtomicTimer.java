package net.audumla.scheduler;
/**
 * User: audumla
 * Date: 11/09/13
 * Time: 10:28 PM
 */

import net.audumla.irrigation.FixedDurationFactory;
import net.audumla.irrigation.FixedIrrigationEventFactory;
import net.audumla.irrigation.Zone;
import net.audumla.scheduler.quartz.IrrigationJob;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;

public class AtomicTimer implements Timer {
    private static final Logger logger = LogManager.getLogger(AtomicTimer.class);
    private int seconds;
    private Date when;
    private JobDetail job;
    private String name;
    private Zone zone;

    public AtomicTimer(int seconds, Date when) {
        this.seconds = seconds;
        this.when = when;
    }

    public AtomicTimer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
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

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            job = JobBuilder.newJob(IrrigationJob.class).withIdentity(getName()).build();
            try {

                job.getJobDataMap().put(IrrigationJob.EVENT_FACTORY_PROPERTY, new FixedIrrigationEventFactory(new FixedDurationFactory(seconds)));
                job.getJobDataMap().put(IrrigationJob.ZONE_PROPERTY, getZone());

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
