package net.audumla.devices.activator.provider.tinyusb;

import net.audumla.devices.activator.*;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:51 PM
 */
public class TinyUSBActivator extends AbstractActivator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private TinyUSBActivatorProvider controller;
    private int device = 0;
    private int relay = 0;

    public static final String DEVICE_ID = "deviceid";
    public static final String RELAY_ID = "relayid";

    public TinyUSBActivator(TinyUSBActivatorProvider controller, int device, int relay) {
        this.controller = controller;
        this.device = device;
        this.relay = relay;
        getId().setProperty(DEVICE_ID, String.valueOf(device));
        getId().setProperty(RELAY_ID, String.valueOf(relay));
    }

    public TinyUSBActivator() {
    }

    @Override
    protected boolean executeStateChange(ActivatorState newstate, Collection<ActivatorListener> listeners) {
        if (newstate.equals(ActivatorState.DEACTIVATED)) {
            return controller.deactivateRelay(device, relay, (String m, Throwable e) -> {
                listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivatorState.DEACTIVATED, this), e, m));
            });
        } else {
            return controller.activateRelay(device, relay, (String m, Throwable e) -> {
                listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivatorState.ACTIVATED, this), e, m));
            });
        }
    }

    public TinyUSBActivator(TinyUSBActivatorProvider controller) {
        this.controller = controller;
    }

    public void setController(TinyUSBActivatorProvider controller) {
        this.controller = controller;
    }

    public void setDevice(int device) {
        this.device = device;
        getId().setProperty(DEVICE_ID, String.valueOf(device));
    }

    public void setRelay(int relay) {
        this.relay = relay;
        getId().setProperty(RELAY_ID, String.valueOf(relay));
    }


}
