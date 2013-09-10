package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 10/09/13
 * Time: 9:03 PM
 */

import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorListener;
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
        activator.activate(event.getEventDuration(), false,new EventActivatorListener(event));
    }

    protected static class EventActivatorListener implements ActivatorListener {

        private final IrrigationEvent event;

        public EventActivatorListener(IrrigationEvent event) {
            this.event = event;
        }

        @Override
        public void activated(Activator activator) {
            event.setStatus(IrrigationEvent.EventStatus.EXECUTING);
        }

        @Override
        public void deactivated(Activator activator) {
            event.setStatus(IrrigationEvent.EventStatus.COMPLETE);
        }

        @Override
        public void activating(Activator activator) {
        }

        @Override
        public void deactivating(Activator activator) {
        }

        @Override
        public void activationFailed(Activator activator, Exception ex, String message) {
            logger.error(message, ex);
            event.setStatus(IrrigationEvent.EventStatus.FAILED);
        }

        @Override
        public void deactivationFailed(Activator activator, Exception ex, String message) {
            logger.error(message, ex);
            event.setStatus(IrrigationEvent.EventStatus.FAILED);
        }
    }
}
