package net.audumla.devices.lcd.rpi;

import com.pi4j.jni.I2C;
import net.audumla.devices.io.channel.*;
import net.audumla.devices.io.channel.gpio.MCP2308DeviceChannel;
import net.audumla.devices.io.channel.i2c.RPiI2CChannel;
import net.audumla.devices.lcd.LCD;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class RPII2CLCD implements net.audumla.devices.lcd.LCD {
    public static final byte DEFAULT_ADDRESS = 0x20;

    protected final static byte LCD_8BITMODE = 0x10;
    protected final static byte LCD_4BITMODE = 0x00;
    protected final static byte LCD_2LINE = 0x08;
    protected final static byte LCD_1LINE = 0x00;
    protected final static byte LCD_5x10DOTS = 0x04;
    protected final static byte LCD_5x8DOTS = 0x00;

    // flags for display on/off control
    protected final static byte LCD_DISPLAYON = 0x04;
    protected final static byte LCD_DISPLAYOFF = 0x00;
    protected final static byte LCD_CURSORON = 0x02;
    protected final static byte LCD_CURSOROFF = 0x00;
    protected final static byte LCD_BLINKON = 0x01;
    protected final static byte LCD_BLINKOFF = 0x00;

    // commands
    protected final static byte LCD_FUNCTIONSET = 0x20;
    protected final static byte LCD_CLEARDISPLAY = 0x01;
    protected final static byte LCD_RETURNHOME = 0x02;
    protected final static byte LCD_ENTRYMODESET = 0x04;
    protected final static byte LCD_DISPLAYCONTROL = 0x08;
    protected final static byte LCD_CURSORSHIFT = 0x10;
    protected final static byte LCD_SETCGRAMADDR = 0x40;
    protected final static byte LCD_SETDDRAMADDR = (byte) 0x80;

    // flags for display/cursor shift
    protected final static byte LCD_DISPLAYMOVE = 0x08;
    protected final static byte LCD_CURSORMOVE = 0x00;
    protected final static byte LCD_MOVERIGHT = 0x04;
    protected final static byte LCD_MOVELEFT = 0x00;

    // flags for display entry mode
    protected final static byte LCD_ENTRYRIGHT = 0x00;
    protected final static byte LCD_ENTRYLEFT = 0x02;
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
    // used to identify each active bit from an 8 bit character
    protected final static int[] LCD_DATA_4BITMASK = {0x80, 0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1};
    // maps matched bits to 4 bit pins
    protected final static int[] LCD_DATA_4BITPIN = {LCD_D7_PIN, LCD_D6_PIN, LCD_D5_PIN, LCD_D4_PIN, LCD_D7_PIN, LCD_D6_PIN, LCD_D5_PIN, LCD_D4_PIN};
    private final String name;
    private final int address;

    private byte backlightStatus;
    private byte displayControl;
    private byte displayMode;
    private DeviceChannel baseDeviceChannel;

    private static Map<Integer, net.audumla.devices.lcd.LCD> instances = new HashMap<Integer, net.audumla.devices.lcd.LCD>();
    public static Logger logger = Logger.getLogger(RPII2CLCD.class);

    public static LCD instance(String name, int address) {
        LCD instance = instances.get(address);
        if (instance == null) {
            instance = new RPII2CLCD(name, address);
        }
        return instance;
    }

    private RPII2CLCD(String name, int address) {
        this.name = name;
        this.address = address;
        baseDeviceChannel = new RPiI2CChannel().createChannel(new ChannelAddressAttr(1), new DeviceAddressAttr(address));
        backlightStatus = LCD_BACKLIGHT;
    }

    @Override
    public boolean initialize() {
        try {
            synchronized (Thread.currentThread()) {
                displayControl = LCD_DISPLAYCONTROL | LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF;
                displayMode = LCD_ENTRYMODESET | LCD_ENTRYLEFT | LCD_ENTRYSHIFTDECREMENT;
                // this will get the RPII2CLCD into the write state to start sending commands
//                Thread.sleep(50, 0);
//                baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_IODIR)).write((byte) 0x00);
//                commandWrite(MCP2308DeviceChannel.MCP23008_IODIR, 0x00); // all pins to outputs

                ByteBuffer bb = ByteBuffer.allocate(100);
                DeviceChannel initChannel = baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_IODIR));
                bb.put((byte) 0x00);
                initChannel.setAttribute(bb, new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO));
                putCommand8bits(bb, initChannel, LCD_COMMAND, (byte) (LCD_D4_PIN | LCD_D5_PIN));
                initChannel.setAttribute(bb, new SleepAttr(5));
                putCommand8bits(bb, initChannel, LCD_COMMAND, (byte) (LCD_D4_PIN | LCD_D5_PIN));
                initChannel.setAttribute(bb, new SleepAttr(5));
                putCommand8bits(bb, initChannel, LCD_COMMAND, (byte) (LCD_D4_PIN | LCD_D5_PIN));
                initChannel.setAttribute(bb, new SleepAttr(1));
                putCommand8bits(bb, initChannel, LCD_COMMAND,
                        LCD_D5_PIN,
                        LCD_D5_PIN,
                        (byte) (LCD_D6_PIN | LCD_D7_PIN),
                        LCD_NO_PIN, LCD_D7_PIN,
                        LCD_NO_PIN, LCD_D4_PIN,
                        LCD_NO_PIN,
                        (byte) (LCD_D4_PIN | LCD_D5_PIN | LCD_D6_PIN));
                putCommand4bits(bb, initChannel, LCD_COMMAND,
                        displayControl,
                        displayMode);
                bb.flip();
                initChannel.write(bb);

//                command4bits((byte) (LCD_D4_PIN | LCD_D5_PIN));
//                Thread.sleep(5, 0);
//                command4bits((byte) (LCD_D4_PIN | LCD_D5_PIN));
//                Thread.sleep(5, 0);
//                command4bits((byte) (LCD_D4_PIN | LCD_D5_PIN));
//                Thread.sleep(1, 0);
//                command4bits(LCD_D5_PIN); // set to 4 bit
//                command4bits(LCD_D5_PIN, (byte) (LCD_D6_PIN | LCD_D7_PIN)); // set lines and character mode
//                command4bits(LCD_NO_PIN, LCD_D7_PIN); // display off
//                command4bits(LCD_NO_PIN, LCD_D4_PIN); // display clear
//                command4bits(LCD_NO_PIN, (byte) (LCD_D4_PIN | LCD_D5_PIN | LCD_D6_PIN));
//
//                command(displayControl, displayMode);
                // LCD_NO_PIN,0x1c, 0x00,0x18
            }
        } catch (Exception ex) {
            logger.error("Cannot initialize LCD [" + getName() + "]", ex);
            return false;
        }
        return true;
    }

    private String getName() {
        return name;
    }

