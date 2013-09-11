package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 10/09/13
 * Time: 9:03 PM
 */

import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorListener;
import net.audumla.devices.activator.ActivatorStateChangeEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ActivatorIrrigationEventHandler implements IrrigationEventHandler {
    private static final Logger logger = LogManager.getLogger(ActivatorIrrigationEventHandler.class);
    private Activator activator;

    public void setActivator(Activator activator) {
        this.activator = activator;
    }

    @Override
    public void handleEvent(IrrigationEvent event) {
        //attempts to activate the irrigationEvent and then monitors its progress using a specific listener.
        activator.activate(event.getEventDuration(), false,new EventActivatorListener(event));
    }

    protected static class EventActivatorListener implements ActivatorListener {

        private final IrrigationEvent irrigationEvent;

        public EventActivatorListener(IrrigationEvent event) {
            this.irrigationEvent = event;
        }

        @Override
        public void onStateChange(ActivatorStateChangeEvent event) {
            switch (event.getNewState()) {
                case ACTIVATED: irrigationEvent.setStatus(IrrigationEvent.EventStatus.EXECUTING);
                case DEACTIVATED: irrigationEvent.setStatus(IrrigationEvent.EventStatus.COMPLETE);
            }
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onStateChangeFailure(ActivatorStateChangeEvent event, Exception ex, String message) {
            irrigationEvent.setFailed(ex, message);
        }
    }
}
