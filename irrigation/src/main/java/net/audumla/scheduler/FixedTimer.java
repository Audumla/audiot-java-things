package net.audumla.scheduler;

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
public class FixedTimer extends TimerAdaptor {
    private static Logger logger = Logger.getLogger(FixedTimer.class);

    private String cronExpression;

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    protected ScheduleBuilder getScheduleBuilder() throws ParseException {
        return CronScheduleBuilder.cronSchedule(new CronExpression(cronExpression));
    }
}
