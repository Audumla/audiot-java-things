package net.audumla.devices.lcd;

import net.audumla.devices.event.AbstractEvent;
import net.audumla.devices.event.CommandEvent;

public class LCDWriteCommand extends AbstractEvent implements CommandEvent<LCD> {
    protected String value;

    public LCDWriteCommand(String v) {
        value = v;
    }

    public boolean execute(LCD lcd) {
        lcd.write(value);
        return true;
    }
}
