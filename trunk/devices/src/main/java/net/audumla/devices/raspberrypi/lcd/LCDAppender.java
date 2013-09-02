package net.audumla.devices.raspberrypi.lcd;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttr;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "LCDLogger", type = "Core", elementType = "appender", printObject = true)
public class LCDAppender extends AbstractAppender<String> {

	protected LCDCommandQueue lcd;

	protected LCDAppender(LCDCommandQueue lcd, String name, Filter filter, Layout<String> layout) {
		this(lcd, name, filter, layout, true);
	}

	protected LCDAppender(LCDCommandQueue lcd, String name, Filter filter, Layout<String> layout, boolean handleException) {
		super(name, filter, layout, handleException);
		this.lcd = lcd;
	}

	public void append(LogEvent logevent) {
		lcd.append(new LCDClearCommand());
		lcd.append(new LCDWriteCommand(logevent.getMessage().getFormattedMessage()));
		lcd.append(new LCDPauseCommand());
	}

	@PluginFactory
	public static LCDAppender createAppender(@PluginAttr("name") String name, @PluginAttr("suppressExceptions") String suppress,
			@PluginElement("layout") Layout<String> layout, @PluginElement("filters") Filter filter) {

		boolean handleExceptions = suppress == null ? true : Boolean.valueOf(suppress);
		return new LCDAppender(LCDCommandQueue.instance(), name, filter, layout, handleExceptions);
	}

	@Override
	public void stop() {
		super.stop();
		lcd.append(new LCDShutdownCommand());
		lcd.append(new LCDPauseCommand());
	}
}