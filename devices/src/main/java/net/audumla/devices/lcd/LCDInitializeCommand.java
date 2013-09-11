package net.audumla.devices.lcd;

public class LCDInitializeCommand implements LCDCommand {

    protected LCDInitializeCommand() {

    }

    public void execute(LCD lcd) {
        lcd.initialize();
    }

}
