package net.audumla.devices.activator;

import net.audumla.devices.activator.event.ToggleActivatorCommand;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
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
    public void testFailExceptionDeactivate() {
        failDeactivate(new ExceptionActivatorMock(false, false));
    }

    @Test
    public void testFailSimpleDeactivate() {
        failDeactivate(new ActivatorMock(false, false));
    }

    @Test
    public void testFailExceptionActivate() {
        failActivate(new ExceptionActivatorMock(false, false));
    }

    @Test
    public void testFailSimpleActivate() {
        failActivate(new ActivatorMock(false, false));
    }

    @Test
    public void testFailDelayedExceptionActivate() throws Exception {
        Date start = new Date();
        failDelayActivate(new ExceptionActivatorMock(false, false));
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

    public void failActivate(Activator activator) {
        final Collection<ActivatorState> states = new ArrayList<ActivatorState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
                assert false;
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                states.add(event.getNewState());
            }
        };

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert states.isEmpty();
        activator.setCurrentState(ActivatorState.ACTIVATED,listener);
        assert states.contains(ActivatorState.ACTIVATED);
        assert states.contains(ActivatorState.DEACTIVATED);
        assert states.size() == 2;
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
    }

    public void failDelayActivate(Activator activator) throws Exception {
        final Collection<ActivatorState> states = new ArrayList<ActivatorState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
                assert false;
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                states.add(event.getNewState());
            }
        };

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert states.isEmpty();
        new ToggleActivatorCommand(Duration.ofSeconds(2), listener).execute(activator);
        assert states.contains(ActivatorState.ACTIVATED);
        assert states.contains(ActivatorState.DEACTIVATED);
        assert states.size() == 2;
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
    }

    public void failDeactivate(Activator activator) {
        final Collection<ActivatorState> states = new ArrayList<ActivatorState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
                assert false;
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                states.add(event.getNewState());
            }
        };

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert states.isEmpty();
        activator.setCurrentState(ActivatorState.DEACTIVATED,listener);
        assert states.contains(ActivatorState.DEACTIVATED);
        assert states.size() == 1;
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
    }


}
