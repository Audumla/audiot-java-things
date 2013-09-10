package net.audumla.devices.activator.relay;

import com.ftdichip.ftd2xx.Device;
import com.ftdichip.ftd2xx.Service;
import net.audumla.devices.activator.Activator;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class TinyOSUSBRelayController {
    private static final Logger logger = Logger.getLogger(Activator.class);

    private static final int RELAY_ACTIVATE_INCREMENT = 100;
    private static final int RELAY_DEACTIVATE_INCREMENT = 110;
    private static TinyOSUSBRelayController instance;

    private Device devices[];

    public static TinyOSUSBRelayController getInstance() {
        if (instance == null) {
            instance = new TinyOSUSBRelayController();
        }
        return instance;
    }

    private TinyOSUSBRelayController() {
        try {
            devices = Service.listDevices();
            logger.info("Found " + devices.length + " TinyOS Relay devices");
            for (Device device : devices) {
                logger.debug("TinyOS Relay Serial Number : " + device.getDeviceDescriptor().getSerialNumber());
            }
        } catch (Exception ex) {
            logger.info("Cannot manage TinyOS relays", ex);
        } finally {
            deactivateAllDevices();
        }
    }

    public int getDeviceCount() {
        return devices.length;
    }

    public boolean deactivateRelay(int device, int relay) {
        try {
            if (!setRelay(device, relay, RELAY_DEACTIVATE_INCREMENT)) {
                logger.error("Attempting to shutdown all TinyOS relays due to unexpected failure");
                deactivateAllDevices();
                return false;
            }
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
        return true;
    }

    public boolean activateRelay(int device, int relay) {
        try {
            if (!setRelay(device, relay, RELAY_ACTIVATE_INCREMENT)) {
                deactivateRelay(device, relay);
                return false;
            }
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
        return true;
    }

    protected boolean setRelay(int device, int relay, int offset) throws Exception {
        if (relay > 0 && relay < 9) {
            try {
                if (!writeToDevice(device, relay + offset)) {
                    logger.error("Failed to set TinyOS relay [Device:" + device + "][Relay:" + relay + "]");
                } else {
                    return true;
                }
            } catch (Exception ex) {
                logger.error("Failed to set TinyOS relay [Device:" + device + "][Relay:" + relay + "]", ex);
            }
        }
        return false;
    }

    protected void deactivateAllDevices() {
        logger.debug("Closing all TinyOS relays");
        for (int i = 0; i < devices.length; ++i) {
            try {
                if (!writeToDevice(i, RELAY_DEACTIVATE_INCREMENT)) {
                    logger.error("Failed to deactivate TinyOS relay [Device:" + i + "]");
                }
            } catch (Exception ex) {
                logger.error("Failed to deactivate TinyOS relay [Device:" + i + "]", ex);
            }
        }
    }

    synchronized protected boolean writeToDevice(int device, int b) throws Exception {
        try {
            openDevice(device);
            devices[device].write(b);
            return true;
        } catch (Exception ex) {
            logger.error("Cannot write to TinyOS relay [Device:" + device + "]", ex);
        }
        return false;
    }

    synchronized protected boolean openDevice(int device) throws Exception {
        try {
            if (!devices[device].isOpen()) {
                devices[device].open();
            }
            return true;
        } catch (Exception ex) {
            logger.error("Cannot open TinyOS relay [Device:" + device + "]", ex);
        }
        return false;
    }
}
