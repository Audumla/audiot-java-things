package devices.lcd;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.automate.event.Dispatcher;
import net.audumla.automate.event.lcd.LCDClearCommand;
import net.audumla.automate.event.lcd.LCDPauseCommand;
import net.audumla.automate.event.lcd.LCDSetCursorCommand;
import net.audumla.automate.event.lcd.LCDWriteCommand;
import net.audumla.devices.lcd.rpi.RPII2CLCD;
import org.junit.Before;
import org.junit.Test;

public class LCDControllerTest {

    Dispatcher scheduler = null;
    LCD target = null;

    @Before
    public void setUp() throws Exception {

        scheduler = Dispatcher.getDefaultEventScheduler();
        target = RPII2CLCD.instance("LCD", RPII2CLCD.DEFAULT_ADDRESS);
    }

    @Test
    public void testLCD() throws Exception {
        scheduler.publishEvent(target.getName(), new LCDWriteCommand("Hello")).begin();
    }

    @Test
    public void testAppender() {
        RPII2CLCD.logger.trace("Testing RPII2CLCD");
        RPII2CLCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
        RPII2CLCD.logger.trace("Massive! Not Really?");
        RPII2CLCD.logger.trace("Massive! 123412341234123412341234123412341234123412341234123412341234 Really?");
    }

    @Test
    public void testController() throws Exception {
        scheduler.publishEvent(target.getName(), new LCDClearCommand()).begin();
        scheduler.publishEvent(target.getName(), new LCDWriteCommand("Test Output")).begin();
        scheduler.publishEvent(target.getName(), new LCDSetCursorCommand(0, 1)).begin();
        scheduler.publishEvent(target.getName(), new LCDWriteCommand("Moved to Line")).begin();
        scheduler.publishEvent(target.getName(), new LCDPauseCommand()).begin();
    }

}
