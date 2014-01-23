package net.audumla.devices.lcd;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.CommandEvent;

public class LCDPauseCommand extends AbstractEvent implements CommandEvent<LCD> {

    protected int msec = 2000;

    public LCDPauseCommand() {
    }

    public int getMsec() {
        return msec;
    }

    public void setMsec(int msec) {
        this.msec = msec;
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

}
