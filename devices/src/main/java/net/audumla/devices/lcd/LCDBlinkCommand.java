package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDBlinkCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected boolean blink;

    public LCDBlinkCommand(boolean b) {
        blink = b;
    }

    public boolean execute(LCD lcd) {
        if (blink)
            lcd.blink();
        else
            lcd.noBlink();
        return true;
    }

    @Override
    public boolean rollback(LCD lcd) throws Exception {
        return false;
    }

}
