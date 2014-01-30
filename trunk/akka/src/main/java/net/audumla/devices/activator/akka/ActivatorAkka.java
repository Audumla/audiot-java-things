package net.audumla.devices.activator.akka;

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

import akka.actor.UntypedActor;
import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ActivatorAkka extends UntypedActor implements Activator {
    private static final Logger logger = LoggerFactory.getLogger(ActivatorAkka.class);

    protected Activator activatorTarget;

    public ActivatorAkka(Activator activatorTarget) {
        this.activatorTarget = activatorTarget;
    }

    public Activator getActivatorTarget() {
        return activatorTarget;
    }

    @Override
    public void onReceive(Object message) throws Exception {

    }

    @Override
    public String getName() {
        return getActivatorTarget().getName();
    }

    @Override
    public void setName(String name) {
        getActivatorTarget().setName(name);

    }

    @Override
    public boolean setState(ActivatorState state) throws Exception {
        return getActivatorTarget().setState(state);
    }

    @Override
    public ActivatorState getState() {
        return getActivatorTarget().getState();
    }

    @Override
    public Properties getId() {
        return getActivatorTarget().getId();
    }

    @Override
    public void allowSetState(boolean set) {
        getActivatorTarget().allowSetState(set);

    }

    @Override
    public boolean canSetState() {
        return getActivatorTarget().canSetState();
    }

    @Override
    public void allowVariableState(boolean var) {
        getActivatorTarget().allowVariableState(var);

    }

    @Override
    public boolean hasVariableState() {
        return getActivatorTarget().hasVariableState();
    }
}
