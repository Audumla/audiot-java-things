package net.audumla.automate.scheduler;

import net.audumla.automate.scheduler.quartz.AstrologicalScheduleBuilder;
import net.audumla.spacetime.Geolocation;
import org.apache.log4j.Logger;
import org.quartz.ScheduleBuilder;

import java.text.ParseException;

public class AstrologicalSchedule extends ScheduleAdaptor {
    private static Logger logger = Logger.getLogger(AstrologicalSchedule.class);
    private AstrologicalScheduleBuilder builder = new AstrologicalScheduleBuilder();

    public AstrologicalSchedule(Scheduler scheduler) {
        super(scheduler);
    }

    public void setStartFromSunrise(int seconds) {
        builder.offsetStartFromSunrise(seconds);
    }

    public void setStartFromSunset(int seconds) {
        builder.offsetStartFromSunset(seconds);
    }

    public void setEndFromSunrise(int seconds) {
        builder.offsetEndFromSunrise(seconds);
    }

    public void setEndFromSunset(int seconds) {
        builder.offsetEndFromSunset(seconds);
    }

    public void setInterval(int seconds) {
        builder.withSecondInterval(seconds);
    }

    public void setLocation(Geolocation source) {
        builder.atLocation(source.getLatitude(), source.getLongitude());
    }

    @Override
    protected ScheduleBuilder getScheduleBuilder() throws ParseException {
        return builder;
    }

}
