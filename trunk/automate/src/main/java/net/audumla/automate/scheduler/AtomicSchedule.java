package net.audumla.automate.scheduler;
/**
 * User: audumla
 * Date: 11/09/13
 * Time: 10:28 PM
 */

import net.audumla.automate.*;
import net.audumla.automate.scheduler.quartz.AutomateJob;
import net.audumla.bean.BeanUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;

public class AtomicSchedule extends ScheduleAdaptor {
    private static final Logger logger = LogManager.getLogger(AtomicSchedule.class);

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
        public void handleEvent(Event event) {
            getScheduler().removeSchedule(AtomicSchedule.this);
            handler.handleEvent(event);
        }
    }
}
