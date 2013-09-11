package net.audumla.devices.activator.relay;

import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorAdaptor;
import net.audumla.devices.activator.ActivatorListener;
import net.audumla.devices.activator.ActivatorStateChangeEvent;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:51 PM
 */
public class TinyOSUSBRelayActivator extends ActivatorAdaptor {
    private static final Logger logger = Logger.getLogger(Activator.class);

    private int device = 0;
    private int relay = 0;

    //
    public TinyOSUSBRelayActivator() {
        deactivate();
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    @Override
    protected boolean doActivate(Collection<ActivatorListener> listeners) {
        return TinyOSUSBRelayController.getInstance().activateRelay(device, relay, (String m, Exception e) -> {
            listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(),ActivateState.ACTIVATED,this),e,m));
        });
    }

    @Override
    protected boolean doDeactivate(Collection<ActivatorListener> listeners) {
        return TinyOSUSBRelayController.getInstance().deactivateRelay(device, relay, (String m, Exception e) -> {
            listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(),ActivateState.DEACTIVATED,this),e,m));
        });
    }

}
