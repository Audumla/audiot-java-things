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
    private TinyUSBActivatorProvider provider;
    private int device = 0;
    private int relay = 0;

    public static final String DEVICE_ID = "deviceid";
    public static final String RELAY_ID = "relayid";

    public TinyUSBActivator(TinyUSBActivatorProvider provider, int device, int relay) {
        this.provider = provider;
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
            return provider.deactivateRelay(device, relay, (String m, Throwable e) -> {
                listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivatorState.DEACTIVATED, this), e, m));
            });
        } else {
            return provider.activateRelay(device, relay, (String m, Throwable e) -> {
                listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivatorState.ACTIVATED, this), e, m));
            });
        }
    }

    public TinyUSBActivator(TinyUSBActivatorProvider provider) {
        this.provider = provider;
    }

    public void setProvider(TinyUSBActivatorProvider provider) {
        this.provider = provider;
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
