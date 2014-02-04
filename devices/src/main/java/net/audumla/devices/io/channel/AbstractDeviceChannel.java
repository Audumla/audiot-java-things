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

import java.nio.Buffer;
import java.util.*;

public abstract class  AbstractDeviceChannel implements DeviceChannel {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDeviceChannel.class);

    public static class PositionAttribute implements Attribute {
        private int position;
        private Collection<Attribute> attributes = new HashSet<>();

        public PositionAttribute() {
        }

        public PositionAttribute(int position) {
            this.position = position;
        }

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

    protected Map<Integer, PositionAttribute> bufferAttributes = new TreeMap<>();

    @Override
    public <T extends Buffer> T setAttribute(T buffer, Attribute attr) {
        setAttribute(buffer.position(),attr);
        return buffer;
    }

    @Override
    public void setAttribute(int pos, Attribute attr) {
        PositionAttribute posAttr = bufferAttributes.get(pos);
        if (posAttr == null) {
            posAttr = attr instanceof PositionAttribute ? (PositionAttribute) attr : new PositionAttribute(pos, attr);
            bufferAttributes.put(posAttr.getPosition(), posAttr);
        } else {
            posAttr.addAttribute(attr);
        }
    }

    @Override
    public Collection<? extends Attribute> getAttributes() {
        return bufferAttributes.values();
    }

    protected void addDefaultAttribute(Attribute ... attr) {
        for (Attribute a : attr) {
            setAttribute(0,a);
        }
    }

}
