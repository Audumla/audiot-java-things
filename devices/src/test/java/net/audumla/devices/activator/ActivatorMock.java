package net.audumla.devices.activator;

import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 4:27 PM
 */
public class ActivatorMock extends AbstractActivator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private boolean activate;
    private boolean deactivate;

    public ActivatorMock(boolean activate, boolean deactivate) {
        this.activate = activate;
        this.deactivate = deactivate;
    }

    @Override
    protected boolean doActivate(Collection<ActivatorListener> listeners) {
        logger.info(activate ? "Simulator Activated - " + getName() : "Simulator Failed Activation - " + getName());
        return activate;
    }

    @Override
    protected boolean doDeactivate(Collection<ActivatorListener> listeners) {
        logger.info(deactivate ? "Simulator Deactivated - " + getName() : "Simulator Failed Deactivation - " + getName());
        return deactivate;
    }

}
