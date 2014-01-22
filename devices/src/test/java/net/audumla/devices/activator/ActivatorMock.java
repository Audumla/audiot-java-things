package net.audumla.devices.activator;

import org.apache.log4j.Logger;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 4:27 PM
 */
public class ActivatorMock extends EventTargetActivator<ActivatorProvider,SetActivatorStateCommand> {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private boolean activate;
    private boolean deactivate;

    public ActivatorMock(boolean activate, boolean deactivate) {
        this.activate = activate;
        this.deactivate = deactivate;
    }

    @Override
    protected void executeStateChange(ActivatorState newstate) throws Exception {
        if (newstate.equals(ActivatorState.DEACTIVATED)) {
            logger.info(deactivate ? "Simulator Deactivated - " + getName() : "Simulator Failed Deactivation - " + getName());
            if (!deactivate) {
                throw new Exception("Unsupported Failure");
            }
        } else {
            logger.info(activate ? "Simulator Activated - " + getName() : "Simulator Failed Activation - " + getName());
            if (!activate) {
                throw new Exception("Unsupported Failure");
            }
        }
    }

    @Override
    public boolean handleEvent(SetActivatorStateCommand event) throws Throwable {
        return event.execute(this);
    }

}
