package com.oracle.deviceaccess.i2cbus;

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

import com.oracle.deviceaccess.AddressablePeripheralMessage;
import com.oracle.deviceaccess.ClosedPeripheralException;
import com.oracle.deviceaccess.UnavailablePeripheralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;

public class I2CMessage implements AddressablePeripheralMessage<I2CDevice,I2CDeviceConfig,I2CMessage> {
    private static final Logger logger = LoggerFactory.getLogger(I2CMessage.class);

    @Override
    public I2CMessage appendRead(I2CDevice peripheral, int subAddress, int subAddressSize, ByteBuffer dst) throws IOException {
        return null;
    }

    @Override
    public I2CMessage appendWrite(I2CDevice peripheral, int subAddress, int subAddressSize, ByteBuffer dst) throws IOException {
        return null;
    }

    @Override
    public I2CMessage appendWrite(I2CDevice peripheral, int subAddress, int subAddressSize, int value) throws IOException {
        return null;
    }

    @Override
    public I2CMessage appendRead(I2CDevice peripheral, int subAddress, ByteBuffer dst) throws IOException {
        return null;
    }

    @Override
    public I2CMessage appendWrite(I2CDevice peripheral, int subAddress, ByteBuffer dst) throws IOException {
        return null;
    }

    @Override
    public I2CMessage appendWrite(I2CDevice peripheral, int subAddress, int value) throws IOException {
        return null;
    }

    @Override
    public I2CMessage appendRead(I2CDevice peripheral, ByteBuffer byteBuffer) throws ClosedPeripheralException {
        return null;
    }

    @Override
    public I2CMessage appendWrite(I2CDevice peripheral, ByteBuffer byteBuffer) throws IOException, ClosedPeripheralException {
        return null;
    }

    @Override
    public I2CMessage appendWrite(I2CDevice peripheral, int value) throws IOException, ClosedPeripheralException {
        return null;
    }

    @Override
    public I2CMessage appendSleep(Duration duration) {
        return null;
    }

    @Override
    public int[] transfer() throws IOException, UnavailablePeripheralException, ClosedPeripheralException {
        return new int[0];
    }
}
