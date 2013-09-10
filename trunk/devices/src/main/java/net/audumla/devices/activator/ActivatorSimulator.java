package net.audumla.devices.activator;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivatorSimulator extends ActivatorAdaptor {
    private static final Logger logger = Logger.getLogger(Activator.class);

    @Override
    protected void doActivate() {
        logger.info("Activated - " + getName());
    }

    @Override
    protected void doDeactivate() {
        logger.info("Deactivated - " + getName());
    }
}
