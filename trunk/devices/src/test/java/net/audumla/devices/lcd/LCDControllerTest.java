package net.audumla.devices.lcd;

import net.audumla.automate.event.EventScheduler;
import net.audumla.devices.lcd.rpi.RPII2CLCD;
import org.junit.Before;
import org.junit.Test;

public class LCDControllerTest {

    EventScheduler scheduler = null;
    LCD target = null;

    @Before
    public void setUp() throws Exception {

        scheduler = EventScheduler.getDefaultEventScheduler();
        target = RPII2CLCD.instance("LCD", RPII2CLCD.DEFAULT_ADDRESS);
    }

    @Test
    public void testLCD() throws Exception {
        scheduler.scheduleEvent(target,new LCDWriteCommand("Hello")).begin();
    }

    @Test
    public void testAppender() {
        RPII2CLCD.logger.trace("Testing RPII2CLCD");
        RPII2CLCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
        RPII2CLCD.logger.trace("Massive! Not Really?");
        RPII2CLCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
    }

    @Test
    public void testController() throws Exception {
        scheduler.scheduleEvent(target,new LCDClearCommand()).begin();
        scheduler.scheduleEvent(target, new LCDWriteCommand("Test Output")).begin();
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 1)).begin();
        scheduler.scheduleEvent(target,new LCDWriteCommand("Moved to Line")).begin();
        scheduler.scheduleEvent(target,new LCDPauseCommand()).begin();
    }

}
