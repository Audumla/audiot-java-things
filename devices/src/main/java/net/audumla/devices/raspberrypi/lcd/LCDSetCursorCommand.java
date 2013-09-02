package net.audumla.devices.raspberrypi.lcd;

public class LCDSetCursorCommand implements LCDCommand {
	
	protected int col;
	protected int row;
	
	public LCDSetCursorCommand(int c, int r) {
		col = c;
		row = r;
	}
	
	public void execute(LCD lcd) {
		lcd.setCursor(col, row);
	}

}
