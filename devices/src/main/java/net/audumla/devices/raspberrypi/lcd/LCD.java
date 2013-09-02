package net.audumla.devices.raspberrypi.lcd;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LCD {
	protected static final int MCP23008_ADDRESS = 0x20;

	protected final static int LCD_8BITMODE = 0x10;
	protected final static int LCD_4BITMODE = 0x00;
	protected final static int LCD_2LINE = 0x08;
	protected final static int LCD_1LINE = 0x00;
	protected final static int LCD_5x10DOTS = 0x04;
	protected final static int LCD_5x8DOTS = 0x00;

	// flags for display on/off control
	protected final static int LCD_DISPLAYON = 0x04 ;
	protected final static int LCD_DISPLAYOFF = 0x00;
	protected final static int LCD_CURSORON = 0x02;
	protected final static int LCD_CURSOROFF = 0x00;
	protected final static int LCD_BLINKON = 0x01;
	protected final static int LCD_BLINKOFF = 0x00;

	// commands
	protected final static int LCD_FUNCTIONSET = 0x20;
	protected final static int LCD_CLEARDISPLAY = 0x01;
	protected final static int LCD_RETURNHOME = 0x02;
	protected final static int LCD_ENTRYMODESET = 0x04;
	protected final static int LCD_DISPLAYCONTROL = 0x08;
	protected final static int LCD_CURSORSHIFT = 0x10;
	protected final static int LCD_SETCGRAMADDR = 0x40;
	protected final static int LCD_SETDDRAMADDR = 0x80;

	// flags for display/cursor shift
	protected final static int LCD_DISPLAYMOVE = 0x08;
	protected final static int LCD_CURSORMOVE = 0x00;
	protected final static int LCD_MOVERIGHT = 0x04;
	protected final static int LCD_MOVELEFT = 0x00;

	// flags for display entry mode
	protected final static int LCD_ENTRYRIGHT = 0x00;
	protected final static int LCD_ENTRYLEFT = 0x02;
	protected final static int LCD_ENTRYSHIFTINCREMENT = 0x01;
	protected final static int LCD_ENTRYSHIFTDECREMENT = 0x00;

	// the I/O expander pinout
	protected final static int LCD_CHARACTER_WRITE = 0x80;
	protected final static int LCD_COMMAND = 0x00;
	protected final static int LCD_BACKLIGHT = 0x02;
	protected final static int LCD_ENABLE_PIN = 0x40;
	protected final static int LCD_D4_PIN = 0x20;
	protected final static int LCD_D5_PIN = 0x10;
	protected final static int LCD_D6_PIN = 0x08;
	protected final static int LCD_D7_PIN = 0x04;
	protected final static int LCD_NO_PIN = 0x00;
	// used to identify each active bit from an 8 bit character
	protected final static int[] LCD_DATA_4BITMASK = { 0x80, 0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1 };
	// maps matched bits to 4 bit pins
	protected final static int[] LCD_DATA_4BITPIN = { LCD_D7_PIN, LCD_D6_PIN, LCD_D5_PIN, LCD_D4_PIN, LCD_D7_PIN, LCD_D6_PIN, LCD_D5_PIN, LCD_D4_PIN };

	protected PortExtender ext;
	protected int backlightStatus;
	private int displayControl;
	private int displayMode;

	private static LCD instance;
	public static Logger logger = LogManager.getLogger(LCD.class);

	public static LCD instance() {
		if (instance == null) {
			instance = new LCD();
		}
		return instance;
	}

	private LCD() {
		ext = new PortExtender();
		backlightStatus = LCD_BACKLIGHT;
	}

	protected void init() {
		try {
			synchronized (Thread.currentThread()) {
				// this will get the LCD into the write state to start sending commands
				Thread.sleep(50, 0);
				ext.commandWrite(PortExtender.MCP23008_IODIR, 0x00); // all pins to outputs
				command4bits(LCD_D4_PIN | LCD_D5_PIN);
				Thread.sleep(5, 0);
				command4bits(LCD_D4_PIN | LCD_D5_PIN);
				Thread.sleep(5, 0);
				command4bits(LCD_D4_PIN | LCD_D5_PIN);
				Thread.sleep(1, 0);
				command4bits(LCD_D5_PIN); // set to 4 bit
				command4bits(LCD_D5_PIN, LCD_D6_PIN | LCD_D7_PIN); // set lines and character mode
				command4bits(LCD_NO_PIN, LCD_D7_PIN); // display off
				command4bits(LCD_NO_PIN, LCD_D4_PIN); // display clear
				command4bits(LCD_NO_PIN, LCD_D4_PIN | LCD_D5_PIN | LCD_D6_PIN);

				displayControl = LCD_DISPLAYCONTROL | LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF;
				displayMode = LCD_ENTRYMODESET | LCD_ENTRYLEFT | LCD_ENTRYSHIFTDECREMENT;
				command(displayControl, displayMode);
				// LCD_NO_PIN,0x1c, 0x00,0x18
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	protected void command4bits(int... args) {
		for (int v : args) {
			send(v, LCD_COMMAND);
		}
	}

	protected void command(int... args) {
		for (int v : args) {
			send4bits(v, LCD_COMMAND);
		}
	}

	protected void send(int value, int mode) {
		try {
			synchronized (Thread.currentThread()) {
				ext.digitalWrite(backlightStatus);
				ext.digitalWrite(value | backlightStatus | mode);
				ext.digitalWrite(value | backlightStatus | mode | LCD_ENABLE_PIN);
				Thread.sleep(0, 500);
				ext.digitalWrite(value | backlightStatus | mode);
				Thread.sleep(0, 50000);
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	protected void send4bits(int value, int mode) {
		int bitx4 = 0;
		for (int i = 0; i < LCD_DATA_4BITMASK.length; ++i) {
			if ((LCD_DATA_4BITMASK[i] & value) > 0) {
				bitx4 |= LCD_DATA_4BITPIN[i];
			}
			if (i == 3) {
				send(bitx4, mode);
				bitx4 = 0;
			}
		}
		send(bitx4, mode);
	}

	protected void write(int... args) {
		for (int v : args) {
			send4bits(v, LCD_CHARACTER_WRITE);
		}
	}

	public void write(String s) {
		for (byte v : s.getBytes()) {
			send4bits(v, LCD_CHARACTER_WRITE);
		}
	}

	public void clear() {
		command(LCD_CLEARDISPLAY); // clear display, set cursor position to zero
	}

	public void home() {
		command(LCD_RETURNHOME); // set cursor position to zero
	}

	public void setCursor(int col, int row) {
		int row_offsets[] = { 0x00, 0x40, 0x14, 0x54 };
		if (row > 4) {
			row = 3; // we count rows starting w/0
		}

		command(LCD_SETDDRAMADDR | (col + row_offsets[row]));
	}

	// Turn the display on/off (quickly)
	public void noDisplay() {
		displayControl &= ~LCD_DISPLAYON;
		command(displayControl);
	}

	public void display() {
		displayControl |= LCD_DISPLAYON;
		command(displayControl);
	}

	// Turns the underline cursor on/off
	public void noCursor() {
		displayControl &= ~LCD_CURSORON;
		command(displayControl);
	}

	public void cursor() {
		displayControl |= LCD_CURSORON;
		command(displayControl);
	}

	// Turn on and off the blinking cursor
	void noBlink() {
		displayControl &= ~LCD_BLINKON;
		command(displayControl);
	}

	public void blink() {
		displayControl |= LCD_BLINKON;
		command(displayControl);
	}

	// These commands scroll the display without changing the RAM
	public void scrollDisplayLeft() {
		command(LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVELEFT);
	}

	public void scrollDisplayRight() {
		command(LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVERIGHT);
	}

	// This is for text that flows Left to Right
	public void leftToRight() {
		displayMode |= LCD_ENTRYLEFT;
		command(displayMode);
	}

	// This is for text that flows Right to Left
	public void rightToLeft() {
		displayMode &= ~LCD_ENTRYLEFT;
		command(displayMode);
	}

	// This will 'right justify' text from the cursor
	public void autoscroll() {
		displayMode |= LCD_ENTRYSHIFTINCREMENT;
		command(displayMode);
	}

	// This will 'left justify' text from the cursor
	public void noAutoscroll() {
		displayMode &= ~LCD_ENTRYSHIFTINCREMENT;
		command(displayMode);
	}

	// Allows us to fill the first 8 CGRAM locations
	// with custom characters
	public void createChar(int location, int charmap[]) {
		location &= 0x7; // we only have 8 locations 0-7
		command(LCD_SETCGRAMADDR | (location << 3));
		for (int i = 0; i < 8; i++) {
			write(charmap[i]);
		}
	}

	public void enableBacklight() {
		backlightStatus = LCD_BACKLIGHT;
		ext.digitalWrite(backlightStatus);
	}

	public void disableBacklight() {
		backlightStatus = 0x00;
		ext.digitalWrite(backlightStatus);
	}

	protected static class PortExtender {

		// registers
		public static final int MCP23008_IODIR = 0x00;
		public static final int MCP23008_IPOL = 0x01;
		public static final int MCP23008_GPINTEN = 0x02;
		public static final int MCP23008_DEFVAL = 0x03;
		public static final int MCP23008_INTCON = 0x04;
		public static final int MCP23008_IOCON = 0x05;
		public static final int MCP23008_GPPU = 0x06;
		public static final int MCP23008_INTF = 0x07;
		public static final int MCP23008_INTCAP = 0x08;
		public static final int MCP23008_GPIO = 0x09;
		public static final int MCP23008_OLAT = 0x0A;

		public static final String DESCRIPTION = "MCP23008 GPIO Provider";
		private I2CDevice device;

		public PortExtender() {

			// create I2C communications bus instance
			for (int i = 0; i < 5; ++i) {
				try {
					device = I2CFactory.getInstance(i).getDevice(MCP23008_ADDRESS);
					logger.trace("Found I2C device on bus " + i);
					break;
				} catch (IOException ex) {
				}
			}

		}

		public void digitalWrite(int d) {
			try {
				// System.out.println("Sending : " + d + " : " + Integer.toBinaryString(d) + " : " + Integer.toHexString(d));
				device.write(MCP23008_GPIO, (byte) d);
			} catch (Exception ex) {
				logger.error(ex);
			}
		}

		public void commandWrite(int reg, int d) {
			try {
				device.write(reg, (byte) d);
			} catch (Exception ex) {
				logger.error(ex);
			}
		}

	}
}
