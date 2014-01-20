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

import net.audumla.automate.event.*;

import java.util.Properties;

public interface Activator<TProvider extends ActivatorProvider, TEvent extends Event>  extends RollbackEventTarget<RollbackEvent<Activator>>, EventTarget<TEvent> {

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
     * Set the state of the activator to the given ActivatorState
     *
     * @param state the new state to be set
     * @return returns true if the activation was successful otherwise false is returned
     */

    boolean setCurrentState(ActivatorState state);

    /**
     * The current state of the activator. Upon initialization the state should be UNKNOWN until a successful call to either activate or deactivate has been made
     *
     * @return the current state of the activator.
     */
    ActivatorState getCurrentState();

    /**
     * It is up to implementation classes to determine the name that will applied to an activator
     *
     * @return the name of the activator
     */
    Properties getId();

    /**
     *
     * @return the provider that manages this activator. May return null if no provider exists for this activator
     */
    TProvider getProvider();

}
