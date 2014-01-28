package net.audumla.automate.event.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDSetCursorCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected int col;
    protected int row;

    public LCDSetCursorCommand() {
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public LCDSetCursorCommand(int c, int r) {
        col = c;
        row = r;
    }
    @Override
    public boolean execute(LCD lcd) throws Exception {
        lcd.setCursor(col, row);
        return true;
    }



}
