package net.audumla.devices.activator.rpi;

import net.audumla.automate.event.EventScheduler;
import net.audumla.devices.activator.AbstractActivator;
import net.audumla.devices.activator.ActivatorState;
import net.audumla.devices.activator.SetActivatorStateCommand;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class RPIGPIOActivator extends AbstractActivator<RPIGPIOActivatorProvider,SetActivatorStateCommand> {


    @Override
    protected boolean executeStateChange(ActivatorState newstate) {
        return false;
    }

}
