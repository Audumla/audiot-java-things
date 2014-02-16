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

public class I2CCombinedMessage {
    java.util.ArrayList<com.oracle.deviceaccess.i2cbus.I2CCombinedMessage.Message> messageList;
    int messageBus;
    boolean isAlreadyTransferedOnce;
    int rxMessageCount;

    void check(com.oracle.deviceaccess.i2cbus.I2CCombinedMessage.Message message) throws com.oracle.deviceaccess.ClosedPeripheralException { }

    public I2CCombinedMessage() { }

    public com.oracle.deviceaccess.i2cbus.I2CCombinedMessage appendRead(com.oracle.deviceaccess.i2cbus.I2CDevice i2CDevice, java.nio.ByteBuffer byteBuffer) throws com.oracle.deviceaccess.ClosedPeripheralException {
        return this;
    }

    public com.oracle.deviceaccess.i2cbus.I2CCombinedMessage appendRead(com.oracle.deviceaccess.i2cbus.I2CDevice i2CDevice, int i, java.nio.ByteBuffer byteBuffer) throws java.io.IOException, com.oracle.deviceaccess.ClosedPeripheralException {
        return this;
    }

    public com.oracle.deviceaccess.i2cbus.I2CCombinedMessage appendWrite(com.oracle.deviceaccess.i2cbus.I2CDevice i2CDevice, java.nio.ByteBuffer byteBuffer) throws java.io.IOException, com.oracle.deviceaccess.ClosedPeripheralException {
        return this;
    }

    public int[] transfer() throws java.io.IOException, com.oracle.deviceaccess.UnavailablePeripheralException, com.oracle.deviceaccess.ClosedPeripheralException {
        return null;
    }

    private class Message {
        public com.oracle.deviceaccess.i2cbus.I2CDevice device;
        public java.nio.ByteBuffer buf;
        public int skip;
        public boolean isRx;

        public Message(com.oracle.deviceaccess.i2cbus.I2CDevice p2, java.nio.ByteBuffer p3, int p4, boolean p5) {
        }
    }
}