package devices.activator;

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

import net.audumla.automate.event.ThreadPoolDispatcher;
import net.audumla.automate.event.activator.ToggleActivatorCommand;
import org.junit.Test;

import java.time.Duration;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FailingNonBlockingActivatorTest {


    @Test
    public void testStateChangeAllFailure() throws Exception {
        DefaultActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeAllFailure(activator);
    }

    @Test
    public void testStateChangeAllFailureException() throws Exception {
        DefaultActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeAllFailure(activator);
    }

    public void stateChangeAllFailure(DefaultActivator activator) throws Exception {
        assert activator.getState() == ActivatorState.UNKNOWN;
        activator.getScheduler().publishEvent(activator.getName(), new ToggleActivatorCommand(Duration.ofSeconds(1))).begin();
//        assert activator.getState() == ActivatorState.UNKNOWN;
        synchronized (this) {
            try {
                this.wait(1100);
                assert activator.getState() == ActivatorState.UNKNOWN;
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getState() == ActivatorState.UNKNOWN;
    }

    @Test
    public void testStateChangeActivateFailure() throws Exception {
        DefaultActivator activator = new ActivatorMock(false, true);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeActivateFailure(activator);
    }

    @Test
    public void testStateChangeActivateFailureException() throws Exception {
        DefaultActivator activator = new ActivatorMock(false, true);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeActivateFailure(activator);
    }

    public void stateChangeActivateFailure(DefaultActivator activator) throws Exception {
        assert activator.getState() == ActivatorState.UNKNOWN;
        activator.getScheduler().publishEvent(activator.getName(), new ToggleActivatorCommand(Duration.ofSeconds(1))).begin();
//        assert activator.getState() == ActivatorState.DEACTIVATED;
        synchronized (this) {
            try {
                this.wait(500);
                assert activator.getState() == ActivatorState.DEACTIVATED;
                this.wait(1100);
                assert activator.getState() == ActivatorState.DEACTIVATED;
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getState() == ActivatorState.DEACTIVATED;
    }

    @Test
    public void testStateChangeListener() throws Exception {
        final DefaultActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeListener(activator);
    }

    @Test
    public void testStateChangeListenerException() throws Exception {
        final DefaultActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeListener(activator);
    }

    public void stateChangeListener(DefaultActivator activator) throws Exception {
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(activator.getName(), new ToggleActivatorCommand(Duration.ofSeconds(3))).begin();
        synchronized (this) {
            try {
                this.wait(4100);
            } catch (Throwable t) {

            }
        }
//        assert target.states.contains(ActivatorState.ACTIVATED);
//        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert activator.getState() == ActivatorState.UNKNOWN;
        assert target.states.size() == 0;
    }

}
