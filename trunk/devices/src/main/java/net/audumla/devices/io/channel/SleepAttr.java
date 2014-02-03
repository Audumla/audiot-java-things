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

public class SleepAttr implements DeviceChannel.Attribute {
    private static final Logger logger = LoggerFactory.getLogger(SleepAttr.class);

    protected long millis;
    protected int nanos;

    public SleepAttr(long millis, int nanos) {
        this.millis = millis;
        this.nanos = nanos;
    }

    public SleepAttr(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }

    public int getNanos() {
        return nanos;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public void setNanos(int nanos) {
        this.nanos = nanos;
    }

    public void sleep() {
        synchronized (Thread.currentThread()) {
            try {
//                logger.debug("Pause "+getMillis()+":"+getNanos());
                Thread.sleep(getMillis(), getNanos());
            } catch (InterruptedException e) {
                logger.warn("Unable to execute sleep attribute", e);
            }
        }
    }

    @Override
    public String toString() {
        return "Sleep{" +
                "millis=" + millis +
                ", nanos=" + nanos +
                '}';
    }
}
