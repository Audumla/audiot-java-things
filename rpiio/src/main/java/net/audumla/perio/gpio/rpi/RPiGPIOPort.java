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

import net.audumla.perio.ReadWritePeripheralChannel;
import net.audumla.perio.PeripheralDescriptor;
import net.audumla.perio.ReadablePeripheralChannel;
import net.audumla.perio.WritablePeripheralChannel;
import net.audumla.perio.gpio.GPIOPin;
import net.audumla.perio.gpio.GPIOPort;
import net.audumla.perio.gpio.GPIOPortConfig;
import net.audumla.perio.gpio.PortListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RPiGPIOPort implements GPIOPort {
    private static final Logger logger = LoggerFactory.getLogger(RPiGPIOPort.class);

    @Override
    public int getDirection() throws IOException {
        return 0;
    }

    @Override
    public int getMaxValue() throws IOException {
        return 0;
    }

    @Override
    public void setDirection(int i) throws IOException {

    }

    @Override
    public void setInputListener(PortListener portListener) throws IOException {

    }

    @Override
    public GPIOPin[] getPins() {
        return new GPIOPin[0];
    }

    @Override
    public WritablePeripheralChannel getWriteChannel(GPIOPin[] write) {
        return null;
    }

    @Override
    public ReadablePeripheralChannel getReadChannel(GPIOPin[] read) {
        return null;
    }

    @Override
    public PeripheralDescriptor<GPIOPort, GPIOPortConfig> getDescriptor() {
        return null;
    }

    @Override
    public ReadWritePeripheralChannel getReadWriteChannel(GPIOPin[] read, GPIOPin[] write) {
        return null;
    }

    @Override
    public ReadWritePeripheralChannel getReadWriteChannel(GPIOPin[] readWrite) {
        return null;
    }
}
