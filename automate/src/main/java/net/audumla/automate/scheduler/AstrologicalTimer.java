package net.audumla.automate.scheduler;

import net.audumla.automate.scheduler.quartz.AstrologicalScheduleBuilder;
import net.audumla.util.Geolocation;
import org.apache.log4j.Logger;
import org.quartz.ScheduleBuilder;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class AstrologicalTimer extends TimerAdaptor {
    private static Logger logger = Logger.getLogger(AstrologicalTimer.class);
    private AstrologicalScheduleBuilder builder = new AstrologicalScheduleBuilder();

    public AstrologicalTimer(Scheduler scheduler) {
        scheduler.addSchedule(this);
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
