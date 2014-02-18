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

import net.audumla.deviceaccess.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class DefaultPeripheralAddressableChannel<P extends AddressablePeripheralChannel<? super P, ? super C, ? super M>, C extends PeripheralConfig<? super P>, M extends PeripheralMessage<? super P, ? super C, ? super M>> extends DefaultPeripheralChannel<P,C,M> implements AddressablePeripheralChannel<P,C,M>   {

    private Integer readAddress;
    private Integer writeAddress;
    private Integer addressSize;

    protected DefaultPeripheralAddressableChannel(PeripheralDescriptor<P, C> descriptor) {
        super(descriptor);
    }

    @Override
    public void setReadAddress(Integer addr) {
        this.readAddress = addr;
    }

    @Override
    public void setWriteAddress(Integer addr) {
        this.writeAddress = addr;
    }

    @Override
    public void setAddressSize(Integer size) {
        this.addressSize = size;
    }

    @Override
    public Integer getReadAddress() {
        return readAddress;
    }

    @Override
    public Integer getWriteAddress() {
        return writeAddress;
    }

    @Override
    public Integer getAddressSize() {
        return addressSize;
    }
}
