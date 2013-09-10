package net.audumla.devices.activator;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ActivatorListener {
    void activated(Activator activator);
    void deactivated(Activator activator);
    void activating(Activator activator);
    void deactivating(Activator activator);
}
