package net.audumla.devices.usb.relay;

import java.util.Date;

public class RelayControllerSimulator extends RelayControllerAdaptor {

    public void executeRelay(Date now, long duration) {
        now.setTime(now.getTime() + (duration * 1000));
    }

}
