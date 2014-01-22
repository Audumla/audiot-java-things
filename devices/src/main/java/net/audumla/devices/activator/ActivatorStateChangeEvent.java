package net.audumla.devices.activator;

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.Event;
import net.audumla.automate.event.RollbackEvent;

public class ActivatorStateChangeEvent extends AbstractEvent {
    private ActivatorState oldState;
    private ActivatorState newState;
    private Activator activator;

    public ActivatorStateChangeEvent() {
    }

    public ActivatorStateChangeEvent(ActivatorState oldState, ActivatorState newState, Activator activator) {
        this.oldState = oldState;
        this.newState = newState;
        this.activator = activator;
        setName(Event.getEventTopic(activator.getName()));
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

    public void setOldState(ActivatorState oldState) {
        this.oldState = oldState;
    }

    public void setNewState(ActivatorState newState) {
        this.newState = newState;
    }

    public void setActivator(Activator activator) {
        this.activator = activator;
    }
}
