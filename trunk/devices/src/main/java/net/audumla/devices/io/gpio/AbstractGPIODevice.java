package net.audumla.devices.io.gpio;

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

import java.util.Arrays;

public class AbstractGPIODevice implements GPIODevice {
    private static final Logger logger = LoggerFactory.getLogger(AbstractGPIODevice.class);

    @Override
    public <T extends GPIOState> void setState(T... state) {
        try {
            Arrays.asList(state).stream().forEach((t) -> t.applyState(this));
        } catch (ClassCastException e) {
            logger.error("Unable to set state " + state[0].getClass() + " on " + this.getClass());
        }
    }

    @Override
    public <T extends GPIOState> void getState(T... state) {
        try {
            Arrays.asList(state).stream().forEach((t) -> t.retrieveState(this));
        } catch (ClassCastException e) {
            logger.error("Unable to get state " + state[0].getClass() + " for " + this.getClass());
        }
    }
}
