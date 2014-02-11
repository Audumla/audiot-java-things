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

public class DeviceRegisterAttr implements DeviceChannel.Attribute {
    private static final Logger logger = LoggerFactory.getLogger(DeviceRegisterAttr.class);

    protected int register;

    public DeviceRegisterAttr(int register) {
        this.register = register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    public int getRegister() {
        return register;
    }

    @Override
    public String toString() {
        return "DeviceReadRegister{" +
                "register=0x" + Integer.toHexString(register) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceRegisterAttr that = (DeviceRegisterAttr) o;

        return register == that.register;

    }

}
