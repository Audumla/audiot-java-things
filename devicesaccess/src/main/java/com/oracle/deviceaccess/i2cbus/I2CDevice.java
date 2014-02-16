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


public abstract interface I2CDevice extends BufferAccess<ByteBuffer>, Peripheral<I2CDevice>, Transactional, ByteChannel
{

    public abstract int read()
            throws IOException;

    public abstract int read(int paramInt1, int paramInt2, int paramInt3, ByteBuffer paramByteBuffer)
            throws IOException;

    public abstract int read(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer)
            throws IOException;

    public abstract int read(int paramInt, ByteBuffer paramByteBuffer)
            throws IOException;

    public abstract int read(ByteBuffer paramByteBuffer)
            throws IOException;

    public abstract int write(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer)
            throws IOException;

    public abstract int write(ByteBuffer paramByteBuffer)
            throws IOException;

    public abstract void begin()
            throws IOException;

    public abstract void end()
            throws IOException;

    public abstract void write(int paramInt)
            throws IOException;
}