package net.audumla.devices.lcd;

import net.audumla.devices.io.channel.*;
import net.audumla.devices.io.channel.gpio.MCP2308DeviceChannel;
import net.audumla.devices.io.channel.i2c.RPiI2CChannel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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
    protected final static byte LCD_CURSORDISPLAYSHIFT_COMMAND = 0x10;
    protected final static byte LCD_SHIFTDISPLAY = 0x08;
    protected final static byte LCD_SHIFTCURSOR = 0x00;
    protected final static byte LCD_SHIFTRIGHT = 0x04;
    protected final static byte LCD_SHIFTLEFT = 0x00;

    // flags for display entry mode
    protected final static byte LCD_ENTRYMODESET_COMMAND = 0x04;
    protected final static byte LCD_ENTRYSHIFTRIGHT = 0x00;
    protected final static byte LCD_ENTRYSHIFTLEFT = 0x02;
    protected final static byte LCD_ENTRYSHIFTINCREMENT = 0x01;
    protected final static byte LCD_ENTRYSHIFTDECREMENT = 0x00;

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
    private byte displayMode;
    private DeviceChannel baseDeviceChannel;

    private static Map<Integer, CharacterLCD> instances = new HashMap<Integer, CharacterLCD>();
    public static Logger logger = Logger.getLogger(HitachiCharacterLCD.class);
    private int columns = 20;
    private int rows = 4;

    public static CharacterLCD instance(String name, int address) {
        CharacterLCD instance = instances.get(address);
        if (instance == null) {
            instance = new HitachiCharacterLCD(name, address);
        }
        return instance;
    }

    private HitachiCharacterLCD(String name, int address) {
        this.name = name;
        baseDeviceChannel = new RPiI2CChannel().createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(address));
        backlightStatus = LCD_BACKLIGHT;
    }

    protected void reset(ByteBuffer bb, DeviceChannel ch) {
        bb.put((byte) 0xFF);
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

    protected void init(ByteBuffer bb, DeviceChannel ch) throws Exception {
        putCommand4bits(bb, ch, LCD_COMMAND, (byte) (LCD_FUNCTIONSET_COMMAND | LCD_2LINE | LCD_5x10DOTS)); // set to 4 bit interface - 2 lines - 5x10 font
        putCommand4bits(bb, ch, LCD_COMMAND, (byte) (LCD_DISPLAYCONTROL_COMMAND | LCD_DISPLAYOFF)); // display off - no cursor - no blink
        putCommand4bits(bb, ch, LCD_COMMAND, (byte) (LCD_RETURNHOME_COMMAND)); // display off - no cursor - no blink
        putCommand4bits(bb, ch, LCD_COMMAND, (byte) (LCD_CLEARDISPLAY_COMMAND)); // display clear
        putCommand4bits(bb, ch, LCD_COMMAND, (byte) (LCD_DISPLAYCONTROL_COMMAND | LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF)); // display on - no cursor - no blink
        putCommand4bits(bb, ch, LCD_COMMAND, (byte) (LCD_ENTRYMODESET_COMMAND | LCD_ENTRYSHIFTINCREMENT | LCD_ENTRYSHIFTRIGHT)); // display on - no cursor - no blink
    }

    @Override
    public boolean initialize() {
        try {
            synchronized (Thread.currentThread()) {
                displayControl = LCD_DISPLAYCONTROL_COMMAND | LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF;
                displayMode = LCD_ENTRYMODESET_COMMAND | LCD_ENTRYSHIFTINCREMENT | LCD_ENTRYSHIFTRIGHT;
                //see http://www.adafruit.com/datasheets/HD44780.pdf page 46 for initialization of 4 bit interface
                ByteBuffer bb = ByteBuffer.allocate(100);
                DeviceChannel initChannel = baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_IODIR));
                bb.put((byte) 0x00); // set MCP to output pins
                initChannel.setAttribute(bb, new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO));
                reset(bb, initChannel);
                init(bb, initChannel);
                bb.flip();
                initChannel.write(bb);


            }
        } catch (Exception ex) {
            logger.error("Cannot initialize LCD [" + getName() + "]", ex);
            return false;
        }
        return true;
    }

    @Override
    public void setDisplaySize(int rows, int cols) {
        this.rows = rows;
        this.columns = cols;
    }

    private String getName() {
        return name;
    }


    protected void putCommand8bits(ByteBuffer bb, DeviceChannel ch, byte mode, byte... values) throws Exception {
        for (byte value : values) {
            bb.put((byte) (value | backlightStatus | LCD_ENABLE_PIN | mode)).put((byte) (value | backlightStatus | mode));
            ch.setAttribute(bb, new FixedWaitAttr(0, 500));
//            bb.put((byte) (value | backlightStatus | mode));
//            ch.setAttribute(bb, new FixedWaitAttr(0, 50000));
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
        DeviceChannel wb = baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO));
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
        logger.debug("write: " + s);
        if (s.length() > columns) {
              s = s.substring(0, Math.min(columns,s.length()));
//            for (int i = 0; i < Math.ceil((double) s.length() / (double) (columns)); ++i) {
//                setCursor(col, row + i);
//                String ss = s.substring(i * (columns), Math.min((i + 1) * (columns), s.length()));
//                logger.debug("Row:" + (row + i) + " col:" + col + " '" + ss + "'");
//                write(ss.getBytes());
            }
