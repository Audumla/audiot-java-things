package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDBlinkCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected boolean blink;

    public LCDBlinkCommand() {
    }

    public boolean isBlink() {
        return blink;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

    public LCDBlinkCommand(boolean b) {
        blink = b;
    }

    public boolean execute(LCD lcd) throws Exception {
        if (blink)
            lcd.blink();
        else
            lcd.noBlink();
        return true;
    }

}
