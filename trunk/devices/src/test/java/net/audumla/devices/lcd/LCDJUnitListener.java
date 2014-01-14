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
    EventScheduler scheduler = EventScheduler.getDefaultEventScheduler();
    LCD target = RPII2CLCD.instance("JunitLCDListener", RPII2CLCD.DEFAULT_ADDRESS);

    public LCDJUnitListener() {
        logger.debug("Loaded JUnit RPII2CLCD Listener");
    }

    @Override
    public void testFinished(Description description) throws Exception {
        scheduler.scheduleEvent(target, new LCDClearCommand()).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Test: " + description.getMethodName())).begin();
        scheduler.scheduleEvent(target, new LCDSetCursorCommand(0, 1)).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Completed")).begin();
        scheduler.scheduleEvent(target, new LCDPauseCommand()).begin();
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        scheduler.scheduleEvent(target, new LCDClearCommand()).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Test: " + failure.getDescription().getMethodName())).begin();
        scheduler.scheduleEvent(target, new LCDSetCursorCommand(0, 1)).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Failed")).begin();
        scheduler.scheduleEvent(target, new LCDPauseCommand()).begin();
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        try {
            scheduler.scheduleEvent(target, new LCDClearCommand()).begin();
            scheduler.scheduleEvent(target, new LCDWriteCommand("Test: " + failure.getDescription().getMethodName())).begin();
            scheduler.scheduleEvent(target, new LCDSetCursorCommand(0, 1)).begin();
            scheduler.scheduleEvent(target, new LCDWriteCommand("Failed")).begin();
            scheduler.scheduleEvent(target, new LCDPauseCommand()).begin();
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        scheduler.scheduleEvent(target, new LCDClearCommand()).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Test: " + description.getMethodName())).begin();
        scheduler.scheduleEvent(target, new LCDSetCursorCommand(0, 1)).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Started")).begin();
        scheduler.scheduleEvent(target, new LCDPauseCommand()).begin();
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        scheduler.scheduleEvent(target, new LCDClearCommand()).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Tests run: " + result.getRunCount())).begin();
        scheduler.scheduleEvent(target, new LCDSetCursorCommand(0, 1)).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Tests passed:" + (result.getRunCount() - result.getFailureCount()))).begin();
        scheduler.scheduleEvent(target, new LCDSetCursorCommand(0, 2)).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Tests failed:" + result.getFailureCount())).begin();
        scheduler.scheduleEvent(target, new LCDPauseCommand()).begin();
        scheduler.shutdown();
    }

}
