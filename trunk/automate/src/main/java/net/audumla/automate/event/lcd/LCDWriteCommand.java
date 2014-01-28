package net.audumla.automate.event.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDWriteCommand extends AbstractEvent implements CommandEvent<LCD> {
    protected String value;

    public LCDWriteCommand(String v) {
        value = v;
    }

    public LCDWriteCommand() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean execute(LCD lcd) throws Exception {
        lcd.write(value);
        return true;
    }

}
