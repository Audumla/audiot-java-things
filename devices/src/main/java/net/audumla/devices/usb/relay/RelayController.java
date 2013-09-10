package net.audumla.devices.usb.relay;

import java.util.Date;

public interface RelayController {
    void setId(int id);

    int getId();

    void setDevice(int device);

    int getDevice();

    void executeRelay(Date now, long duration) throws Exception;
}
