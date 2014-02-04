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

    void setCursorPosition(int row, int col) throws Exception;

    void enableDisplay(boolean enable) throws Exception;

    void displayCursor(boolean cursor) throws Exception;

    void blinkCursor(boolean blink) throws Exception;

    void scrollDisplayLeft() throws Exception;

    void scrollDisplayRight() throws Exception;

    void autoIncrementCursor() throws Exception;

    void autoDecrementCursor() throws Exception;

    void incrementCursor() throws Exception;

    void decrementCursor() throws Exception;

    void autoScrollDisplay(boolean scroll) throws Exception;

    void enableBacklight() throws IOException;

    void disableBacklight() throws IOException;

    boolean initialize(int rows, int cols);

}
