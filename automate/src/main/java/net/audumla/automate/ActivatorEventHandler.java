package net.audumla.automate;
/**
 * User: audumla
 * Date: 10/09/13
 * Time: 9:03 PM
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
