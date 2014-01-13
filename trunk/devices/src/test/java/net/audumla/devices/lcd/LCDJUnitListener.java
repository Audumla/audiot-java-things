package net.audumla.devices.lcd;

import net.audumla.devices.event.EventScheduler;
import net.audumla.devices.lcd.rpi.RPII2CLCD;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class LCDJUnitListener extends RunListener {

    Logger logger = Logger.getLogger(LCDJUnitListener.class);
    EventScheduler scheduler = EventScheduler.getInstance();
    LCD target = RPII2CLCD.instance("JunitLCDListener", RPII2CLCD.DEFAULT_ADDRESS);

    public LCDJUnitListener() {
        logger.debug("Loaded JUnit RPII2CLCD Listener");
    }

    @Override
    public void testFinished(Description description) throws Exception {
        scheduler.scheduleEvent(target,new LCDClearCommand());
        scheduler.scheduleEvent(target,new LCDWriteCommand("Test: " + description.getMethodName()));
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 1));
        scheduler.scheduleEvent(target,new LCDWriteCommand("Completed"));
        scheduler.scheduleEvent(target,new LCDPauseCommand());
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        scheduler.scheduleEvent(target,new LCDClearCommand());
        scheduler.scheduleEvent(target,new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()));
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 1));
        scheduler.scheduleEvent(target,new LCDWriteCommand("Failed"));
        scheduler.scheduleEvent(target,new LCDPauseCommand());
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        scheduler.scheduleEvent(target,new LCDClearCommand());
        scheduler.scheduleEvent(target,new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()));
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 1));
        scheduler.scheduleEvent(target,new LCDWriteCommand("Failed"));
        scheduler.scheduleEvent(target,new LCDPauseCommand());
    }

    @Override
    public void testStarted(Description description) throws Exception {
        scheduler.scheduleEvent(target,new LCDClearCommand());
        scheduler.scheduleEvent(target,new LCDWriteCommand("Test: " + description.getMethodName()));
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 1));
        scheduler.scheduleEvent(target,new LCDWriteCommand("Started"));
        scheduler.scheduleEvent(target,new LCDPauseCommand());
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        scheduler.scheduleEvent(target,new LCDClearCommand());
        scheduler.scheduleEvent(target,new LCDWriteCommand("Tests run: " + result.getRunCount()));
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 1));
        scheduler.scheduleEvent(target,new LCDWriteCommand("Tests passed:" + (result.getRunCount() - result.getFailureCount())));
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 2));
        scheduler.scheduleEvent(target,new LCDWriteCommand("Tests failed:" + result.getFailureCount()));
        scheduler.scheduleEvent(target,new LCDPauseCommand());
        scheduler.shutdown();
    }

}
