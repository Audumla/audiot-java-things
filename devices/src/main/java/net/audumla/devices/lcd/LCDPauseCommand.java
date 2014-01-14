package net.audumla.devices.lcd;

import net.audumla.devices.event.AbstractEvent;
import net.audumla.devices.event.CommandEvent;

public class LCDPauseCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected int msec = 2000;

    public LCDPauseCommand() {
    }

    public LCDPauseCommand(int msec) {
        this.msec = msec;
    }

    public boolean execute(LCD lcd) {
        synchronized (lcd) {
            try {
                lcd.wait(msec);
            } catch (Exception ignored) {
            }

        }
        return true;
    }

    @Override
    public boolean rollback(LCD lcd) throws Exception {
        return false;
    }

}
