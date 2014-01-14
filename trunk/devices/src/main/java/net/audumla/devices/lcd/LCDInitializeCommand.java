package net.audumla.devices.lcd;

import net.audumla.devices.event.AbstractEvent;
import net.audumla.devices.event.CommandEvent;

public class LCDInitializeCommand extends AbstractEvent implements CommandEvent<LCD>{

    protected LCDInitializeCommand() {

    }

    public boolean execute(LCD lcd) {
        lcd.initialize();
        return true;
    }
    @Override
    public boolean rollback(LCD lcd) throws Exception {
        return false;
    }

}
