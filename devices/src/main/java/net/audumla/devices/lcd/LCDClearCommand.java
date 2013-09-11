package net.audumla.devices.lcd;

public class LCDClearCommand implements LCDCommand {

    public LCDClearCommand() {

    }

    public void execute(LCD lcd) {
        lcd.clear();
    }

}
