package net.audumla.devices.activator;

import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 4:27 PM
 */
public class ExceptionActivatorMock extends ActivatorAdaptor {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private boolean activate;
    private boolean deactivate;

    public ExceptionActivatorMock(boolean activate, boolean deactivate) {
        this.activate = activate;
        this.deactivate = deactivate;
    }

    @Override
    protected boolean doActivate(Collection<ActivatorListener> listeners) {
        logger.info(activate ? "Simulator Activated - " + getName() : "Simulator Failed Activation - " + getName());
        if (activate) {
            return activate;
        } else {
            throw new UnsupportedOperationException("Unsupported Failure");
        }
    }

    @Override
    protected boolean doDeactivate(Collection<ActivatorListener> listeners) {
        logger.info(deactivate ? "Simulator Deactivated - " + getName() : "Simulator Failed Deactivation - " + getName());
        if (deactivate) {
            return deactivate;
        } else {
            throw new UnsupportedOperationException("Unsupported Failure");
        }
    }


}