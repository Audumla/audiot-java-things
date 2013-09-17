package net.audumla.devices.lcd;

public class LCDWriteCommand implements LCDCommand {
    protected String value;

    public LCDWriteCommand(String v) {
        value = v;
    }

    public void execute(LCD lcd) {
        lcd.write(value);
    }
}