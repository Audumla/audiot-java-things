package net.audumla.deviceaccess.impl;

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

import net.audumla.deviceaccess.AddressablePeripheralChannel;
import net.audumla.deviceaccess.PeripheralChannelMessage;
import net.audumla.deviceaccess.PeripheralConfig;
import net.audumla.deviceaccess.PeripheralDescriptor;

public abstract class DefaultAddressablePeripheralChannel<P extends AddressablePeripheralChannel<? super P, ? super C>, C extends PeripheralConfig<? super P>> extends DefaultPeripheralChannel<P,C> implements AddressablePeripheralChannel<P,C>   {

    private Integer readAddress;
    private Integer writeAddress;

    protected DefaultAddressablePeripheralChannel(PeripheralDescriptor<P, C> descriptor) {
        super(descriptor);
    }


    @Override
    public Integer getReadAddress() {
        return readAddress;
    }

    @Override
    public Integer getWriteAddress() {
        return writeAddress;
    }

}
