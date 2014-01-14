package net.audumla.devices.lcd.log4j;

import net.audumla.devices.event.CommandEvent;
import net.audumla.devices.event.EventScheduler;
import net.audumla.devices.event.EventTarget;
import net.audumla.devices.lcd.*;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class LCDAppender extends AppenderSkeleton {

    protected EventTarget<CommandEvent<LCD>> target;

    public LCDAppender(EventTarget<CommandEvent<LCD>> t) {
        target = t;
    }

    public void append(LoggingEvent logevent) {
        try {
            EventScheduler.getDefaultEventScheduler().scheduleEvent(target,
                    new LCDClearCommand(),
                    new LCDWriteCommand(logevent.getRenderedMessage()),
                    new LCDPauseCommand()).begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            EventScheduler.getDefaultEventScheduler().scheduleEvent(target,
                    new LCDShutdownCommand(),
                    new LCDPauseCommand()).begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}