package net.audumla.deviceaccess;

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


/**
 * The Peripheral interface represents peripheral devices in the system. This interface provides generic methods for handling peripherals
 * but imposes no pre set logic on how the peripheral operates.
 * All peripherals must implement the Peripheral interface.
 *
 * When a peripheral device is open in shared mode then access synchronization may be performed by invoking tryLock and unlock. Peripheral
 * locks are held on a per Peripheral instance basis. When the same peripheral device is open twice in shared access mode by the same application,
 * locking one of the Peripheral instances will prevent the other form being accessed/used.*
 *
 * @param <P>  the implementation class of the Peripheral
 * @param <C>  the configuration class for the Peripheral
 */
public interface Peripheral<P extends Peripheral<? super P, ? super C>, C extends PeripheralConfig<? super P>> {

    PeripheralDescriptor<P, C> getDescriptor();

}