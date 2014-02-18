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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public interface PeripheralChannel<P extends PeripheralChannel<? super P, ? super C, ? super M>, C extends PeripheralConfig<? super P>, M extends PeripheralMessage<? super P, ? super C, ? super M>> extends Peripheral<P, C>, ByteChannel {
    public enum ChannelWidth {
        WIDTH8(1), WIDTH16(2), WIDTH32(4);

        ChannelWidth(int bytes) {
            this.bytes = bytes;
        }

        public int byteSize() {return bytes;}

        protected int bytes;
    }

    int write(byte... data) throws IOException;

    int write(ByteBuffer dst, int offset, int size) throws IOException;

    int read() throws IOException;

    int read(ByteBuffer dst) throws IOException;

    int read(ByteBuffer dst, int offset, int size) throws IOException;

    void setWidth(ChannelWidth width);

    ChannelWidth getWidth();

    void setMask(Integer mask);

    Integer getMask();

    M createMessage();

}
