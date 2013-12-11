package net.audumla.automate.scheduler.quartz;

import net.audumla.scheduler.quartz.AstronomicScheduleBuilder;
import net.audumla.astronomy.Geolocation;
import org.apache.log4j.Logger;
import org.quartz.ScheduleBuilder;

import java.text.ParseException;

public class AstrologicalSchedule extends QuartzScheduleAdaptor {
    private static Logger logger = Logger.getLogger(AstrologicalSchedule.class);
    private AstronomicScheduleBuilder builder = new AstronomicScheduleBuilder();

    public AstrologicalSchedule(QuartzScheduler scheduler) {
        super(scheduler);
    }

    public void setStartFromSunrise(int seconds) {
        builder.startEventOffset(seconds);
    }

    public void setStartFromSunset(int seconds) {
       // builder.offsetStartFromSet(seconds);
    }

    public void setEndFromSunrise(int seconds) {
      //  builder.offsetEndFromRise(seconds);
    }

    public void setEndFromSunset(int seconds) {
      //  builder.offsetEndFromSet(seconds);
    }

    public void setInterval(int seconds) {
     //   builder.withIntervalInSeconds(seconds);
    }

    public void setLocation(Geolocation source) {
     //   builder.atLocation(source.getLatitude(), source.getLongitude());
    }

    @Override
    protected ScheduleBuilder getScheduleBuilder() throws ParseException {
        return builder;
    }

}
