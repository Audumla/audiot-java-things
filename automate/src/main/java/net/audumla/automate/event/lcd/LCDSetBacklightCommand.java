package net.audumla.automate.event.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

import java.io.IOException;

public class LCDSetBacklightCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected boolean backlight;


    public LCDSetBacklightCommand() {
    }

    public boolean isBacklight() {
        return backlight;
    }

    public void setBacklight(boolean backlight) {
        this.backlight = backlight;
    }

    public LCDSetBacklightCommand(boolean b) {
        backlight = b;
    }
    @Override
    public boolean execute(LCD lcd) throws IOException {
        if (backlight)
            lcd.enableBacklight();
        else
            lcd.disableBacklight();
        return true;
    }



}
