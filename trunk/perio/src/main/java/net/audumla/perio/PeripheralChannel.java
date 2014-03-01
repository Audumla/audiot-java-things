package net.audumla.perio;

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

public interface PeripheralChannel {
    /**
     * A mask is applied to every transaction performed on the channel. The mask is capable of handling a 64 bit value, however only
     * the low order bits up to a number equal to getBitWidth will ever be applied to transfer.
     * <p>
     * If a channel is 16 bits wide then only the lower 16 bits of the mask will ever be applied to the data being transferred.
     * The actual application of the mask is dependant on the implementing classes and the Writable/Readable Channel interfaces
     *
     * @param mask the mask to apply
     */
    void setMask(long mask);

    /**
     * Removes any mask that has been applied to this channel
     */
    void removeMask();

    /**
     * A channel is defined to be a specific bit width during the instantiation of the Peripheral it will transfer from.
     * This value is immutable and the only way of obtaining a channel with a different width is to configure a new Peripheral with
     * a different width
     *
     * @return the number of bits that this channel can transfer on an individual read or write
     */
    int getBitWidth();

    /**
     * Evaluates the maximum signed value that can be accepted on an individual read or write.
     * The value returned should be interpreted as an unsigned 32-bit integer.
     *
     * @return a value representing the maximum value this channel can transmit in a single transfer
     */
    default long getMaxValue() {
        return (long) Math.pow(2, getBitWidth());
    }

    /**
     * Returns the number of bytes required to hold the maximum transfer value ( or bit width) of this channel
     * A channel with a bit width of 1 to 8 will need 1 byte, a bit width of 9 to 16 will need 2, etc
     *
     * @return the minimum number of bytes needed to store a value of getBitWidth
     */
    default int getDeviceWidth() {
        return (int) (Math.round((getBitWidth() / 8) + 0.4));
    }

}
