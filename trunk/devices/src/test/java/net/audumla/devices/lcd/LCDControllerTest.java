package net.audumla.devices.lcd;

import net.audumla.devices.event.EventScheduler;
import net.audumla.devices.lcd.raspberrypi.RaspberryPII2CLCD;
import org.junit.Before;
import org.junit.Test;

public class LCDControllerTest {

    EventScheduler scheduler = null;
    LCD target = null;

    @Before
    public void setUp() throws Exception {

        scheduler = EventScheduler.getInstance();
        target = RaspberryPII2CLCD.instance("LCD",RaspberryPII2CLCD.DEFAULT_ADDRESS);
    }

    @Test
    public void testLCD() throws Exception {
        scheduler.scheduleEvent(target,new LCDWriteCommand("Hello"));
    }

    @Test
    public void testAppender() {
        RaspberryPII2CLCD.logger.trace("Testing RaspberryPII2CLCD");
        RaspberryPII2CLCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
        RaspberryPII2CLCD.logger.trace("Massive! Not Really?");
        RaspberryPII2CLCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
    }

    @Test
    public void testController() {
        scheduler.scheduleEvent(target,new LCDClearCommand());
        scheduler.scheduleEvent(target,new LCDWriteCommand("Test Output"));
        scheduler.scheduleEvent(target,new LCDSetCursorCommand(0, 1));
        scheduler.scheduleEvent(target,new LCDWriteCommand("Moved to Line"));
        scheduler.scheduleEvent(target,new LCDPauseCommand());
    }

}
