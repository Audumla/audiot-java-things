package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDSetBacklightCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected boolean backlight;

    public LCDSetBacklightCommand(boolean b) {
        backlight = b;
    }

    public boolean execute(LCD lcd) {
        if (backlight)
            lcd.enableBacklight();
        else
            lcd.disableBacklight();
        return true;
    }

    @Override
    public boolean rollback(LCD lcd) throws Exception {
        return false;
    }


}
