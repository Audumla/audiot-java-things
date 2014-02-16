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


import com.oracle.deviceaccess.BufferAccess;
import com.oracle.deviceaccess.Peripheral;
import com.oracle.deviceaccess.Transactional;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;


public interface I2CDevice extends BufferAccess<ByteBuffer>, Peripheral<I2CDevice>, Transactional, ByteChannel
{
    static interface Bus {
        I2CCombinedMessage	createCombinedMessage();
    }

    int read()
            throws IOException;

    int read(int subAddress, int subAddressSize, int skip, ByteBuffer dst)
            throws IOException;

    int read(int subAddress, int subAddressSize, ByteBuffer dst)
            throws IOException;

    int read(int skip, ByteBuffer dst)
            throws IOException;

    int read(ByteBuffer dst)
            throws IOException;

    int write(int subAddress, int subAddressSize, ByteBuffer dst)
            throws IOException;

    int write(ByteBuffer dst)
            throws IOException;

    void begin()
            throws IOException;

    void end()
            throws IOException;

    void write(int data)
            throws IOException;
}