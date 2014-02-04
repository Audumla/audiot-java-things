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

public class FixedWaitAttr implements ActionAttr {
    private static final Logger logger = LoggerFactory.getLogger(FixedWaitAttr.class);

    protected long millis;
    protected int nanos;

    public FixedWaitAttr(long millis, int nanos) {
        this.millis = millis;
        this.nanos = nanos;
    }

    public FixedWaitAttr(long millis) {
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

    @Override
    public void performAction() {
        synchronized (Thread.currentThread()) {
            try {
//                logger.debug("Pause "+getMillis()+":"+getNanos());
                Thread.sleep(getMillis(), getNanos());
            } catch (InterruptedException e) {
                logger.warn("Unable to execute wait attribute", e);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedWaitAttr that = (FixedWaitAttr) o;
        return millis == that.millis && nanos == that.nanos;
    }

}
