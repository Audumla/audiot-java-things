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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DummyMessageChannel implements ReadWritePeripheralChannel {
    private static final Logger logger = LoggerFactory.getLogger(DummyMessageChannel.class);

    public byte bytes[] = new byte[2048];
    protected ByteBuffer rx = ByteBuffer.wrap(bytes);
    protected ByteBuffer tx = ByteBuffer.wrap(bytes);

    @Override
    public void setMask(long mask) {

    }

    @Override
    public void removeMask() {

    }

    @Override
    public int getBitWidth() {
        return 8;
    }

    @Override
    public int read() throws IOException {
        return rx.get();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return read(dst,0,dst.remaining());
    }

    @Override
    public int read(ByteBuffer dst, int offset, int size) throws IOException {
        rx.get(dst.array(),offset,size);
        dst.position(dst.position()+size);
        return size;
    }

    @Override
    public int write(int value) throws IOException {
        tx.put((byte) value);
        return 1;
    }

    @Override
    public int write(ByteBuffer dst) throws IOException {
        return write(dst,0,dst.limit());
    }

    @Override
    public int write(ByteBuffer dst, int offset, int size) throws IOException {
        tx.put(dst.array(),offset,size);
        dst.position(dst.position()+size);
        return size;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }
}