//    protected void command4bits(byte... args) throws Exception {
//        for (byte v : args) {
//            send(v, LCD_COMMAND);
//        }
//    }

    protected void command(byte... args) throws Exception {
        for (byte b : args) {
            send4bits(b, LCD_COMMAND);
        }
    }

//    protected void command(byte... args) throws Exception {
//        DeviceChannel wb = baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO));
//        ByteBuffer bb = ByteBuffer.allocate(args.length*5);
//        putCommand4bits(bb,wb,LCD_COMMAND,args);
//        bb.flip();
//        wb.write(bb);
//    }

    protected void putCommand8bits(ByteBuffer bb, DeviceChannel ch, byte mode, byte... values) throws Exception {
        for (byte value : values) {
            bb.put(backlightStatus).
                    put((byte) (value | backlightStatus | mode)).
                    put((byte) (value | backlightStatus | LCD_ENABLE_PIN | mode));
            ch.setAttribute(bb, new SleepAttr(0, 500));
            bb.put((byte) (value | backlightStatus | mode));
            ch.setAttribute(bb, new SleepAttr(0, 50000));
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

    protected void send(byte value, byte mode) throws Exception {
        synchronized (Thread.currentThread()) {
            digitalWrite(backlightStatus);
            digitalWrite((value | backlightStatus | mode));
            digitalWrite((value | backlightStatus | mode | LCD_ENABLE_PIN));
            logger.debug("Pause 0:500");
            Thread.sleep(0, 500);
            digitalWrite((value | backlightStatus | mode));
            logger.debug("Pause 0:50000");
            Thread.sleep(0, 50000);
        }
    }

    protected void send4bits(byte value, byte mode) throws Exception {
        byte bitx4 = 0;
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

    protected void write(byte... args) throws Exception {
//        for (byte v : args) {
//            send4bits(v, LCD_CHARACTER_WRITE);
//        }
        DeviceChannel wb = baseDeviceChannel.createChannel(new DeviceRegisterAttr(MCP2308DeviceChannel.MCP23008_GPIO));
        ByteBuffer bb = ByteBuffer.allocate(args.length*8);
        putCommand4bits(bb,wb,LCD_CHARACTER_WRITE,args);
        bb.flip();
        logger.debug("Write to "+wb);
        wb.write(bb);
    }

    @Override
    public void write(String s) throws Exception {
        logger.debug("LCD Write - '"+s+"'");
        write(s.getBytes());
    }

    @Override
    public void clear() throws Exception {
        command(LCD_CLEARDISPLAY); // clear display, set cursor position to zero
    }

    @Override
    public void home() throws Exception {
        command(LCD_RETURNHOME); // set cursor position to zero
    }

    @Override
    public void setCursor(int col, int row) throws Exception {
        int row_offsets[] = {0x00, 0x40, 0x14, 0x54};
        if (row > 4) {
            row = 3; // we count rows starting w/0
        }

        command((byte) (LCD_SETDDRAMADDR | (col + row_offsets[row])));
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
        command((byte) (LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVELEFT));
    }

    @Override
    public void scrollDisplayRight() throws Exception {
        command((byte) (LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVERIGHT));
    }

    // This is for text that flows Left to Right
    @Override
    public void leftToRight() throws Exception {
        displayMode |= LCD_ENTRYLEFT;
        command(displayMode);
    }

    // This is for text that flows Right to Left
    @Override
    public void rightToLeft() throws Exception {
        displayMode &= ~LCD_ENTRYLEFT;
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
        command((byte) (LCD_SETCGRAMADDR | (location << 3)));
        for (byte i = 0; i < 8; i++) {
            write(charmap[i]);
        }
    }

    @Override
    public void enableBacklight() throws IOException {
        backlightStatus = LCD_BACKLIGHT;
        digitalWrite(backlightStatus);
    }

    @Override
    public void disableBacklight() throws IOException {
        backlightStatus = 0x00;
        digitalWrite(backlightStatus);
    }

    public void digitalWrite(int d) throws IOException {
        logger.debug("Write GPIO "+(byte)d);

        I2C.i2cWriteByte(RPiI2CChannel.getBusHandle(1), address, MCP2308DeviceChannel.MCP23008_GPIO, (byte) d);
    }

//    public void commandWrite(int reg, int d) throws IOException {
//        I2C.i2cWriteByte(RPiI2CChannel.getBusHandle(1), address, reg, (byte) d);
//    }
}
