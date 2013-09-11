package net.audumla.devices.lcd;

import net.audumla.devices.lcd.*;
import net.audumla.devices.lcd.raspberrypi.RaspberryPII2CLCD;
import org.junit.Test;

public class LCDControllerTest {

    @Test
    public void testLCD() throws Exception {
        LCDCommandQueue.instance(RaspberryPII2CLCD.instance(RaspberryPII2CLCD.DEFAULT_ADDRESS)).append(new LCDWriteCommand("Hello"));
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
        LCDCommandQueue lcd = LCDCommandQueue.instance(RaspberryPII2CLCD.instance(RaspberryPII2CLCD.DEFAULT_ADDRESS));
        lcd.append(new LCDClearCommand());
        lcd.append(new LCDWriteCommand("Test Output"));
        lcd.append(new LCDSetCursorCommand(0, 1));
        lcd.append(new LCDWriteCommand("Moved to Line"));
        lcd.append(new LCDPauseCommand());
    }

}
