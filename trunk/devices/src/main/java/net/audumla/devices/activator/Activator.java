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
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.devices.event.CommandEvent;
import net.audumla.devices.event.EventTarget;

public interface Activator extends EventTarget<CommandEvent<Activator>> {
    /**
     * The Activator interface models a device that can be switched on or off either independently or for a given amount of time.
     * When a time is specified a choice can be made to determine whether the calling thread will be blocked or asynchronously deactivated.
     * The intention of the activator is to have a mechanism that is independent of the underlying device and to ensure that
     * where an error has occurred the device will always be placed back into a deactivated state.
     * Activator implementations should attempt to deactivate the activator upon instantiation to ensure a know state is applied
     *
     * The lifecycle status for an activator is as follows
     *
     * 1. Instantiated
     * State:UNKNOWN
     *
     * 2. Activate
     * Success flow - State:ACTIVATING -&gt; [Device Successfully Activated] -&gt; State:ACTIVATED
     * Failure flow - State:ACTIVATING -&gt; [Device Unsuccessfully Activated] -&gt; [Call Deactivate (3)]
     *
     * 3. Deactivate
     * Success flow - State:DEACTIVATING -&gt; [Device Successfully Deactivated] -&gt; State:DEACTIVATED
     * Failure flow - State:DEACTIVATING -&gt; [Device Unsuccessfully Activated] -&gt; State:UNKNOWN
     *
     * 3. Activate with timeout
     * [Call Activate (2)] -&gt; [Wait for timeout] -&gt; [Call Deactivate (3)]
     *
     * Listeners can be attached to the activator. These will be called on each change of state and when failures occur
     * Listeners can also be passed as parameters to any of the activate or deactivate methods. This listener will only
     * be called back for events that occur on that specific calls state changes and no others.
     */

    public enum ActivateState {
        ACTIVATING, ACTIVATED, DEACTIVATING, DEACTIVATED, UNKNOWN
    }

    /**
     * Attempts to activate the Activator.
     *
     * @param listeners listeners to be notified of state changes and errors for this invocation
     * @return returns true if the activation was successful otherwise false is returned
     */
    boolean activate(ActivatorListener... listeners);

    /**
     * Attempts to activate the Activator.
     *
     * @param listeners listeners to be notified of state changes and errors for this invocation
     * @param block     if true then after a successful activation the calling thread is blocked for the amount of time specified in the seconds parameter followed by an attempt to deactivate the Activator
     * @param seconds   specified the amount of time to leave the Activator in an ACTIVATED state before automatically calling deactivate
     * @return returns true if the activation was successful otherwise false is returned
     */
//    boolean activate(long seconds, boolean block, ActivatorListener... listeners);

    /**
     * Attempts to activate the Activator.
     *
     * @param listeners listeners to be notified of state changes and errors for this invocation
     * @return returns true if the activation was successful otherwise false is returned
     */
    boolean deactivate(ActivatorListener... listeners);

    /**
     * Adds a list to the global listener list that will be notified of all state changes and errors applied to this activator
     *
     * @param listener the listener to add
     */
    void addListener(ActivatorListener listener);

    /**
     * Removes the given listener from this activator
     *
     * @param listener the listener to remove
     */
    void removeListener(ActivatorListener listener);

    /**
     * It is up to implementation classes to determine the name that will applied to an activator
     *
     * @return the name of the activator
     */
    String getName();

    /**
     * Set the name of the activator
     *
     * @param name the name of the activator
     */
    void setName(String name);

    /**
     * The current state of the activator. Upon initialization the state should be UNKNOWN until a successful call to either activate or deactivate has been made
     *
     * @return the current state of the activator.
     */
    ActivateState getCurrentState();


}
