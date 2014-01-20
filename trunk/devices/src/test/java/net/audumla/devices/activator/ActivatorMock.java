package net.audumla.devices.activator;

import net.audumla.automate.event.AbstractEventTarget;
import net.audumla.automate.event.Event;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 4:27 PM
 */
public class ActivatorMock extends AbstractActivator<ActivatorProvider,SetActivatorStateCommand> {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private boolean activate;
    private boolean deactivate;

    public ActivatorMock(boolean activate, boolean deactivate) {
        this.activate = activate;
        this.deactivate = deactivate;
    }

    @Override
    protected boolean executeStateChange(ActivatorState newstate) {
        if (newstate.equals(ActivatorState.DEACTIVATED)) {
            logger.info(deactivate ? "Simulator Deactivated - " + getName() : "Simulator Failed Deactivation - " + getName());
            return deactivate;
        } else {
            logger.info(activate ? "Simulator Activated - " + getName() : "Simulator Failed Activation - " + getName());
            return activate;
        }
    }

}
