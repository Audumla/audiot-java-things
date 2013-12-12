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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActivatorToggleCommand extends ActivatorEnableCommand {
    private static final Logger logger = LoggerFactory.getLogger(ActivatorToggleCommand.class);
    private long delay;
    private ScheduledExecutorService executor = null;

    public ActivatorToggleCommand() {
    }

    public ActivatorToggleCommand(ScheduledExecutorService executor, Activator activator, long delay, ActivatorListener... listeners) {
        super(activator, listeners);
        this.executor = executor;
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public Activator call() throws Exception {
        if (delay > 0 && super.call().getCurrentState().equals(Activator.ActivateState.ACTIVATED)) {
            if (executor != null) {
                executor.schedule(new ActivatorDisableCommand(activator, listeners), delay, TimeUnit.SECONDS);
            } else {
                synchronized (this) {
                    this.wait(delay * 1000);
                }
                new ActivatorDisableCommand(activator, listeners).call();
            }
        }
        return activator;
    }


}
