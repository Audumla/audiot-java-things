package net.audumla.automate.event.lcd;


import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDSetDisplayCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected boolean display;

    public LCDSetDisplayCommand() {
    }

    public LCDSetDisplayCommand(boolean d) {
        this.display = d;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
    @Override
    public boolean execute(LCD lcd) throws Exception {
        if (display)
            lcd.display();
        else {
            lcd.noDisplay();
        }
        return true;
    }

}
