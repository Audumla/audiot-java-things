package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDInitializeCommand extends AbstractEvent implements CommandEvent<LCD>{

    protected LCDInitializeCommand() {

    }
    @Override
    public boolean execute(LCD lcd) {
        lcd.initialize();
        return true;
    }

}
