package net.audumla.devices.lcd;

public class LCDSetBacklightCommand implements LCDCommand {

    protected boolean backlight;

    public LCDSetBacklightCommand(boolean b) {
        backlight = b;
    }

    public void execute(LCD lcd) {
        if (backlight)
            lcd.enableBacklight();
        else
            lcd.disableBacklight();
    }

}
