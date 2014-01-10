package net.audumla.devices.activator;

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

import net.audumla.devices.event.SimpleEventSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActivatorToggleCommand extends ActivatorEnableCommand {
    private static final Logger logger = LoggerFactory.getLogger(ActivatorToggleCommand.class);
    private Duration delay;

    public ActivatorToggleCommand() {
    }

    public ActivatorToggleCommand(Duration delay, ActivatorListener... listeners) {
        super(listeners);
        this.delay = delay;
    }

    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    @Override
    public boolean execute(Activator activator) {
        // ensure that the result of the call to ativate results in the activator now being in the correct state.
        if (super.execute(activator)) {
            if (getScheduler() != null) {
                return getScheduler().scheduleEvent(activator, new SimpleEventSchedule(Instant.now().plus(delay)),new ActivatorDisableCommand(getListeners()));
            } else {
                synchronized (this) {
                    try {
                        this.wait(delay.toMillis());
                    } catch (InterruptedException e) {
                        logger.error("Failed to execute blocking deactivate",e);
                    }
                }
                return new ActivatorDisableCommand(getListeners()).execute(activator);
            }
        }
        return false;
    }


}
