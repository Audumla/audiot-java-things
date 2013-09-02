package net.audumla.devices.raspberrypi.lcd;

import org.junit.Test;

public class LCDControllerTest {

	@Test
	public void testLCD() throws Exception {
		LCDCommandQueue.instance().append(new LCDWriteCommand("Hello"));
	}

	@Test
	public void testAppender() {
		LCD.logger.trace("Testing LCD");
		LCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
		LCD.logger.trace("Massive! Not Really?");
		LCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
	}

	@Test
	public void testController() {
		LCDCommandQueue lcd = LCDCommandQueue.instance();
		lcd.append(new LCDClearCommand());
		lcd.append(new LCDWriteCommand("Test Output"));
		lcd.append(new LCDSetCursorCommand(0, 1));
		lcd.append(new LCDWriteCommand("Moved to Line"));
		lcd.append(new LCDPauseCommand());
	}

}
