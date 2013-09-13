package net.audumla.automate.scheduler;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class FixedSchedule extends ScheduleAdaptor {
    private static Logger logger = Logger.getLogger(FixedSchedule.class);
    private String cronExpression;

    public FixedSchedule(Scheduler scheduler) {
        super(scheduler);
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    protected ScheduleBuilder getScheduleBuilder() throws ParseException {
        return CronScheduleBuilder.cronSchedule(new CronExpression(cronExpression));
    }
}
