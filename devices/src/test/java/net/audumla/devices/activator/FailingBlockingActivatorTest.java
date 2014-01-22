package net.audumla.devices.activator;

import net.audumla.automate.event.ThreadLocalEventScheduler;
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

    public void failActivate(EventTargetActivator activator) throws Exception {
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalEventScheduler().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(new EnableActivatorCommand(), activator.getName()).begin();
//        assert target.states.contains(ActivatorState.ACTIVATED);
//        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 0;
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
    }

    public void failDelayActivate(EventTargetActivator activator) throws Exception {
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalEventScheduler().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(new ToggleActivatorCommand(Duration.ofSeconds(2)), activator.getName()).begin();
//        assert target.states.contains(ActivatorState.ACTIVATED);
//        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 0;
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
    }

    public void failDeactivate(EventTargetActivator activator) throws Exception {
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);

        new ThreadLocalEventScheduler().registerEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);


        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(new DisableActivatorCommand(), activator.getName()).begin();
//        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 0;
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
    }


}
