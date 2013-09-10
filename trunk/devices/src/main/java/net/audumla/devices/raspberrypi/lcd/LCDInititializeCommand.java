package net.audumla.devices.raspberrypi.lcd;

public class LCDInititializeCommand implements LCDCommand {

    protected LCDInititializeCommand() {

    }

    public void execute(LCD lcd) {
        lcd.init();
    }

}
