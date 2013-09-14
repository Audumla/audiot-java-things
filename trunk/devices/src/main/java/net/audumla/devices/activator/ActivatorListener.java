package net.audumla.devices.activator;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:02 PM
 */
public interface ActivatorListener {
    void onStateChange(ActivatorStateChangeEvent event);

    void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message);
}
