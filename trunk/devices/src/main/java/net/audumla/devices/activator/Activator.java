package net.audumla.devices.activator;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:01 PM
 */
public interface Activator {
    public enum ActivateState {ACTIVATING,ACTIVATED,DEACTIVATING,DEACTIVATED,UNKNOWN};

    boolean activate();

    boolean activate(long seconds, boolean block);

    boolean activate(ActivatorListener listener);

    boolean activate(long seconds, boolean block, ActivatorListener listener);

    boolean deactivate();

    boolean deactivate(ActivatorListener listener);

    void addListener(ActivatorListener listener);

    void removeListener(ActivatorListener listener);

    String getName();

    ActivateState getCurrentState();

//    void setCurrentState(ActivateState state);

}
