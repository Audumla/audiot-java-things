package net.audumla.devices.activator.relay;

import net.audumla.devices.activator.ActivatorAdaptor;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class TinyOSUSBRelayActivator extends ActivatorAdaptor {

    private int device = 0;
    private int relay = 0;

    public TinyOSUSBRelayActivator() {

    }

    public void setDevice(int device) {
        this.device = device;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    @Override
    protected void doActivate() {
        TinyOSUSBRelayController.getInstance().activateRelay(device,relay);
    }

    @Override
    protected void doDeactivate() {
        TinyOSUSBRelayController.getInstance().deactivateRelay(device,relay);
    }
}
