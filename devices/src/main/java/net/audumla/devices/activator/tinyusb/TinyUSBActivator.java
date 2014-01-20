package net.audumla.devices.activator.tinyusb;

import net.audumla.devices.activator.*;
import org.apache.log4j.Logger;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:51 PM
 */
public class TinyUSBActivator extends AbstractActivator<TinyUSBActivatorProvider,SetActivatorStateCommand> {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private int device = 0;
    private int relay = 0;

    public static final String DEVICE_ID = "deviceid";
    public static final String RELAY_ID = "relayid";

    protected TinyUSBActivator(TinyUSBActivatorProvider provider, int device, int relay) {
        super(provider);
        setDevice(device);
        setRelay(relay);
    }

    protected TinyUSBActivator(TinyUSBActivatorProvider provider) {
        setProvider(provider);
    }

    @Override
    protected boolean executeStateChange(ActivatorState newstate) {
        if (newstate.equals(ActivatorState.DEACTIVATED)) {
            return getProvider().deactivateRelay(device, relay, (String m, Throwable e) -> {
//                listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivatorState.DEACTIVATED, this), e, m));
            });
        } else {
            return getProvider().activateRelay(device, relay, (String m, Throwable e) -> {
//                listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivatorState.ACTIVATED, this), e, m));
            });
        }
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
