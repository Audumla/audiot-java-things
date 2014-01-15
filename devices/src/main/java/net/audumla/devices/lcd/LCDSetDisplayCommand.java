package net.audumla.devices.lcd;


import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDSetDisplayCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected boolean display;

    public LCDSetDisplayCommand(boolean d) {
        this.display = d;
    }

    public boolean execute(LCD lcd) {
        if (display)
            lcd.display();
        else {
            lcd.noDisplay();
        }
        return true;
    }

}
