package net.audumla.devices.lcd;


public class LCDSetDisplayCommand implements LCDCommand {

    protected boolean display;

    public LCDSetDisplayCommand(boolean d) {
        this.display = d;
    }

    public void execute(LCD lcd) {
        if (display)
            lcd.display();
        else {
            lcd.noDisplay();
        }
    }

}
