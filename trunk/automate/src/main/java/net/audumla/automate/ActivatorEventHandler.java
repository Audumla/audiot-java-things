package net.audumla.automate;
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

import net.audumla.bean.BeanUtils;
import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorListener;
import net.audumla.devices.activator.ActivatorStateChangeEvent;
import org.apache.log4j.Logger;

public class ActivatorEventHandler implements EventHandler {
    private static final Logger logger = Logger.getLogger(ActivatorEventHandler.class);
    private Activator activator;
    private String name = BeanUtils.generateName(EventHandler.class);

    public ActivatorEventHandler(Activator activator) {
        this.activator = activator;
    }

    public ActivatorEventHandler() {
    }

    public void setActivator(Activator activator) {
        this.activator = activator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void handleEvent(Event event) {
        //attempts to activate the irrigationEvent and then monitors its progress using a specific listener.
        activator.activate(event.getEventDuration(), false, new EventActivatorListener(event));
    }

    protected static class EventActivatorListener implements ActivatorListener {

        private final Event irrigationEvent;

        public EventActivatorListener(Event event) {
            this.irrigationEvent = event;
        }

        @Override
        public void onStateChange(ActivatorStateChangeEvent event) {
            switch (event.getNewState()) {
                case ACTIVATED:
                    irrigationEvent.setStatus(Event.EventStatus.EXECUTING);
                    break;
                case DEACTIVATED:
                    irrigationEvent.setStatus(Event.EventStatus.COMPLETE);
                    break;
            }
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onStateChangeFailure(ActivatorStateChangeEvent event, Exception ex, String message) {
            irrigationEvent.setFailed(ex, message);
        }
    }
}
