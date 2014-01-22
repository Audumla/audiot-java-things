package net.audumla.devices.activator.rpi;

import net.audumla.devices.activator.EventTargetActivator;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.activator.SetActivatorStateCommand;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class RPIGPIOActivator extends EventTargetActivator<RPIGPIOActivatorProvider,SetActivatorStateCommand> {


    @Override
    protected void executeStateChange(ActivatorState newstate) {
    }

    @Override
    public boolean handleEvent(SetActivatorStateCommand event) throws Exception {
        event.getEventTransaction().addTransactionListener(getProvider());
        return true;
    }
}
