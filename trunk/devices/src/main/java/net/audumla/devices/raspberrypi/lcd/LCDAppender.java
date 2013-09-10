package net.audumla.devices.raspberrypi.lcd;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class LCDAppender extends AppenderSkeleton {

    protected LCDCommandQueue lcd;


    protected LCDAppender(LCDCommandQueue lcd) {
        this.lcd = lcd;
    }

    public void append(LoggingEvent logevent) {
        lcd.append(new LCDClearCommand());
        lcd.append(new LCDWriteCommand(logevent.getRenderedMessage()));
        lcd.append(new LCDPauseCommand());
    }

    @Override
    public void close() {
        lcd.append(new LCDShutdownCommand());
        lcd.append(new LCDPauseCommand());
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}