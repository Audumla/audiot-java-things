package net.audumla.deviceaccess.i2cbus.rpi.jni;

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

import net.audumla.utils.jni.LibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPiPeripheralProviderNative {
    // private constructor
    private RPiPeripheralProviderNative () {
        // forbid object construction
    }

    static {
        // Load the platform library
        LibraryLoader.load("audumlaRPi_bcm2835", "audumlaRPi_bcm2835.so");
    }

    /**
     * Initializes the raspberry pi for access to its peripherals
     *
     * @return greater that 1 if successfully initialized
     */
    public static native int init();

    /**
     * Shuts down and releases any handles that the application is holding
     *
     */
    public static native void shutdown();

    /**
     * Returns the board revision number
     *
     */
    public static native int getRevision();


}
