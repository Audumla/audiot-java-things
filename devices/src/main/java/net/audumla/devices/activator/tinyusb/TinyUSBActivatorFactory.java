package net.audumla.devices.activator.tinyusb;

import com.ftdichip.ftd2xx.Device;
import com.ftdichip.ftd2xx.FTD2xxException;
import com.ftdichip.ftd2xx.Service;
import net.audumla.bean.BeanUtils;
import net.audumla.devices.activator.*;
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
public class TinyUSBActivatorFactory extends EventTransactionActivatorFactory<EventTransactionActivator> {
    private static final Logger logger = Logger.getLogger(Activator.class);

    public static final String DEVICE_ID = "deviceid";
    public static final String RELAY_ID = "relayid";

    private static final int RELAY_ACTIVATE_INCREMENT = 100;
    private static final int RELAY_DEACTIVATE_INCREMENT = 110;
    private Device devices[] = new Device[0];
    private Map<String, EventTransactionActivator> activatorRegistry = new HashMap<>();
    private String id = BeanUtils.generateName(this);

    private TinyUSBActivatorFactory() {
        super("TinyUSBActivator");
    }

    public void initialize() throws Exception {
        try {
            devices = Service.listDevices();
            if (devices == null) {
                devices = new Device[0];
            }
            logger.info("Found " + devices.length + " TinyOS Relay devices");
            for (Device device : devices) {
                logger.debug("TinyOS Relay Serial Number : " + device.getDeviceDescriptor().getSerialNumber());
            }
            for (int di = 0; di < devices.length; ++di) {
                for (int i = 0; i < getRelaysPerDevice(); ++i) {
                    String id = di + "," + i;
                    EventTransactionActivator<TinyUSBActivatorFactory, ActivatorCommand> activator = new EventTransactionActivator<>(this);
                    activator.getId().put(PROVIDER_ID, getId());
                    activator.getId().put(RELAY_ID, i);
                    activator.getId().put(DEVICE_ID, di);
                    activator.allowSetState(true);
                    activator.allowVariableState(false);
                    activator.setName("Device[" + di + "] Relay[" + i + "]");
                    activatorRegistry.put(id, activator);
                    logger.debug("Relay identified : " + activator.getId() + " - " + activator.getName());
                }
            }
        } catch (Throwable ex) {
            logger.info("Cannot manage TinyOS relays", ex);
        } finally {
            deactivateAllDevices();
        }
    }

    public void shutdown() throws Exception {
        deactivateAllDevices();
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
    public EventTransactionActivator getActivator(Properties id) {
        String sid = id.getProperty(DEVICE_ID) + "," + id.getProperty(RELAY_ID);
        return activatorRegistry.get(sid);
    }

    @Override
    public Collection<? extends EventTransactionActivator> getActivators() {
        return activatorRegistry.values();
    }

    @Override
    public boolean setState(EventTransactionActivator activator, ActivatorState newState) throws Exception {
        int deviceid = Integer.parseInt(activator.getId().getProperty(DEVICE_ID));
        int relayid = Integer.parseInt(activator.getId().getProperty(RELAY_ID));

        if (newState.equals(ActivatorState.DEACTIVATED)) {
            deactivateRelay(deviceid, relayid);
        } else {
            activateRelay(deviceid, relayid);
        }
        return true;
    }

    public void deactivateRelay(int device, int relay) throws Exception {
        try {
            setRelay(device, relay, RELAY_DEACTIVATE_INCREMENT);
        } catch (Throwable e) {
            deactivateAllDevices();
        }
    }

    public void activateRelay(int device, int relay) throws Exception {
        try {
            setRelay(device, relay, RELAY_ACTIVATE_INCREMENT);
        } catch (Throwable e) {
            deactivateRelay(device, relay);
        }
    }

    protected void setRelay(int device, int relay, int offset) throws Exception {
        if (hasDevice(device) && relay > 0 && relay < 9) {
            try {
                writeToDevice(device, relay + offset);
            } catch (Exception ex) {
                throw new Exception("Failed to set TinyOS tinyusb [Device:" + device + "][Relay:" + relay + "]", ex);
            }
        }
    }

    protected void deactivateAllDevices() throws Exception {
        logger.debug("Closing all TinyOS relays");
        for (int i = 0; i < devices.length; ++i) {
            writeToDevice(i, RELAY_DEACTIVATE_INCREMENT);
        }
    }

    synchronized protected void writeToDevice(int device, int b) throws Exception {
        getDevice(device).write(b);
    }

    synchronized protected Device getDevice(int device) throws Exception {
        if (hasDevice(device)) {
            try {
                if (!devices[device].isOpen()) {
                    devices[device].open();
                }
            } catch (Exception ex) {
                try {
                    devices[device].reset();
                    devices[device].purgeReceiveBuffer();
                    devices[device].purgeTransmitBuffer();
                } catch (Exception ignored) {
                }
                try {
                    devices[device].open();
                } catch (Exception e) {
                    throw new Exception("Cannot open TinyOS tinyusb [Device:" + device + "]", e);
                }
            }
        }
        return devices[device];
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
