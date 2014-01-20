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

import net.audumla.automate.event.Event;
import net.audumla.automate.event.EventTarget;
import net.audumla.automate.event.EventTransactionListener;
import net.audumla.devices.activator.Activator;
import net.audumla.devices.activator.ActivatorState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface ActivatorProvider{

    /**
     * The key that is used within the id property bundle for each activator to associate it back to its provider
     */
    static String PROVIDER_ID = "providerid";

    /**
     * Performs any initialization that is required to startup the activitors
     * @throws Exception if the activiators cannot be started
     */
    void initialize() throws Exception;

    /**
     * performs any shut down activities. Generally this should deactivate all associated activators
     */
    void shutdown();

    /**
     *
     * @return the unique identifier for this provider
     */
    String getId();

    /**
     * Retrieves an activator using the id that has been generated for it during initialization
     * @param id the id property bundle
     * @return the activator associated with the id or null if it cannot be found
     */
    Activator getActivator(Properties id);

    /**
     *
     * @return all the activators associated with this provider
     */
    Collection<? extends Activator> getActivators();

    /**
     * Performs an atomic update if possible to all activators to the associated state.
     * All the activators in the map can be assumed to have originated from this provider
     * @param newStates a Map containing the activator as the key and the new state that should be assigned to that activator
     * @return true if the assignment of the activators completed successfully
     */
    boolean setCurrentStates(Map<Activator,ActivatorState> newStates) throws Exception;
}
