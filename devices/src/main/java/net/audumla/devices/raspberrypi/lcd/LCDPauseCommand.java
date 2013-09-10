package net.audumla.devices.raspberrypi.lcd;

public class LCDPauseCommand implements LCDCommand {

    protected int msec = 2000;

    public LCDPauseCommand() {
    }

    public LCDPauseCommand(int msec) {
        this.msec = msec;
    }

    public void execute(LCD lcd) {
        synchronized (lcd) {
            try {
                lcd.wait(msec);
            } catch (Exception e) {
            }

        }
    }

}
