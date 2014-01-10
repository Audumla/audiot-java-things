package net.audumla.devices.lcd;

import net.audumla.devices.event.AbstractEvent;
import net.audumla.devices.event.CommandEvent;

public class LCDSetCursorCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected int col;
    protected int row;

    public LCDSetCursorCommand(int c, int r) {
        col = c;
        row = r;
    }

    public boolean execute(LCD lcd) {
        lcd.setCursor(col, row);
        return true;
    }

}
