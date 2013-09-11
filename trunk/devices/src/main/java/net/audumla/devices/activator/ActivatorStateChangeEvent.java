package net.audumla.devices.activator;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivatorStateChangeEvent {
    private Activator.ActivateState oldState;
    private Activator.ActivateState newState;
    private Activator activator;

    public ActivatorStateChangeEvent(Activator.ActivateState oldState, Activator.ActivateState newState, Activator activator) {
        this.oldState = oldState;
        this.newState = newState;
        this.activator = activator;
    }

    public Activator.ActivateState getOldState() {
        return oldState;
    }

    public Activator.ActivateState getNewState() {
        return newState;
    }

    public Activator getActivator() {
        return activator;
    }
}
