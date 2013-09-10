package net.audumla.devices.usb.relay;

import com.ftdichip.ftd2xx.Device;
import com.ftdichip.ftd2xx.Service;
import org.apache.log4j.Logger;

import java.util.Date;

public class RelayControllerTinyOS extends RelayControllerAdaptor {
	private static final Logger LOG = Logger.getLogger(RelayController.class);
	private static final int RELAY_ACTIVATE_INCREMENT = 100;
	private static final int RELAY_DEACTIVATE_INCREMENT = 110;

	private static Device devices[];

	static {
		try {
			devices = Service.listDevices();
			LOG.info("Found " + devices.length + " TinyOS relay devices");
			for (Device device : devices) {
				LOG.info("Serial Number : " + device.getDeviceDescriptor().getSerialNumber());
				closeAllRelays(device);
			}
		} catch (Exception ex) {
			LOG.info("Cannot manage relays", ex);
		}
	}

	public RelayControllerTinyOS() {
		super();
	}

	public void executeRelay(Date now, long duration) throws Exception {
		if (getId() > 0 && getId() < 9) {
			try {
				synchronized (this) {
					try {
						writeToDevice(devices[getDevice()], getId() + RELAY_ACTIVATE_INCREMENT);
						this.wait(duration * 1000);
					} catch (InterruptedException ex) {
						LOG.error("Failed to activate relay " + getId(), ex);
						throw ex;

					} finally {
						writeToDevice(devices[getDevice()], getId() + RELAY_DEACTIVATE_INCREMENT);
					}
				}
			} catch (Exception e) {
				LOG.error("Failed to control relay " + getId(), e);
				closeAllRelays(devices[getDevice()]);
				throw e;
			}
		} else {
			throw new Exception("Relay ID not set");
		}
	}

	static protected void closeAllRelays(Device device) throws Exception {
		try {
			writeToDevice(device, RELAY_DEACTIVATE_INCREMENT);
			LOG.info("Closing all relays");
		} catch (Exception ex) {
			LOG.error("Cannot close relays", ex);
			throw ex;
		}
	}

	static protected void writeToDevice(Device device, int b) throws Exception {
		try {
			synchronized (device) {
				openDevice(device);
				device.write(b);
			}
		} catch (Exception ex) {
			LOG.error("Cannot write to device", ex);
			throw ex;
		}
	}

	static protected void openDevice(Device device) throws Exception {
		try {
			synchronized (device) {
				if (!device.isOpen()) {
					device.open();
				}
			}
		} catch (Exception ex) {
			LOG.error("Cannot open device", ex);
			throw ex;
		}
	}
}
