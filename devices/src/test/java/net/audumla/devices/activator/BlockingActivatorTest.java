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
public class BlockingActivatorTest {

    @Test
    public void testStateChange() {
        ActivatorMock activator = new ActivatorMock(true, true);
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        activator.setCurrentState(ActivatorState.DEACTIVATED );
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
        activator.setCurrentState(ActivatorState.ACTIVATED);
        assert activator.getCurrentState() == ActivatorState.ACTIVATED;
    }

    @Test
    public void testStateChangeListener() {
        final ActivatorMock activator = new ActivatorMock(true, true);
        final Collection<ActivatorState> states = new ArrayList<ActivatorState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                assert false;
            }
        };

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert states.isEmpty();
        activator.setCurrentState(ActivatorState.DEACTIVATED, listener);
        assert !states.contains(ActivatorState.ACTIVATED);
        assert states.contains(ActivatorState.DEACTIVATED);
        assert states.size() == 1;
        activator.setCurrentState(ActivatorState.ACTIVATED, listener);
        assert states.contains(ActivatorState.ACTIVATED);
        assert states.contains(ActivatorState.DEACTIVATED);
        assert states.size() == 2;
    }

    @Test
    public void testDelayedStateChange() throws Exception {
        ActivatorMock activator = new ActivatorMock(true, true);
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        activator.setCurrentState(ActivatorState.DEACTIVATED);
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
        Date start = new Date();
        new ToggleActivatorCommand(Duration.ofSeconds(3)).execute(activator);
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 3000, 100);
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
    }

    @Test
    public void testDelayedStateChangeListener() throws Exception {
        final ActivatorMock activator = new ActivatorMock(true, true);
        final Collection<ActivatorState> states = new ArrayList<ActivatorState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                assert false;
            }
        };

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert states.isEmpty();
        Date start = new Date();
        new ToggleActivatorCommand(Duration.ofSeconds(3), listener).execute(activator);
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 3000, 100);
        assert states.contains(ActivatorState.ACTIVATED);
        assert states.contains(ActivatorState.DEACTIVATED);
        assert states.size() == 2;
        activator.setCurrentState(ActivatorState.ACTIVATED, listener);
        assert states.size() == 3;
        activator.setCurrentState(ActivatorState.DEACTIVATED, listener);
        assert states.size() == 4;
    }
}

