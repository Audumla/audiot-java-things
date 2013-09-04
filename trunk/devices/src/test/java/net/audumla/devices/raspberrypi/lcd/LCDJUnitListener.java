package net.audumla.devices.raspberrypi.lcd;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class LCDJUnitListener extends RunListener {

	Logger logger = LogManager.getLogger(LCDJUnitListener.class);
	LCDCommandQueue lcd = LCDCommandQueue.instance();

	public LCDJUnitListener() {
		logger.debug("Loaded JUnit LCD Listener");
	}

	@Override
	public void testFinished(Description description) throws Exception {
		lcd.append(new LCDClearCommand());
		lcd.append(new LCDWriteCommand("Test: " + description.getMethodName()));
		lcd.append(new LCDSetCursorCommand(0, 1));
		lcd.append(new LCDWriteCommand("Completed"));
		lcd.append(new LCDPauseCommand());
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		lcd.append(new LCDClearCommand());
		lcd.append(new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()));
		lcd.append(new LCDSetCursorCommand(0, 1));
		lcd.append(new LCDWriteCommand("Failed"));
		lcd.append(new LCDPauseCommand());
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		lcd.append(new LCDClearCommand());
		lcd.append(new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()));
		lcd.append(new LCDSetCursorCommand(0, 1));
		lcd.append(new LCDWriteCommand("Failed"));
		lcd.append(new LCDPauseCommand());
	}

	@Override
	public void testStarted(Description description) throws Exception {
		lcd.append(new LCDClearCommand());
		lcd.append(new LCDWriteCommand("Test: " + description.getMethodName()));
		lcd.append(new LCDSetCursorCommand(0, 1));
		lcd.append(new LCDWriteCommand("Started"));
		lcd.append(new LCDPauseCommand());
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		lcd.append(new LCDClearCommand());
		lcd.append(new LCDWriteCommand("Tests run: " + result.getRunCount()));
		lcd.append(new LCDSetCursorCommand(0, 1));
		lcd.append(new LCDWriteCommand("Tests passed:" + (result.getRunCount() - result.getFailureCount())));
		lcd.append(new LCDSetCursorCommand(0, 2));
		lcd.append(new LCDWriteCommand("Tests failed:" + result.getFailureCount()));
		lcd.append(new LCDPauseCommand());
		lcd.stop();
	}

}
