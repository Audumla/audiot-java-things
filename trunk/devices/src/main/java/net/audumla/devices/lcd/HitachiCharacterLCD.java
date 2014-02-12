package net.audumla.devices.lcd;

import net.audumla.devices.io.channel.*;
import net.audumla.devices.io.channel.gpio.MCP2308DeviceChannel;
import net.audumla.devices.io.channel.i2c.I2CDeviceChannel;
import net.audumla.devices.io.i2c.RPiI2CDeviceFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HitachiCharacterLCD implements CharacterLCD {
    public static final byte DEFAULT_ADDRESS = 0x20;

    protected final static byte LCD_FUNCTIONSET_COMMAND = 0x20;
    protected final static byte LCD_8BITMODE = 0x10;
    protected final static byte LCD_4BITMODE = 0x00;
    protected final static byte LCD_2LINE = 0x08;
    protected final static byte LCD_1LINE = 0x00;
    protected final static byte LCD_5x10DOTS = 0x04;
    protected final static byte LCD_5x8DOTS = 0x00;

    // flags for display on/off control
    protected final static byte LCD_DISPLAYCONTROL_COMMAND = 0x08;
    protected final static byte LCD_DISPLAYON = 0x04;
    protected final static byte LCD_DISPLAYOFF = 0x00;
    protected final static byte LCD_CURSORON = 0x02;
    protected final static byte LCD_CURSOROFF = 0x00;
    protected final static byte LCD_BLINKON = 0x01;
    protected final static byte LCD_BLINKOFF = 0x00;

    // commands
    protected final static byte LCD_CLEARDISPLAY_COMMAND = 0x01;

    protected final static byte LCD_RETURNHOME_COMMAND = 0x02;

    protected final static byte LCD_SETCGRAMADDR_COMMAND = 0x40;
    protected final static byte LCD_SETDDRAMADDR_COMMAND = (byte) 0x80;

    // flags for display/cursor shift
    protected final static byte LCD_CURSORDISPLAY_SHIFT_COMMAND = 0x10;
    protected final static byte LCD_SHIFT_DISPLAY_RIGHT = 0x08 + 0x04;
    protected final static byte LCD_SHIFT_DISPLAY_LEFT = 0x08;
    protected final static byte LCD_SHIFT_CURSOR_LEFT = 0x00;
    protected final static byte LCD_SHIFT_CURSOR_RIGHT = 0x04;

    // flags for display entry mode
    protected final static byte LCD_ENTRYMODESET_COMMAND = 0x04;
    protected final static byte LCD_ENTRYMODE_INCREMENT_CURSOR = 0x02;
    protected final static byte LCD_ENTRYMODE_SCROLL_DISPLAY = 0x01;

    // the I/O expander pinout
    protected final static byte LCD_CHARACTER_WRITE = (byte) 0x80;
    protected final static byte LCD_COMMAND = 0x00;
    protected final static byte LCD_BACKLIGHT = 0x02;
    protected final static byte LCD_ENABLE_PIN = 0x40;
    protected final static byte LCD_D4_PIN = 0x20;
    protected final static byte LCD_D5_PIN = 0x10;
    protected final static byte LCD_D6_PIN = 0x08;
    protected final static byte LCD_D7_PIN = 0x04;
    protected final static byte LCD_NO_PIN = 0x00;
    protected final static byte LCD_READWRITE = 0x01;
    // used to identify each active bit from an 8 bit character
    protected final static int[] LCD_DATA_4BITMASK = {0x80, 0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1};
    // maps matched bits to 4 bit pins
    protected final static int[] LCD_DATA_4BITPIN = {LCD_D7_PIN, LCD_D6_PIN, LCD_D5_PIN, LCD_D4_PIN, LCD_D7_PIN, LCD_D6_PIN, LCD_D5_PIN, LCD_D4_PIN};
    private final String name;

    private byte backlightStatus;
    private byte displayControl;
    private byte writeMode;
    private DeviceChannel baseDeviceChannel;

    private DeviceChannel ioChannel; // the channel used to read and write to the LCD interface

    public static Logger logger = Logger.getLogger(HitachiCharacterLCD.class);
    private int columns = 20;
    private int rows = 4;

    public HitachiCharacterLCD(String name, int address) {
        this.name = name;
        try {
            baseDeviceChannel = new I2CDeviceChannel(new RPiI2CDeviceFactory(),new ChannelAddressAttr(1), new DeviceAddressAttr(address), new DeviceWriteRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO));
        } catch (IOException e) {
            logger.error("Unable to create channel",e);
        }
        backlightStatus = LCD_BACKLIGHT;
    }

    protected void reset(ByteBuffer bb, DeviceChannel ch) throws Exception {
        ch.setAttribute(bb, new FixedWaitAttr(20));
        bb.put((byte) (LCD_D4_PIN | LCD_D5_PIN | LCD_ENABLE_PIN));
        bb.put((byte) (LCD_D4_PIN | LCD_D5_PIN));
        ch.setAttribute(bb, new FixedWaitAttr(5));
        bb.put((byte) (LCD_D4_PIN | LCD_D5_PIN | LCD_ENABLE_PIN));
        bb.put((byte) (LCD_D4_PIN | LCD_D5_PIN));
        ch.setAttribute(bb, new FixedWaitAttr(0, 100));
        bb.put((byte) (LCD_D4_PIN | LCD_D5_PIN | LCD_ENABLE_PIN));
        bb.put((byte) (LCD_D4_PIN | LCD_D5_PIN));
        bb.put((byte) (LCD_D5_PIN | LCD_ENABLE_PIN));
        bb.put((byte) (LCD_D5_PIN));
    }

    @Override
    public boolean initialize(int rows, int cols) {
        try {
            this.rows = rows;
            this.columns = cols;
            displayControl = LCD_DISPLAYCONTROL_COMMAND | LCD_DISPLAYON;
            writeMode = LCD_ENTRYMODESET_COMMAND | LCD_ENTRYMODE_INCREMENT_CURSOR;
            //see http://www.adafruit.com/datasheets/HD44780.pdf page 46 for initialization of 4 bit interface
            baseDeviceChannel.write( (byte) 0x00, new DeviceWriteRegisterAttr(MCP2308DeviceChannel.MCP23008_IODIR));
            DeviceChannel initChannel = baseDeviceChannel.createChannel();
            ByteBuffer bb = ByteBuffer.allocateDirect(100);
            reset(bb, initChannel);
            putCommand4bits(bb, initChannel, LCD_COMMAND, (byte) (LCD_FUNCTIONSET_COMMAND | (rows > 1 ? LCD_2LINE : LCD_1LINE) | (rows > 1 ? LCD_5x8DOTS : LCD_5x10DOTS))); // set to 4 bit interface - 2 lines - 5x10 font
            putCommand4bits(bb, initChannel, LCD_COMMAND, (byte) (LCD_DISPLAYCONTROL_COMMAND));
            putCommand4bits(bb, initChannel, LCD_COMMAND, (byte) (LCD_RETURNHOME_COMMAND));
            putCommand4bits(bb, initChannel, LCD_COMMAND, (byte) (LCD_CLEARDISPLAY_COMMAND)); // display clear
            putCommand4bits(bb, initChannel, LCD_COMMAND, (byte) displayControl);
            putCommand4bits(bb, initChannel, LCD_COMMAND, (byte) writeMode);
            bb.flip();
            initChannel.write(bb);
        } catch (Exception ex) {
            logger.error("Cannot initialize LCD [" + getName() + "]", ex);
            return false;
        }
        return true;
    }

    private String getName() {
        return name;
    }

    protected void putCommand8bits(ByteBuffer bb, DeviceChannel ch, byte mode, byte... values) throws Exception {
        for (byte value : values) {
            bb.put((byte) (value | backlightStatus | LCD_ENABLE_PIN | mode));
            bb.put((byte) (value | backlightStatus | mode));
            ch.setAttribute(bb, new FixedWaitAttr(0, 400));
        }
    }

    protected void putCommand4bits(ByteBuffer bb, DeviceChannel ch, byte mode, byte... values) throws Exception {
        for (byte value : values) {
            byte bitx4 = 0;
            for (int i = 0; i < LCD_DATA_4BITMASK.length; ++i) {
                if ((LCD_DATA_4BITMASK[i] & value) > 0) {
                    bitx4 |= LCD_DATA_4BITPIN[i];
                }
                if (i == 3) {
                    putCommand8bits(bb, ch, mode, bitx4);
                    bitx4 = 0;
                }
            }
            putCommand8bits(bb, ch, mode, bitx4);
        }
    }

    protected void commandByMode(byte mode, byte... args) throws Exception {
        DeviceChannel wb = baseDeviceChannel.createChannel();
        ByteBuffer bb = ByteBuffer.allocate(args.length * 8);
        putCommand4bits(bb, wb, mode, args);
        bb.flip();
        wb.write(bb);
    }

    protected void command(byte... args) throws Exception {
        commandByMode(LCD_COMMAND, args);
    }

    protected void write(byte... args) throws Exception {
        commandByMode(LCD_CHARACTER_WRITE, args);
    }

    @Override
    public void write(String s) throws Exception {
        write(s.getBytes());
    }

    @Override
    public void write(int row, int col, String s) throws Exception {
//        logger.debug("collect: " + s);
        if (s.length() > columns) {
            for (int i = 0; i < Math.ceil((double) s.length() / (double) (columns)); ++i) {
                setCursorPosition(row + i, col);
                String ss = s.substring(i * (columns), Math.min((i + 1) * (columns), s.length()));
                write(ss.getBytes());
            }
        } else {
            setCursorPosition(row, col);
            write(s.getBytes());
        }
    }

    @Override
    public void clear() throws Exception {
        command(LCD_CLEARDISPLAY_COMMAND); // clear display, set cursor position to zero
    }

    @Override
    public void home() throws Exception {
        command(LCD_RETURNHOME_COMMAND); // set cursor position to zero
    }

    @Override
    public void setCursorPosition(int row, int col) throws Exception {
        int row_offsets[] = {0x00, 0x40, 0x14, 0x54};
        if (row >= rows) {
            row = rows - 1; // we count rows starting w/0
        }
        command((byte) (LCD_SETDDRAMADDR_COMMAND | (col + row_offsets[row])));
    }

    @Override
    public void enableDisplay(boolean enable) throws Exception {
        command(enable ? (displayControl |= LCD_DISPLAYON) : (displayControl &= ~LCD_DISPLAYON));
    }

    @Override
    public void displayCursor(boolean cursor) throws Exception {
        command(cursor ? (displayControl |= LCD_CURSORON) : (displayControl &= ~LCD_CURSORON));
    }

    @Override
    public void blinkCursor(boolean blink) throws Exception {
        command(blink ? (displayControl |= LCD_BLINKON) : (displayControl &= ~LCD_BLINKON));
    }

    // These commands scroll the display without changing the RAM
    @Override
    public void scrollDisplayLeft() throws Exception {
        command((byte) (LCD_CURSORDISPLAY_SHIFT_COMMAND | LCD_SHIFT_DISPLAY_LEFT));
    }

    @Override
    public void scrollDisplayRight() throws Exception {
        command((byte) (LCD_CURSORDISPLAY_SHIFT_COMMAND | LCD_SHIFT_DISPLAY_RIGHT));
    }

    // This is for text that flows Left to Right
    @Override
    public void autoIncrementCursor() throws Exception {
        command(writeMode |= LCD_ENTRYMODE_INCREMENT_CURSOR);
    }

    // This is for text that flows Right to Left
    @Override
    public void autoDecrementCursor() throws Exception {
        command(writeMode &= ~LCD_ENTRYMODE_INCREMENT_CURSOR);
    }

    @Override
    public void incrementCursor() throws Exception {
        command((byte) (LCD_CURSORDISPLAY_SHIFT_COMMAND | LCD_SHIFT_CURSOR_RIGHT));
    }

    @Override
    public void decrementCursor() throws Exception {
        command((byte) (LCD_CURSORDISPLAY_SHIFT_COMMAND));
    }

    // This will 'right justify' text from the cursor
    @Override
    public void autoScrollDisplay(boolean scroll) throws Exception {
        command(scroll ? (writeMode |= LCD_ENTRYMODE_SCROLL_DISPLAY) : (writeMode &= ~LCD_ENTRYMODE_SCROLL_DISPLAY));
    }

    // Allows us to fill the first 8 CGRAM locations
    // with custom characters
    public void createChar(int location, byte charmap[]) throws Exception {
        location &= 0x7; // we only have 8 locations 0-7
        command((byte) (LCD_SETCGRAMADDR_COMMAND | (location << 3)));
        for (byte i = 0; i < 8; i++) {
            write(charmap[i]);
        }
    }

    @Override
    public void enableBacklight() throws IOException {
        backlightStatus = LCD_BACKLIGHT;
        baseDeviceChannel.write(backlightStatus);
    }

    @Override
    public void disableBacklight() throws IOException {
        backlightStatus = 0x00;
        baseDeviceChannel.write(backlightStatus);
    }

}
