package net.audumla.devices.activator.provider.rpi;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.impl.GpioControllerImpl;
import net.audumla.devices.activator.AbstractActivator;
import net.audumla.devices.activator.ActivatorListener;
import net.audumla.devices.activator.ActivatorState;

import java.io.IOException;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPIOActivator extends AbstractActivator {

    @Override
    protected boolean executeStateChange(ActivatorState newstate, Collection<ActivatorListener> listeners) {
        return false;
    }
}
