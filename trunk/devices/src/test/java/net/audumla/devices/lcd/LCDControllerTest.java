package net.audumla.devices.lcd;

import net.audumla.automate.event.Dispatcher;
import net.audumla.devices.lcd.rpi.RPII2CLCD;
import org.junit.Before;
import org.junit.Test;

public class LCDControllerTest {

    Dispatcher scheduler = null;
    LCD target = null;

    @Before
    public void setUp() throws Exception {

        scheduler = Dispatcher.getDefaultEventScheduler();
        target = RPII2CLCD.instance("LCD", RPII2CLCD.DEFAULT_ADDRESS);
    }

    @Test
    public void testLCD() throws Exception {
        scheduler.publishEvent(target.getName(), new LCDWriteCommand("Hello")).begin();
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
        scheduler.publishEvent(target.getName(), new LCDClearCommand()).begin();
        scheduler.publishEvent(target.getName(), new LCDWriteCommand("Test Output")).begin();
        scheduler.publishEvent(target.getName(), new LCDSetCursorCommand(0, 1)).begin();
        scheduler.publishEvent(target.getName(), new LCDWriteCommand("Moved to Line")).begin();
        scheduler.publishEvent(target.getName(), new LCDPauseCommand()).begin();
    }

}
