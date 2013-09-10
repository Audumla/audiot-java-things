package net.audumla.devices.activator;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:02 PM
 */
public interface ActivatorListener {
    void activated(Activator activator);

    void deactivated(Activator activator);

    void activating(Activator activator);

    void deactivating(Activator activator);

    void activationFailed(Activator activator, Exception ex, String message);

    void deactivationFailed(Activator activator, Exception ex, String message);


}
