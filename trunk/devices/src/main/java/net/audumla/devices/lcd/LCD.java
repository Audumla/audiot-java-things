package net.audumla.devices.lcd;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LCD {
    void write(String s);

    void clear();

    void home();

    void setCursor(int col, int row);

    // Turn the display on/off (quickly)
    void noDisplay();

    void display();

    // Turns the underline cursor on/off
    void noCursor();

    void cursor();

    void blink();

    // These commands scroll the display without changing the RAM
    void scrollDisplayLeft();

    void scrollDisplayRight();

    // This is for text that flows Left to Right
    void leftToRight();

    // This is for text that flows Right to Left
    void rightToLeft();

    // This will 'right justify' text from the cursor
    void autoscroll();

    // This will 'left justify' text from the cursor
    void noAutoscroll();

    void enableBacklight();

    void disableBacklight();

    // Turn on and off the blinking cursor
    void noBlink();

    void initialize();
}
