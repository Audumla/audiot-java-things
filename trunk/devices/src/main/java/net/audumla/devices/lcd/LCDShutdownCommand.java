package net.audumla.devices.lcd;

import net.audumla.devices.event.AbstractEvent;
import net.audumla.devices.event.CommandEvent;

public class LCDShutdownCommand extends AbstractEvent implements CommandEvent<LCD> {

    public boolean execute(LCD lcd) {
//		lcd.clear();
//		lcd.write("System shutdown");
        return false;
    }

    @Override
    public boolean rollback(LCD lcd) throws Exception {
        return false;
    }


}
