package net.audumla.devices.lcd.junit;

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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;
import net.audumla.akka.CMTargetCreator;
import net.audumla.devices.io.channel.ChannelAddressAttr;
import net.audumla.devices.io.channel.DeviceAddressAttr;
import net.audumla.devices.io.channel.I2CDeviceChannel;
import net.audumla.devices.io.i2c.RPiI2CDeviceFactory;
import net.audumla.devices.lcd.CharacterLCD;
import net.audumla.devices.lcd.akka.*;
import net.audumla.devices.lcd.HitachiCharacterLCD;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class LCDJUnitListener extends RunListener {
    private static final Logger logger = Logger.getLogger(LCDJUnitListener.class);

    private ActorRef target;

    public LCDJUnitListener() {
        try {
            ActorSystem actorSystem = ActorSystem.create();
            I2CDeviceChannel channel = new I2CDeviceChannel(new RPiI2CDeviceFactory(), new ChannelAddressAttr(1), new DeviceAddressAttr(HitachiCharacterLCD.DEFAULT_ADDRESS));
            Props lcpProps = Props.create(new CMTargetCreator<CharacterLCD>(new HitachiCharacterLCD(channel, "LCD JUnit Logger"))).withDispatcher("junit-dispatcher");
            target = actorSystem.actorOf(lcpProps, "lcd");
            target.tell(new LCDInitializeCommand(4, 20), null);
            logger.debug("Loaded JUnit LCD Listener");
        } catch (Exception e) {
            logger.error("Unable to initialize LCD", e);
        }
    }

    protected void displayTestStatus(String desc, String status) {
        target.tell(new LCDClearDisplayCommand(), null);
        target.tell(new LCDPositionedWriteCommand(0, 0, status), null);
        target.tell(new LCDPositionedWriteCommand(1, 0, desc), null);
        target.tell(new LCDPauseCommand(), null);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        displayTestStatus("Test: " + description.getMethodName(), "Test Completed");
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        displayTestStatus("Test: " + failure.getDescription().getMethodName(), "Test Failed");
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        displayTestStatus("Test: " + failure.getDescription().getMethodName(), "Assumption Failed");
    }

    @Override
    public void testStarted(Description description) throws Exception {
        displayTestStatus("Test: " + description.getMethodName(), "Test Started");
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        target.tell(new LCDClearDisplayCommand(), null);
        target.tell(new LCDPositionedWriteCommand(0, 0, "Tests run: " + result.getRunCount()), null);
        target.tell(new LCDPositionedWriteCommand(1, 0, "Tests passed:" + (result.getRunCount() - result.getFailureCount())), null);
        target.tell(new LCDPositionedWriteCommand(2, 0, "Tests failed:" + result.getFailureCount()), null);
        target.tell(new LCDPauseCommand(), null);
        target.tell(akka.actor.PoisonPill.getInstance(), null);
        try {
            Future<Boolean> stopped =
                    Patterns.gracefulStop(target, Duration.create(5, TimeUnit.SECONDS));
            Await.result(stopped, Duration.create(60, TimeUnit.SECONDS));
            // the actor has been stopped
        } catch (AskTimeoutException e) {
            // the actor wasn't stopped within 5 seconds
        }
    }
}
