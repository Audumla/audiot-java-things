package net.audumla.devices.activator;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:01 PM
 */
public interface Activator {
    boolean activate();

    boolean activate(long seconds, boolean block);

    boolean activate(ActivatorListener listener);

    boolean activate(long seconds, boolean block, ActivatorListener listener);

    boolean deactivate();

    boolean deactivate(ActivatorListener listener);

    void addListener(ActivatorListener listener);

    void removeListener(ActivatorListener listener);

    String getName();

}
