package net.audumla.devices.raspberrypi;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import com.pi4j.system.SystemInfo;

public class InfoTest {

	@Test
	public void testInfo() throws InterruptedException, IOException,
			ParseException {
		Assert.assertNotNull(SystemInfo.getProcessor());

	}
}
