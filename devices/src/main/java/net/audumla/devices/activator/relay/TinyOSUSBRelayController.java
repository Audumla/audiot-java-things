package net.audumla.devices.activator.relay;

import com.ftdichip.ftd2xx.Device;
import com.ftdichip.ftd2xx.Service;
import net.audumla.devices.activator.Activator;
import net.audumla.exception.ErrorHandler;
import org.apache.log4j.Logger;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:52 PM
 */
public class TinyOSUSBRelayController {
    private static final Logger logger = Logger.getLogger(Activator.class);
    private static final int RELAY_ACTIVATE_INCREMENT = 100;
    private static final int RELAY_DEACTIVATE_INCREMENT = 110;
    private static TinyOSUSBRelayController instance;
    private Device devices[] = new Device[0];

    //
    private TinyOSUSBRelayController() {
        try {
            devices = Service.listDevices();
            if (devices == null) {
                devices = new Device[0];
            }
            logger.info("Found " + devices.length + " TinyOS Relay devices");
            for (Device device : devices) {
                logger.debug("TinyOS Relay Serial Number : " + device.getDeviceDescriptor().getSerialNumber());
            }
        } catch (Throwable ex) {
            logger.info("Cannot manage TinyOS relays", ex);
        } finally {
            deactivateAllDevices(logger::error);
        }
    }

    public static TinyOSUSBRelayController getInstance() {
        if (instance == null) {
            instance = new TinyOSUSBRelayController();
        }
        return instance;
    }

    public int getDeviceCount() {
        return devices.length;
    }

    public boolean deactivateRelay(int device, int relay, ErrorHandler handler) {
        if (hasDevice(device)) {
            try {
                if (!setRelay(device, relay, RELAY_DEACTIVATE_INCREMENT, handler)) {
                    handler.handleError("Attempting to shutdown all TinyOS relays due to unexpected failure", null);
                    deactivateAllDevices(handler);
                    return false;
                }
            } catch (Exception e) {
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
        } catch (Exception e) {
            handler.handleError("Unknown activation failure", e);
            return false;
        }
        return true;
    }

    protected boolean setRelay(int device, int relay, int offset, ErrorHandler handler) throws Exception {
        if (relay > 0 && relay < 9) {
            try {
                if (!writeToDevice(device, relay + offset, handler)) {
                    handler.handleError("Failed to set TinyOS relay [Device:" + device + "][Relay:" + relay + "]", null);
                } else {
                    return true;
                }
            } catch (Exception ex) {
                handler.handleError("Failed to set TinyOS relay [Device:" + device + "][Relay:" + relay + "]", ex);
            }
        }
        return false;
    }

    protected void deactivateAllDevices(ErrorHandler handler) {
        logger.debug("Closing all TinyOS relays");
        for (int i = 0; i < devices.length; ++i) {
            try {
                if (!writeToDevice(i, RELAY_DEACTIVATE_INCREMENT, handler)) {
                    handler.handleError("Failed to deactivate TinyOS relay [Device:" + i + "]", null);
                }
            } catch (Exception ex) {
                handler.handleError("Failed to deactivate TinyOS relay [Device:" + i + "]", ex);
            }
        }
    }

    synchronized protected boolean writeToDevice(int device, int b, ErrorHandler handler) throws Exception {
        try {
            if (openDevice(device, handler)) {
                devices[device].write(b);
                return true;
            }
        } catch (Exception ex) {
            handler.handleError("Cannot write to TinyOS relay [Device:" + device + "]", ex);
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
            } catch (Exception ex) {
                handler.handleError("Cannot open TinyOS relay [Device:" + device + "]", ex);
            }
        }
        return false;
    }

    protected boolean hasDevice(int device) {
        if (devices != null && devices.length > device) {
            return true;
        } else {
            logger.error("Cannot locate TinyOS Relay at index [" + device + "]");
        }
        return true;
    }

}
