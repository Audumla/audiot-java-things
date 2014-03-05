package net.audumla.perio.gpio.rpi.jni;

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

import net.audumla.perio.gpio.GPIOPin;
import net.audumla.perio.jni.ErrorHandler;
import net.audumla.utils.jni.LibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;

public class RPiGPIONative {

    // private constructor
    private RPiGPIONative() {
        // forbid object construction
    }

    static {
        // Load the platform library
        LibraryLoader.load("audumlaRPiI2C_bcm2835", "audumlaRPiI2C_bcm2835.so");                    }


    public static native void waitForGPIOTrigger(int handle, int timeout, ErrorHandler handler);

    public static native int openGPIO(int pin, ErrorHandler handler);

    public static native void closeGPIO(int handle, ErrorHandler handler);

    public static native void enableAsyncFallingEdge(int pin, ErrorHandler handler);

    public static native void disableAsyncFallingEdge(int pin, ErrorHandler handler);

    public static native void enableAsyncRisingEdge(int pin, ErrorHandler handler);

    public static native void disableAsyncRisingEdge(int pin, ErrorHandler handler);

    public static native void enableHighDetect(int pin, ErrorHandler handler);

    public static native void disableHighDetect(int pin, ErrorHandler handler);

    public static native void enableLowDetect(int pin, ErrorHandler handler);

    public static native void disableLowDetect(int pin, ErrorHandler handler);
}
