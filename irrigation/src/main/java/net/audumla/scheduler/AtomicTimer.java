package net.audumla.scheduler;
/**
 * User: audumla
 * Date: 11/09/13
 * Time: 10:28 PM
 */

import net.audumla.irrigation.FixedDurationFactory;
import net.audumla.irrigation.FixedIrrigationEventFactory;
import net.audumla.scheduler.quartz.IrrigationJob;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;

public class AtomicTimer extends TimerAdaptor {
    private static final Logger logger = LogManager.getLogger(AtomicTimer.class);
    private int seconds;
    private Date when;

    public AtomicTimer(int seconds, Date when) {
        this.seconds = seconds;
        this.when = when;
    }


    public AtomicTimer() {
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
    protected JobDetail start() {
        JobDetail jd = JobBuilder.newJob(IrrigationJob.class).withIdentity(getName(), getGroup()).build();
        try {

            jd.getJobDataMap().put(IrrigationJob.EVENT_FACTORY_PROPERTY, new FixedIrrigationEventFactory(new FixedDurationFactory(seconds)));
            jd.getJobDataMap().put(IrrigationJob.ZONE_PROPERTY, getZone());

            ScheduleBuilder builder = getScheduleBuilder();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + getName(), getGroup()).startAt(when)
                    .withSchedule(builder).build();

            StdSchedulerFactory.getDefaultScheduler().scheduleJob(jd, trigger);
        } catch (Exception e) {
            logger.error(e);
        }
        return jd;
    }

    @Override
    protected ScheduleBuilder getScheduleBuilder() throws ParseException {
        return SimpleScheduleBuilder.repeatSecondlyForTotalCount(1).withMisfireHandlingInstructionFireNow();
    }
}
