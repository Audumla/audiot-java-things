package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDSetCursorCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected int col;
    protected int row;

    public LCDSetCursorCommand(int c, int r) {
        col = c;
        row = r;
    }

    public boolean execute(LCD lcd) throws Exception {
        lcd.setCursor(col, row);
        return true;
    }



}
