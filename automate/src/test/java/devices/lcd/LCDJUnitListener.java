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
import net.audumla.automate.event.ThreadLocalDispatcher;
import net.audumla.automate.event.lcd.LCDClearCommand;
import net.audumla.automate.event.lcd.LCDPauseCommand;
import net.audumla.automate.event.lcd.LCDSetCursorCommand;
import net.audumla.automate.event.lcd.LCDWriteCommand;
import net.audumla.devices.lcd.rpi.RPII2CLCD;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class LCDJUnitListener extends RunListener {

    private Logger logger = Logger.getLogger(LCDJUnitListener.class);
    private Dispatcher scheduler = new ThreadLocalDispatcher();
    private LCD target = RPII2CLCD.instance("JunitLCDListener", RPII2CLCD.DEFAULT_ADDRESS);

    public LCDJUnitListener() {
        if (target.initialize()) {
            logger.debug("Loaded JUnit RPII2CLCD Listener");
            scheduler.registerEventTarget(target, target.getName());
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Test: " + description.getMethodName()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Completed"),
                new LCDPauseCommand()).begin();
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Failed"),
                new LCDPauseCommand()).begin();
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        try {
            scheduler.publishEvent(target.getName(),
                    new LCDClearCommand(),
                    new LCDWriteCommand("Test: " + failure.getDescription().getMethodName()),
                    new LCDSetCursorCommand(0, 1),
                    new LCDWriteCommand("Failed"),
                    new LCDPauseCommand()).begin();
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Test: " + description.getMethodName()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Started"),
                new LCDPauseCommand()).begin();
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        scheduler.publishEvent(target.getName(),
                new LCDClearCommand(),
                new LCDWriteCommand("Tests run: " + result.getRunCount()),
                new LCDSetCursorCommand(0, 1),
                new LCDWriteCommand("Tests passed:" + (result.getRunCount() - result.getFailureCount())),
                new LCDSetCursorCommand(0, 2),
                new LCDWriteCommand("Tests failed:" + result.getFailureCount()),
                new LCDPauseCommand()).begin();
//        scheduler.shutdown();
    }

}
