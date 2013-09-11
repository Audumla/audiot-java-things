package net.audumla.devices.lcd.raspberrypi;

import com.pi4j.system.SystemInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class InfoTest {

    @Test
    public void testInfo() throws InterruptedException, IOException,
            ParseException {
        Assert.assertNotNull(SystemInfo.getProcessor());

    }
}
