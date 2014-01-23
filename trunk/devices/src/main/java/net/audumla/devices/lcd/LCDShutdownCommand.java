package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDShutdownCommand extends AbstractEvent implements CommandEvent<LCD> {


    public LCDShutdownCommand() {
    }

    @Override
    public boolean execute(LCD lcd) {
//		lcd.clear();
//		lcd.write("System shutdown");
        return false;
    }

}
