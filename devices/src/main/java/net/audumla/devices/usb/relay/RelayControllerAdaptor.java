package net.audumla.devices.usb.relay;


public abstract class RelayControllerAdaptor implements RelayController {

	private int id;
	private int device;

	public int getDevice() {
		return device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
