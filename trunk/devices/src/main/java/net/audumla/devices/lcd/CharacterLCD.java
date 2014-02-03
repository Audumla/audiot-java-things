package net.audumla.devices.lcd;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CharacterLCD {
    void write(int row, int col, String s) throws Exception;
    void write(String s) throws Exception;

    void clear() throws Exception;

    void home() throws Exception;

    void setCursor(int col, int row) throws Exception;

    // Turn the display on/off (quickly)
    void noDisplay() throws Exception;

    void display() throws Exception;

    // Turns the underline cursor on/off
    void noCursor() throws Exception;

    void cursor() throws Exception;

    void blink() throws Exception;

    // These commands scroll the display without changing the RAM
    void scrollDisplayLeft() throws Exception;

    void scrollDisplayRight() throws Exception;

    // This is for text that flows Left to Right
    void leftToRight() throws Exception;

    // This is for text that flows Right to Left
    void rightToLeft() throws Exception;

    // This will 'right justify' text from the cursor
    void autoscroll() throws Exception;

    // This will 'left justify' text from the cursor
    void noAutoscroll() throws Exception;

    void enableBacklight() throws IOException;

    void disableBacklight() throws IOException;

    // Turn on and off the blinking cursor
    void noBlink() throws Exception;

    boolean initialize();

    void setDisplaySize(int rows, int cols);
}