//        } else {
            setCursor(col, row);
            write(s.getBytes());
//        }
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
    public void setCursor(int col, int row) throws Exception {
        int row_offsets[] = {0x00, 0x40, 0x14, 0x54};
        if (row >= rows) {
            row = rows - 1; // we count rows starting w/0
        }

        command((byte) (LCD_SETDDRAMADDR_COMMAND | (col + row_offsets[row])));
    }

    // Turn the display on/off (quickly)
    @Override
    public void noDisplay() throws Exception {
        displayControl &= ~LCD_DISPLAYON;
        command(displayControl);
    }

    @Override
    public void display() throws Exception {
        displayControl |= LCD_DISPLAYON;
        command(displayControl);
    }

    // Turns the underline cursor on/off
    @Override
    public void noCursor() throws Exception {
        displayControl &= ~LCD_CURSORON;
        command(displayControl);
    }

    @Override
    public void cursor() throws Exception {
        displayControl |= LCD_CURSORON;
        command(displayControl);
    }

    // Turn on and off the blinking cursor
    @Override
    public void noBlink() throws Exception {
        displayControl &= ~LCD_BLINKON;
        command(displayControl);
    }

    @Override
    public void blink() throws Exception {
        displayControl |= LCD_BLINKON;
        command(displayControl);
    }

    // These commands scroll the display without changing the RAM
    @Override
    public void scrollDisplayLeft() throws Exception {
        command((byte) (LCD_CURSORDISPLAYSHIFT_COMMAND | LCD_SHIFTDISPLAY | LCD_SHIFTLEFT));
    }

    @Override
    public void scrollDisplayRight() throws Exception {
        command((byte) (LCD_CURSORDISPLAYSHIFT_COMMAND | LCD_SHIFTDISPLAY | LCD_SHIFTRIGHT));
    }

    // This is for text that flows Left to Right
    @Override
    public void leftToRight() throws Exception {
        displayMode |= LCD_ENTRYSHIFTLEFT;
        command(displayMode);
    }

    // This is for text that flows Right to Left
    @Override
    public void rightToLeft() throws Exception {
        displayMode &= ~LCD_ENTRYSHIFTLEFT;
        command(displayMode);
    }

    // This will 'right justify' text from the cursor
    @Override
    public void autoscroll() throws Exception {
        displayMode |= LCD_ENTRYSHIFTINCREMENT;
        command(displayMode);
    }

    // This will 'left justify' text from the cursor
    @Override
    public void noAutoscroll() throws Exception {
        displayMode &= ~LCD_ENTRYSHIFTINCREMENT;
        command(displayMode);
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
        baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO)).write(backlightStatus);
    }

    @Override
    public void disableBacklight() throws IOException {
        backlightStatus = 0x00;
        baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO)).write(backlightStatus);
    }

}
