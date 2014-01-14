package net.audumla.devices.activator.provider.tinyusb;

import com.ftdichip.ftd2xx.Device;
import com.ftdichip.ftd2xx.FTD2xxException;
import com.ftdichip.ftd2xx.Service;
import net.audumla.bean.BeanUtils;
import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.activator.provider.ActivatorProvider;
import net.audumla.exception.ErrorHandler;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:52 PM
 */
public class TinyUSBActivatorProvider implements ActivatorProvider {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private static final int RELAY_ACTIVATE_INCREMENT = 100;
    private static final int RELAY_DEACTIVATE_INCREMENT = 110;
    private Device devices[] = new Device[0];
    private Map<String, Activator> activatorRegistry = new HashMap<String,Activator>();
    private String id = BeanUtils.generateName(this);

    private TinyUSBActivatorProvider() {
    }

    public void initialize() {
        try {
            devices = Service.listDevices();
            if (devices == null) {
                devices = new Device[0];
            }
            logger.info("Found " + devices.length + " TinyOS Relay devices");
            for (Device device : devices) {
                logger.debug("TinyOS Relay Serial Number : " + device.getDeviceDescriptor().getSerialNumber());
            }
            Collection<Activator> activators = getActivators();
            for (Activator activator : activators) {
                logger.debug("Relay identified : " + activator.getId() +" - " + activator.getName());
            }
        } catch (Throwable ex) {
            logger.info("Cannot manage TinyOS relays", ex);
        } finally {
            deactivateAllDevices(logger::error);
        }
    }

    public void shutdown() {
        deactivateAllDevices(logger::error);
        for (Device d : devices) {
            try {
                d.purgeTransmitBuffer();
                d.purgeReceiveBuffer();
                d.close();
            } catch (FTD2xxException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    protected int getRelaysPerDevice() {
        return 8;
    }

    @Override
    public Activator getActivator(Properties id) {
        String sid = id.getProperty(TinyUSBActivator.DEVICE_ID)+","+id.getProperty(TinyUSBActivator.RELAY_ID);
        return activatorRegistry.get(sid);
    }

    @Override
    public Collection<Activator> getActivators() {
        int di = 0;
        for (Device d : devices) {
            for (int i = 0; i < getRelaysPerDevice(); ++i) {
                String id = di+","+i;
                Activator activator = new TinyUSBActivator(this,di,i);
                activator.getId().put(PROVIDER_ID,getId());
                activator.setName("Device["+di+"] Relay["+i+"]");
                Properties props = activator.getId();
                activatorRegistry.put(id,activator);
            }
            ++di;
        }
        return activatorRegistry.values();
    }

    @Override
    public boolean setCurrentStates(Map<Activator, ActivatorState> newStates) throws Exception {
        return false;
    }

    public boolean deactivateRelay(int device, int relay, ErrorHandler handler) {
        if (hasDevice(device)) {
            try {
                if (!setRelay(device, relay, RELAY_DEACTIVATE_INCREMENT, handler)) {
                    handler.handleError("Attempting to shutdown all TinyOS relays due to unexpected failure", null);
                    deactivateAllDevices(handler);
                    return false;
                }
            } catch (Throwable e) {
                handler.handleError("Unknown deactivation failure", e);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean activateRelay(int device, int relay, ErrorHandler handler) {
        try {
            if (!setRelay(device, relay, RELAY_ACTIVATE_INCREMENT, handler)) {
                deactivateRelay(device, relay, handler);
                return false;
            }
        } catch (Throwable e) {
            handler.handleError("Unknown activation failure", e);
            return false;
        }
        return true;
    }

    protected boolean setRelay(int device, int relay, int offset, ErrorHandler handler) throws Exception {
        if (relay > 0 && relay < 9) {
            try {
                if (!writeToDevice(device, relay + offset, handler)) {
                    handler.handleError("Failed to set TinyOS tinyusb [Device:" + device + "][Relay:" + relay + "]", null);
                } else {
                    return true;
                }
            } catch (Throwable ex) {
                handler.handleError("Failed to set TinyOS tinyusb [Device:" + device + "][Relay:" + relay + "]", ex);
            }
        }
        return false;
    }

    protected void deactivateAllDevices(ErrorHandler handler) {
        logger.debug("Closing all TinyOS relays");
        for (int i = 0; i < devices.length; ++i) {
            try {
                if (!writeToDevice(i, RELAY_DEACTIVATE_INCREMENT, handler)) {
                    handler.handleError("Failed to deactivate TinyOS tinyusb [Device:" + i + "]", null);
                }
            } catch (Throwable ex) {
                handler.handleError("Failed to deactivate TinyOS tinyusb [Device:" + i + "]", ex);
            }
        }
    }

    synchronized protected boolean writeToDevice(int device, int b, ErrorHandler handler) throws Exception {
        try {
            if (openDevice(device, handler)) {
                devices[device].write(b);
                return true;
            }
        } catch (Throwable ex) {
            handler.handleError("Cannot write to TinyOS tinyusb [Device:" + device + "]", ex);
        }
        return false;
    }

    synchronized protected boolean openDevice(int device, ErrorHandler handler) throws Exception {
        if (hasDevice(device)) {
            try {
                if (!devices[device].isOpen()) {
                    devices[device].open();
                }
                return true;
            } catch (Throwable ex) {
                try {
                    devices[device].reset();
                    devices[device].purgeReceiveBuffer();
                    devices[device].purgeTransmitBuffer();
                }
                catch (Throwable ignored) {
                }
                try {
                    devices[device].open();
                    return true;
                }
                catch (Throwable e) {
                    handler.handleError("Cannot open TinyOS tinyusb [Device:" + device + "]", e);
                }
            }
        }
        return false;
    }

    protected boolean hasDevice(int device) {
        if (devices != null && devices.length > device) {
            return true;
        } else {
            logger.error("Cannot locate TinyOS at index [Device:" + device + "]");
            return false;
        }
    }

}
