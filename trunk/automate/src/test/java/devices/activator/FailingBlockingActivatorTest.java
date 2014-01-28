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
import net.audumla.automate.event.activator.DisableActivatorCommand;
import net.audumla.automate.event.activator.EnableActivatorCommand;
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
public class FailingBlockingActivatorTest {


    @Test
    public void testFailExceptionDeactivate() throws Exception {
        failDeactivate(new ActivatorMock(false, false));
    }

    @Test
    public void testFailSimpleDeactivate() throws Exception {
        failDeactivate(new ActivatorMock(false, false));
    }

    @Test
    public void testFailExceptionActivate() throws Exception {
        failActivate(new ActivatorMock(false, false));
    }

    @Test
    public void testFailSimpleActivate() throws Exception {
        failActivate(new ActivatorMock(false, false));
    }

    @Test
    public void testFailDelayedExceptionActivate() throws Exception {
        Date start = new Date();
        failDelayActivate(new ActivatorMock(false, false));
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 0, 50);
    }

    @Test
    public void testFailDelayedSimpleActivate() throws Exception {
        Date start = new Date();
        failDelayActivate(new ActivatorMock(false, false));
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 0, 50);
    }

    public void failActivate(DefaultActivator activator) throws Exception {
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalDispatcher().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(new EnableActivatorCommand(), activator.getName()).begin();
//        assert target.states.contains(ActivatorState.ACTIVATED);
//        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 0;
        assert activator.getState() == ActivatorState.UNKNOWN;
    }

    public void failDelayActivate(DefaultActivator activator) throws Exception {
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalDispatcher().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(new ToggleActivatorCommand(Duration.ofSeconds(2)), activator.getName()).begin();
//        assert target.states.contains(ActivatorState.ACTIVATED);
//        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 0;
        assert activator.getState() == ActivatorState.UNKNOWN;
    }

    public void failDeactivate(DefaultActivator activator) throws Exception {
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalDispatcher().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);


        assert activator.getState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(new DisableActivatorCommand(), activator.getName()).begin();
//        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 0;
        assert activator.getState() == ActivatorState.UNKNOWN;
    }


}
