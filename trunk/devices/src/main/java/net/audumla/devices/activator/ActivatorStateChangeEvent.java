package net.audumla.devices.activator;

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
