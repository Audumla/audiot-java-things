package net.audumla.devices.lcd;

import net.audumla.devices.event.AbstractEvent;
import net.audumla.devices.event.CommandEvent;

public class LCDClearCommand extends AbstractEvent implements CommandEvent<LCD> {

    public LCDClearCommand() {

    }

    public boolean execute(LCD lcd) {
        lcd.clear();
        return true;
    }

}
