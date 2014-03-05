package net.audumla.perio.gpio.rpi;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.perio.PeripheralDescriptor;
import net.audumla.perio.ReadWritePeripheralChannel;
import net.audumla.perio.ReadablePeripheralChannel;
import net.audumla.perio.WritablePeripheralChannel;
import net.audumla.perio.gpio.*;
import net.audumla.perio.gpio.rpi.jni.RPiGPIONative;
import net.audumla.perio.jni.DefaultErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RPiGPIOPlatformPort implements GPIOPort {
    private static final Logger logger = LoggerFactory.getLogger(RPiGPIOPlatformPort.class);

    protected final Map<GPIOPin, GPIOPinAsyncListener> asyncListeners = new HashMap<>();

    protected class GPIOPinAsyncListener extends DefaultErrorHandler implements Runnable {

        public Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);

        protected Collection<PinListener> listeners = new ArrayList<>();
        protected GPIOPin pin;
        protected boolean shutdown = false;
        private Thread thread;

        public void shutdown() {
            try {
                shutdown = true;
                thread.join(DEFAULT_TIMEOUT.toMillis() * 3);
            } catch (InterruptedException e) {
                logger.error("Failed to shutdown GPIO listener thread [" + pin.toString() + "]", e);
            }
        }

        public void setTrigger(GPIOPin.Trigger trigger) {
            int pinId = getBCM2835PinNumber(pin);
            switch (trigger) {
                case TRIGGER_BOTH_EDGES:
                    RPiGPIONative.enableAsyncFallingEdge(pinId, this);
                    logError();
                    RPiGPIONative.enableAsyncRisingEdge(pinId, this);
                    logError();
                    break;
                case TRIGGER_BOTH_LEVELS:
                    RPiGPIONative.enableHighDetect(pinId, this);
                    logError();
                    RPiGPIONative.enableLowDetect(pinId, this);
                    logError();
                    break;
                case TRIGGER_FALLING_EDGE:
                    RPiGPIONative.enableAsyncFallingEdge(pinId, this);
                    logError();
                    break;
                case TRIGGER_RISING_EDGE:
                    RPiGPIONative.enableAsyncRisingEdge(pinId, this);
                    logError();
                    break;
                case TRIGGER_HIGH_LEVEL:
                    RPiGPIONative.enableHighDetect(pinId, this);
                    logError();
                    break;
                case TRIGGER_LOW_LEVEL:
                    RPiGPIONative.enableLowDetect(pinId, this);
                    logError();
                    break;
            }
        }

        public void addListener(PinListener listener) {
            listeners.add(listener);
        }

        @Override
        public void run() {
            int gpioFileHandle = 0;
            try {
                gpioFileHandle = RPiGPIONative.openGPIO(getBCM2835PinNumber(pin), this);
                if (!hasError()) {
                    thread = Thread.currentThread();
                    while (!shutdown) {
                        RPiGPIONative.waitForGPIOTrigger(gpioFileHandle, (int) DEFAULT_TIMEOUT.toMillis(), this);
                        if (!hasError()) {
                            final PinEvent event = new PinEvent(pin, pin.getValue(), Instant.now());
                            listeners.forEach(l -> l.valueChanged(event));
                        } else {
                            shutdown = true;
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Failure waiting for GPIO pin trigger event [" + pin.toString() + "]", getException());
            } finally {
                if (gpioFileHandle > 0) {
                    RPiGPIONative.closeGPIO(gpioFileHandle, this);
                    if (hasError()) {
                        logger.error("Failure waiting for GPIO pin trigger event [" + pin.toString() + "]", getException());
                    }
                }
            }
        }
    }

    protected int getBCM2835PinNumber(GPIOPin pin) {
        return pin.getPinIndex();
    }

    @Override
    public void setInputListener(GPIOPin.Trigger trigger, PinListener listener, GPIOPin... pins) throws IOException {
        for (GPIOPin pin : pins) {
            GPIOPinAsyncListener alistener = getPinAsyncListener(pin, trigger);
            alistener.addListener(listener);
        }
    }

    @Override
    public void setDirection(GPIOPin.Direction direction, GPIOPin... pins) {

    }

    @Override
    public GPIOPin[] getPins() {
        return new GPIOPin[0];
    }

    @Override
    public ReadWritePeripheralChannel getReadWriteChannel(GPIOPin[] read, GPIOPin[] write) {
        return null;
    }

    @Override
    public ReadWritePeripheralChannel getReadWriteChannel(GPIOPin[] readWrite) {
        return null;
    }

    @Override
    public ReadablePeripheralChannel getReadChannel(GPIOPin[] read) {
        return null;
    }

    @Override
    public WritablePeripheralChannel getWriteChannel(GPIOPin[] write) {
        return null;
    }

    @Override
    public PeripheralDescriptor<GPIOPort, GPIOPortConfig> getDescriptor() {
        return null;
    }

    public GPIOPinAsyncListener getPinAsyncListener(GPIOPin pin, GPIOPin.Trigger trigger) {
        synchronized (asyncListeners) {
            GPIOPinAsyncListener listener = asyncListeners.get(pin);
            if (trigger == GPIOPin.Trigger.TRIGGER_NONE) {
                if (listener != null) {
                    int pinId = getBCM2835PinNumber(pin);
                    RPiGPIONative.disableAsyncFallingEdge(pinId, listener);
                    listener.logError();
                    RPiGPIONative.disableAsyncRisingEdge(pinId, listener);
                    listener.logError();
                    RPiGPIONative.disableHighDetect(pinId, listener);
                    listener.logError();
                    RPiGPIONative.disableLowDetect(pinId, listener);
                    listener.logError();
                    listener.shutdown();
                    asyncListeners.remove(pin);
                }
            } else {
                if (listener == null) {
                    listener = new GPIOPinAsyncListener();
                    Thread thread = new Thread(listener);
                    thread.start();
                    asyncListeners.put(pin, listener);
                }
                listener.setTrigger(trigger);
            }
            return listener;
        }
    }
}
