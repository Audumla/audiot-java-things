package net.audumla.devices.raspberrypi.lcd;

public class LCDClearCommand implements LCDCommand {

    public LCDClearCommand() {

    }

    public void execute(LCD lcd) {
        lcd.clear();
    }

}
