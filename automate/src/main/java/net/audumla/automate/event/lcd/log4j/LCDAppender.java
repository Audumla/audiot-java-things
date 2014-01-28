package net.audumla.automate.event.lcd.log4j;

import net.audumla.automate.event.CommandEvent;
import net.audumla.automate.event.Dispatcher;
import net.audumla.automate.event.EventTarget;
import net.audumla.automate.event.lcd.LCDClearCommand;
import net.audumla.automate.event.lcd.LCDPauseCommand;
import net.audumla.automate.event.lcd.LCDShutdownCommand;
import net.audumla.automate.event.lcd.LCDWriteCommand;
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
            Dispatcher.getDefaultEventScheduler().publishEvent(target.getName(),
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
            Dispatcher.getDefaultEventScheduler().publishEvent(target.getName(),
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