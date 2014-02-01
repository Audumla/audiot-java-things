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

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Collection;

public interface DeviceChannel extends ByteChannel {

    /**
     * An Attribute is a marker interface that is used by implementations of the DeviceChannel to associate
     * attributes to given positions within a ByteBuffer. The exact implementation of the DeviceChannel will
     * determine the types of attributes that it supports
     */
    public interface Attribute {
    }

    /**
     * Adds an attribute to the channel that applies to the current position of the given buffer
     * When the buffer is being written or read to/from the channel the attribute will be enacted up at the appropriate
     * position. This could be to change the state of the channel at a position in the buffer so that it writes
     * to a different location of the channel or pauses the channel for a set time to allow the channel to
     * receive the data correctly before moving on to the rest of the byte stream.
     * A reference to The buffer may be stored within the channel so the the attribute list can be associated back
     * to each Buffer.
     * Once the buffer is written or read the reference will be removed and all associate attributes will be lost
     * unless an external reference to the result of getAttributes is made
     *
     * @param buffer the buffer to associate the Attribute to.
     * @param attr   the attribute to associate to the buffer
     * @return the buffer
     */
    ByteBuffer setAttribute(ByteBuffer buffer, Attribute attr);

    /**
     * Returns the associated attributes of the given buffer
     *
     *
//     * @param buffer the buffer to return attributes for
     * @return the collection of attributes
     */
    Collection<? extends Attribute> getAttributes();

    /**
     * Associates all of the attributes in the given collection to the given buffer
     *
     * @param buffer the buffer of interest
     * @param attributes the attributes that will be associated with the buffer
     */
    void setAttributes(ByteBuffer buffer, Collection<Attribute> attributes);

    /**
     *
     * @param attr the attribute class to be tested
     * @return true if the attribute class is supported by the channel
     */
    boolean supportsAttribute(Class<? extends Attribute> attr);

    /**
     * Creates a new channel based on the existing instance. The given attributes will be considered primary attributes and associated with every buffer
     * written to the channel. These attributes will be applied before any bytes are written to the channel.
     * All primary attributes of the current channel will be passed onto the new channel instance
     * @param attr the attributes to set as primary attributes for the new channel
     */
    DeviceChannel createChannel(Attribute ... attr);

    /**
     * Writes a single byte using only the primary attributes associated with this channel
     * @param b the byte to be written
     */
    void write(byte b) throws IOException;

}
