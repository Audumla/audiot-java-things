package net.audumla.devices.activator.rasperrypi;

import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorAdaptor;
import net.audumla.devices.activator.ActivatorListener;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPIOActivator extends ActivatorAdaptor{



    @Override
    protected boolean doActivate(Collection<ActivatorListener> listeners) {
        return false;
    }

    @Override
    protected boolean doDeactivate(Collection<ActivatorListener> listeners) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
