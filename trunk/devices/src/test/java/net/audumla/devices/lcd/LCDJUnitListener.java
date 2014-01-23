package net.audumla.devices.lcd;

import net.audumla.automate.event.EventScheduler;
import net.audumla.devices.lcd.rpi.RPII2CLCD;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class LCDJUnitListener extends RunListener {

    private Logger logger = Logger.getLogger(LCDJUnitListener.class);
    private EventScheduler scheduler = EventScheduler.getDefaultEventScheduler();
    private LCD target = RPII2CLCD.instance("JunitLCDListener", RPII2CLCD.DEFAULT_ADDRESS);

    public LCDJUnitListener() {
        if (target.initialize()) {
            logger.debug("Loaded JUnit RPII2CLCD Listener");
            scheduler.registerEventTarget(target,target.getName());
            try {
                target.write("LCD Started");
                scheduler.publishEvent(target.getName(),
                        new LCDClearCommand(),
                        new LCDWriteCommand("Testing"),
                        new LCDPauseCommand()).begin();            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            scheduler.unregisterEventTarget(target);
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Test: " + description.getMethodName()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Completed"),
                new LCDPauseCommand()).begin();
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Failed"),
                new LCDPauseCommand()).begin();
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        try {
            scheduler.publishEvent(target.getName(),
                    new LCDClearCommand(),
                    new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()),
                    new LCDSetCursorCommand(0, 1),
                    new LCDWriteCommand("Failed"),
                    new LCDPauseCommand()).begin();
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Test: " + description.getMethodName()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Started"),
                new LCDPauseCommand()).begin();
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Tests run: " + result.getRunCount()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Tests passed:" + (result.getRunCount() - result.getFailureCount())),
                new LCDSetCursorCommand(0, 2),
                new LCDWriteCommand("Tests failed:" + result.getFailureCount()),
                new LCDPauseCommand()).begin();
//        scheduler.shutdown();
    }

}
