package net.audumla.automate.scheduler;

import net.audumla.automate.DefaultEventFactory;
import net.audumla.automate.FixedDurationFactory;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;

import java.text.ParseException;


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

    public void setSeconds(int seconds) {
        setFactory(new DefaultEventFactory(new FixedDurationFactory(seconds)));
    }
}
