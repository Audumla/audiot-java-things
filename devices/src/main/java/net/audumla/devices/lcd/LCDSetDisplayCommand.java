package net.audumla.devices.lcd;


import net.audumla.devices.event.AbstractEvent;
import net.audumla.devices.event.CommandEvent;

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

    @Override
    public boolean rollback(LCD lcd) throws Exception {
        return false;
    }


}
