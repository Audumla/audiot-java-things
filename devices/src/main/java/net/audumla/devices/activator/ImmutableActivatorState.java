package net.audumla.devices.activator;

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

public class ImmutableActivatorState implements ActivatorState {
    private static final Logger logger = LoggerFactory.getLogger(ImmutableActivatorState.class);
    private Float value;
    private String name;

    public ImmutableActivatorState(Float value) {
        this.value = value;
    }

    public ImmutableActivatorState(Float value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (getName() == null) {
            return "Value:" + getValue();
        } else {
            return getName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableActivatorState)) return false;

        ImmutableActivatorState that = (ImmutableActivatorState) o;

        return Math.abs(value - that.value) < 5.96e-08;
//        return Precision.equals(value, that.value,5.96e-08);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
