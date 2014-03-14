package net.audumla.perio.gpio;

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

import net.audumla.perio.SelectableIOPeripheral;
import net.audumla.perio.UnsupportedAccessModeException;

/**
 * A GPIOPort represents a collection of GPIOPins grouped within a common platform or source.
 * The pins can be referenced and manipulated individually or grouped as a PeripheralChannel via the GetXChannel methods
 * A channel of more than one pin will allow a value for each write of no greater than 2 to the power of pin count.
 *  i.e. a channel of 3 pins will have a maximum value of 8 for each value. Multiple values can transferred sequentially using the
 *       channel read/write ByteBuffer methods.
 *
 *  Where a channel has a value greater than a single byte (255) then multiple bytes in the ByteBuffer will be used to allow values of the
 *  appropriate size.
 *  i.e. a channel that has 12 pins will have a maximum value of 4096. This will require a minimum of 2 bytes to represent. Any left over bytes
 *       will be ignored during the write. To assist in creating the correct sized buffer, an Char, Integer, Long buffer can be used prior to passing
 *       the underlying ByteBuffer to the channel methods
 *
 *  Where possible the underlying implementation will attempt to optimize the writes to multiple GPIO pins by first converting the ByteBuffer into
 *  a value that can written atomically to the underlying device. As the pins chosen for the channel may not be sequential in the underlying device,
 *  bit shifting transformation may need to be performed to allow optimized transfer of the data.
 *
 *  Where optimization is not possible, then the underlying implementation will get or set the states of the pins individually.
 *
 *  When creating a channel, the pins chosen will be set to be input, output, or both depending on the configuration of the channel. This means that
 *  if a ReadWriteChannel is requested with Pin1, Pin2, Pin3 as input, and Pin1, Pin2, Pin4 as output then Pin1 and Pin2 will be set as Input/Output,
 *  Pin3 will be set as input, and Pin4 will be set as output.
 *
 *
 */
public interface GPIOPort extends SelectableIOPeripheral<GPIOPort, GPIOPortConfig, GPIOPin[]> {

    /**
     * Allows a callback to be issued when the state of a pin changes based on the trigger type. This will be performed asynchronously
     * and should execute as an interupt where possible. The underlying implementation will need to support interrupts otherwise polling
     * will need to be configured according to the devices configuration
     *
     * @param trigger the trigger event
     * @param listener the listener of the trigger event
     * @param pins the pins to listen on
     * @throws java.io.IOException if the triggering was unsuccessful due to an underlying device issue
     */
    void setInputListener(GPIOPin.Trigger trigger, PinListener listener, GPIOPin ... pins) throws java.io.IOException;

    /**
     * Sets the direction of the pin as allowed by the device
     *
     * @param direction the direction to set the pin to
     * @param pins the pins to be set
     * @throws net.audumla.perio.UnsupportedAccessModeException thrown if the pin does not support the specified direction
     */
    void setDirection(GPIOPin.Direction direction, GPIOPin ... pins) throws UnsupportedAccessModeException;

    /**
     * Each GPIOPort provides a collection of pins. This method allows access to all of the pins that the device has made
     * available.
     *
     * @return the available pins
     */
    GPIOPin[] getPins();

}