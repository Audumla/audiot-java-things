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

import net.audumla.automate.event.EventTransaction;
import net.audumla.automate.event.EventTransactionListener;
import net.audumla.collections.Pair;
import net.audumla.devices.activator.factory.ActivatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public abstract class EventTransactionActivatorFactory<TActivator extends EventTargetActivator> implements ActivatorFactory<TActivator>, EventTransactionListener<ActivatorCommand, TActivator> {
    private static final Logger logger = LoggerFactory.getLogger(EventTransactionActivatorFactory.class);

    private String id;

    protected EventTransactionActivatorFactory(String id) {
        this.id = id;
        logger.info("Instantiating Activator Factory ["+id+"]");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean onTransactionCommit(EventTransaction transaction, Collection<Pair<TActivator, ActivatorCommand>> events) throws Exception {
        // this should be overridden to commit all the states as an atomic transaction
        boolean result = true;
        for (Pair<TActivator, ActivatorCommand> e : events) {
            result &= e.getItem1().setState(e.getItem2().getNewState());
        }
        return result;
    }

    @Override
    public String toString() {
        return getId();
    }
}
