package net.audumla.devices.activator.provider.tinyusb;

import net.audumla.devices.activator.AbstractActivator;
import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorListener;
import net.audumla.devices.activator.ActivatorStateChangeEvent;
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
    private String id;

    public TinyUSBActivator(TinyUSBActivatorProvider controller, int device, int relay, String id) {
        this.controller = controller;
        this.device = device;
        this.relay = relay;
    }

    public TinyUSBActivator() {
    }

    public TinyUSBActivator(TinyUSBActivatorProvider controller) {
        this.controller = controller;
    }

    public void setController(TinyUSBActivatorProvider controller) {
        this.controller = controller;
    }

    public void deactivate() {
        super.deactivate();
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    @Override
    protected boolean doActivate(Collection<ActivatorListener> listeners) {
        return controller.activateRelay(device, relay, (String m, Throwable e) -> {
            listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivateState.ACTIVATED, this), e, m));
        });
    }

    @Override
    protected boolean doDeactivate(Collection<ActivatorListener> listeners) {
        if (controller.deactivateRelay(device, relay, (String m, Throwable e) -> {
            listeners.forEach(l -> l.onStateChangeFailure(new ActivatorStateChangeEvent(getCurrentState(), ActivateState.DEACTIVATED, this), e, m));
        })) {
            return true;
        } else {
            setActiveState(ActivateState.UNKNOWN);
            return false;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
