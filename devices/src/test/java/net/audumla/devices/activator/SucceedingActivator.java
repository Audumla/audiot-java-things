package net.audumla.devices.activator;

import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 4:27 PM
 */
public class SucceedingActivator extends ActivatorAdaptor {
    private static final Logger logger = Logger.getLogger(Activator.class);

    @Override
    protected boolean doActivate(Collection<ActivatorListener> listeners) {
        logger.info("Simulator Activated - " + getName());
        return true;
    }

    @Override
    protected boolean doDeactivate(Collection<ActivatorListener> listeners) {
        logger.info("Simulator Deactivated - " + getName());
        return true;
    }

}
