package net.audumla.devices.activator.provider.rpi;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.impl.GpioControllerImpl;
import net.audumla.devices.activator.AbstractActivator;
import net.audumla.devices.activator.ActivatorListener;

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

    protected Pin pin;

    public GPIOActivator(int pinid) {
        try {
            for (Pin pin : PCF8574Pin.ALL) {
                if (pin.getAddress() == pinid) {
                    this.pin = pin;
                }
            }
            GpioController gpio = new GpioControllerImpl(new PCF8574GpioProvider(1,1));
            GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(PCF8574Pin.GPIO_00, "", PinState.HIGH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // provision gpio pin #01 as an output pin and turn on
    }

    @Override
    protected boolean doActivate(Collection<ActivatorListener> listeners) {
        return false;
    }

    @Override
    protected boolean doDeactivate(Collection<ActivatorListener> listeners) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
