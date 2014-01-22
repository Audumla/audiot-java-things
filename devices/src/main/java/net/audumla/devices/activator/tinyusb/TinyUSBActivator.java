package net.audumla.devices.activator.tinyusb;

import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.activator.EventTargetActivator;
import net.audumla.devices.activator.SetActivatorStateCommand;
import org.apache.log4j.Logger;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:51 PM
 */
public class TinyUSBActivator extends EventTargetActivator<TinyUSBActivatorProvider, SetActivatorStateCommand> {
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
    protected void executeStateChange(ActivatorState newstate) throws Exception {
        if (newstate.equals(ActivatorState.DEACTIVATED)) {
            getProvider().deactivateRelay(device, relay);
        } else {
            getProvider().activateRelay(device, relay);
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

    @Override
    public boolean handleEvent(SetActivatorStateCommand event) throws Exception {
        event.getEventTransaction().addTransactionListener(getProvider());
        return true;
    }
}
