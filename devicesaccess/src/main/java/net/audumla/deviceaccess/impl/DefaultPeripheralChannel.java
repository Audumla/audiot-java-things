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

import net.audumla.deviceaccess.PeripheralChannel;
import net.audumla.deviceaccess.PeripheralConfig;
import net.audumla.deviceaccess.PeripheralDescriptor;
import net.audumla.deviceaccess.PeripheralMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultPeripheralChannel<P extends PeripheralChannel<? super P, ? super C, ? super M>,C extends PeripheralConfig<? super P>, M extends PeripheralMessage<? super P, ? super C, ? super M>> implements PeripheralChannel<P,C,M> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPeripheralChannel.class);

    private ChannelWidth width = ChannelWidth.WIDTH8;
    private Integer mask;
    private PeripheralDescriptor<P,C> descriptor;

    protected DefaultPeripheralChannel(PeripheralDescriptor<P, C> descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void setWidth(ChannelWidth width) {
        this.width = width;
    }

    @Override
    public ChannelWidth getWidth() {
        return width;
    }

    @Override
    public Integer getMask() {
        return mask;
    }

    @Override
    public void setMask(Integer mask) {
        this.mask = mask;
    }

    @Override
    public PeripheralDescriptor<P, C> getDescriptor() {
        return descriptor;
    }
}
