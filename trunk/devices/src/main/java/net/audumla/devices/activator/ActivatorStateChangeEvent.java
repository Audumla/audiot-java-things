package net.audumla.devices.activator;

import net.audumla.automate.event.AbstractEvent;

public class ActivatorStateChangeEvent extends AbstractEvent{
    private ActivatorState oldState;
    private ActivatorState newState;
    private Activator activator;

    public ActivatorStateChangeEvent(ActivatorState oldState, ActivatorState newState, Activator activator) {
        this.oldState = oldState;
        this.newState = newState;
        this.activator = activator;
    }

    public ActivatorState getOldState() {
        return oldState;
    }

    public ActivatorState getNewState() {
        return newState;
    }

    public Activator getActivator() {
        return activator;
    }
}
