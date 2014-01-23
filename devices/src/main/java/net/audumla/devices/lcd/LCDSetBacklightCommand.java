package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

import java.io.IOException;

public class LCDSetBacklightCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected boolean backlight;

    public LCDSetBacklightCommand(boolean b) {
        backlight = b;
    }

    public boolean execute(LCD lcd) throws IOException {
        if (backlight)
            lcd.enableBacklight();
        else
            lcd.disableBacklight();
        return true;
    }



}
