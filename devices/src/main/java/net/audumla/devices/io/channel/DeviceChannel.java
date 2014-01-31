package net.audumla.devices.io.channel;

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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

public abstract class DeviceChannel implements AttributeChannel {
    private static final Logger logger = LoggerFactory.getLogger(DeviceChannel.class);

    public static class PositionAttribute implements Attribute {
        private int position;
        private Collection<Attribute> attributes = new ArrayList<>();

        public PositionAttribute(int position, Attribute attribute) {
            this.position = position;
            attributes.add(attribute);
        }

        /**
         * The position that the attribute will be enacted upon when reading or writing the buffer associated with it
         *
         * @return the int position in the associated buffer
         */
        public int getPosition() {
            return position;
        }

        public Collection<Attribute> getAttributeReferences() {
            return attributes;
        }

        public void addAttribute(Attribute attr) {
            if (attr instanceof PositionAttribute) {
                for (Attribute a : ((PositionAttribute) attr).getAttributeReferences())
                    addAttribute(a);
            } else {
                attributes.add(attr);
            }
        }

    }

    protected Map<Buffer, Map<Integer, PositionAttribute>> bufferAttributes = new HashMap<>();

    protected Map<Integer, PositionAttribute> getBufferAttributes(Buffer buffer) {
        Map<Integer, PositionAttribute> attrs = bufferAttributes.get(buffer);
        if (attrs == null) {
            attrs = new TreeMap<Integer, PositionAttribute>();
        }
        return attrs;
    }

    @Override
    public ByteBuffer setAttribute(ByteBuffer buffer, Attribute attr) {
        PositionAttribute posAttr = getBufferAttributes(buffer).get(buffer.position());
        if (posAttr == null) {
            posAttr = attr instanceof PositionAttribute ? (PositionAttribute) attr : new PositionAttribute(buffer.position(), attr);
            getBufferAttributes(buffer).put(posAttr.getPosition(), posAttr);
        } else {
            posAttr.addAttribute(attr);
        }

        return buffer;
    }

    @Override
    public Collection<? extends Attribute> getAttributes(ByteBuffer buffer) {
        Map<Integer, PositionAttribute> attrs = bufferAttributes.get(buffer);
        return attrs == null ? null : attrs.values();
    }

    @Override
    public void setAttributes(ByteBuffer buffer, Collection<Attribute> attributes) {
        Map<Integer, PositionAttribute> attrs = getBufferAttributes(buffer);
        for (Attribute a : attributes) {
            setAttribute(buffer, a);
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        Map<Integer, PositionAttribute> attr = getBufferAttributes(dst);
        Byte b;
        int i = 0;
        do {
            PositionAttribute attrs;
            if ((attrs = attr.get(i)) != null) {
                for (Attribute a : attrs.getAttributeReferences()) {
                    applyAttribute(a);
                }
            }
            if ((b = read()) != null) {
                dst.put(b);
                ++i;
            }
        }
        while (b != null);
        return i;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        Map<Integer, PositionAttribute> attr = getBufferAttributes(src);
        int i;
        for (i = 0; i < src.limit(); i++) {
            byte b = src.get(i);
            PositionAttribute attrs;
            if ((attrs = attr.get(i)) != null) {
                for (Attribute a : attrs.getAttributeReferences()) {
                    applyAttribute(a);
                }
            }
            write(b);
        }
        bufferAttributes.remove(src);
        return i;
    }

    protected abstract Byte read() throws IOException;

    protected abstract void write(byte b) throws IOException;

    protected abstract void applyAttribute(Attribute a);
}
