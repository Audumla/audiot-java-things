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

import net.audumla.automate.event.ThreadLocalDispatcher;
import net.audumla.automate.event.activator.ToggleActivatorCommand;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockingActivatorTest {

    @Test
    public void testStateChange() throws Exception {
        ActivatorMock activator = new ActivatorMock(true, true);
        new ThreadLocalDispatcher().registerEventTarget(activator);
        assert activator.getState() == ActivatorState.UNKNOWN;
        activator.setState(ActivatorState.DEACTIVATED);
        assert activator.getState() == ActivatorState.DEACTIVATED;
        activator.setState(ActivatorState.ACTIVATED);
        assert activator.getState() == ActivatorState.ACTIVATED;
    }

    @Test
    public void testStateChangeListener() throws Exception {
        final ActivatorMock activator = new ActivatorMock(true, true);
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalDispatcher().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.setState(ActivatorState.DEACTIVATED);
        assert !target.states.contains(ActivatorState.ACTIVATED);
        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 1;
        activator.setState(ActivatorState.ACTIVATED);
        assert target.states.contains(ActivatorState.ACTIVATED);
        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 2;
    }

    @Test
    public void testDelayedStateChange() throws Exception {
        ActivatorMock activator = new ActivatorMock(true, true);
        new ThreadLocalDispatcher().registerEventTarget(activator);
        assert activator.getState() == ActivatorState.UNKNOWN;
        activator.setState(ActivatorState.DEACTIVATED);
        assert activator.getState() == ActivatorState.DEACTIVATED;
        Date start = new Date();
        new ToggleActivatorCommand(Duration.ofSeconds(3)).execute(activator);
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 3000, 100);
        assert activator.getState() == ActivatorState.DEACTIVATED;
    }

    @Test
    public void testDelayedStateChangeListener() throws Exception {
        final ActivatorMock activator = new ActivatorMock(true, true);
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalDispatcher().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        Date start = new Date();
        new ToggleActivatorCommand(Duration.ofSeconds(3)).execute(activator);
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 3000, 100);
        assert target.states.contains(ActivatorState.ACTIVATED);
        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 2;
        activator.setState(ActivatorState.ACTIVATED);
        assert target.states.size() == 3;
        activator.setState(ActivatorState.DEACTIVATED);
        assert target.states.size() == 4;
    }
}

