package net.audumla.automate.scheduler;
/**
 * User: audumla
 * Date: 11/09/13
 * Time: 10:28 PM
 */

import net.audumla.automate.DefaultEventFactory;
import net.audumla.automate.Event;
import net.audumla.automate.EventHandler;
import net.audumla.automate.FixedDurationFactory;
import org.apache.log4j.Logger;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;

import java.text.ParseException;

public class AtomicSchedule extends ScheduleAdaptor {
    private static final Logger logger = Logger.getLogger(AtomicSchedule.class);

    public AtomicSchedule(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void setHandler(EventHandler handler) {
        super.setHandler(new AtomicHandler(handler));
    }

    public void setSeconds(int seconds) {
        setFactory(new DefaultEventFactory(new FixedDurationFactory(seconds)));
    }

    @Override
    protected ScheduleBuilder getScheduleBuilder() throws ParseException {
        return SimpleScheduleBuilder.repeatSecondlyForTotalCount(1).withMisfireHandlingInstructionFireNow();
    }

    /**
     * Used to remove the instance of the timer from the scheduler once it has activated as this timer is designed as a single shot
     * instance only
     */
    private class AtomicHandler implements EventHandler {

        private final EventHandler handler;

        public AtomicHandler(EventHandler handler) {
            this.handler = handler;
        }

        @Override
        public boolean handleEvent(Event event) {
            getScheduler().removeSchedule(AtomicSchedule.this);
            return handler.handleEvent(event);
        }
    }
}
