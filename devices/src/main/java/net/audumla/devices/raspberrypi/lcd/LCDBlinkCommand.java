package net.audumla.devices.raspberrypi.lcd;

public class LCDBlinkCommand implements LCDCommand {
	
	protected boolean blink;
	
	public LCDBlinkCommand(boolean b) {
		blink = b;
	}
	
	public void execute(LCD lcd) {
		if (blink)
			lcd.blink();
		else 
			lcd.noBlink();
	}

}
