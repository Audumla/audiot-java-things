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

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public interface ActivatorProvider<TActivator extends Activator> {

    /**
     * The key that is used within the id property bundle for each activator to associate it back to its provider
     */
    static String PROVIDER_ID = "providerid";

    /**
     * Performs any initialization that is required to startup the activitors
     * @throws Exception if the activiators cannot be started
     */
    default void initialize() throws Exception {

    }

    /**
     * performs any shut down activities. Generally this should deactivate all associated activators
     */
    default void shutdown() throws Exception {

    }

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
    TActivator getActivator(Properties id);

    /**
     *
     * @return all the activators associated with this provider
     */
    Collection<? extends TActivator> getActivators();

    /**
     * Performs an atomic update if possible to all activators to the associated state.
     * All the activators in the map can be assumed to have originated from this provider
     * @param newStates a Map containing the activator as the key and the new state that should be assigned to that activator
     * @return true if the assignment of the activators completed successfully
     */
    default boolean setStates(Map<TActivator, ActivatorState> newStates) throws Exception {
        boolean result = true;
        for (Map.Entry<TActivator, ActivatorState> e : newStates.entrySet()) {
             result &= setState(e.getKey(), e.getValue());
        }
        return result;
    }

    /**
     * Performs an update on a single activator to the given state.
     *
     * @param activator the activator that will be assigned the new state
     * @param newState new state that should be assigned to that activator
     * @return true if the assignment of the activators completed successfully
     */
    boolean setState(TActivator activator, ActivatorState newState) throws Exception;
}
