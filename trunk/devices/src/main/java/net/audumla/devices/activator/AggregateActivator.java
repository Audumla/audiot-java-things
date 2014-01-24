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

import net.audumla.automate.event.AbstractEvent;
import net.audumla.automate.event.AbstractEventTarget;
import net.audumla.automate.event.Event;
import net.audumla.devices.activator.factory.ActivatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class AggregateActivator extends EventTargetActivator<ActivatorFactory,ActivatorCommand>  {
    private static final Logger logger = LoggerFactory.getLogger(AggregateActivator.class);

    protected Collection<Activator> activators = new ArrayList<>();

    public AggregateActivator() {
        super();
        setName("Aggregate Activator ");
    }

    public AggregateActivator(Collection<Activator> activators) {
        this.activators = activators;
    }

    public void addActivator(Activator a) {
        activators.add(a);
        setName(getName()+"["+a.getName()+"]");
    }

    @Override
    protected void executeStateChange(ActivatorState newstate) throws Exception {

    }

    @Override
    public boolean setState(ActivatorState newstate) throws Exception {
        for (Activator a : activators) {
            a.setState(newstate);
        }
        return true;
    }
}
