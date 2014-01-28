package net.audumla.devices.i2c.rpi;

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

import net.audumla.devices.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RPII2CBusFactory implements I2CBus.I2CBusFactory{
    private static final Logger logger = LoggerFactory.getLogger(RPII2CBusFactory.class);

    private Map<Integer,I2CBus> busRegistry = new HashMap<Integer,I2CBus>();

    @Override
    public I2CBus getInstance(int busid) throws IOException {
        I2CBus bus = busRegistry.get(busid);
        if (bus == null) {
            bus = new RPII2CBus(busid);
            busRegistry.put(busid,bus);
        }
        return bus;
    }
}
