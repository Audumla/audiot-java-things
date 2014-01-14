package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDClearCommand extends AbstractEvent implements CommandEvent<LCD> {

    public LCDClearCommand() {

    }

    public boolean execute(LCD lcd) {
        lcd.clear();
        return true;
    }
    @Override
    public boolean rollback(LCD lcd) throws Exception {
        return false;
    }

}
