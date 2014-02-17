package com.oracle.deviceaccess;

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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

public class PeripheralEvent<P extends Peripheral<? super P,? super C>,C extends PeripheralConfig<? super P>, V> {
    private static final Logger logger = LoggerFactory.getLogger(PeripheralEvent.class);

    private Instant firstTimeStamp;
    private Instant lastTimeStamp;
    private final P peripheral;
    private final V value;
    private int count = 0;


    public PeripheralEvent(P peripheral, V value) {
        this.peripheral = peripheral;
        this.value = value;
        this.firstTimeStamp = Instant.now();
        this.lastTimeStamp = firstTimeStamp;
    }

    public PeripheralEvent(P peripheral, V value, Instant timeStamp) {
        this.peripheral = peripheral;
        this.value = value;
        this.firstTimeStamp = timeStamp;
        this.lastTimeStamp = firstTimeStamp;
    }

    public P getPeripheral() {
        return peripheral;
    }

    public V getValue() {
        return value;
    }

    public Instant getTimeStamp() {
        return firstTimeStamp;
    }

    public Instant getLastTimeStamp() {
        return lastTimeStamp;
    }

    void addOccurence(V b, TemporalAmount offset) {
        lastTimeStamp = firstTimeStamp.plus(offset);
        ++count;
    }

    public int getCount() {
        return count;
    }
}
