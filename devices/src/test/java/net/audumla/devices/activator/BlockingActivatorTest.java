package net.audumla.devices.activator;

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
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        activator.deactivate();
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
        activator.activate();
        assert activator.getCurrentState() == Activator.ActivateState.ACTIVATED;
    }

    @Test
    public void testStateChangeListener() {
        final ActivatorMock activator = new ActivatorMock(true, true);
        final Collection<Activator.ActivateState> states = new ArrayList<Activator.ActivateState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
                switch (event.getNewState()) {
                    case ACTIVATING:
                        assert activator.getCurrentState() != Activator.ActivateState.ACTIVATED;
                        break;
                    case DEACTIVATING:
                        assert activator.getCurrentState() != Activator.ActivateState.DEACTIVATED;
                        break;
                    case ACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.ACTIVATING;
                        break;
                    case DEACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATING;
                        break;
                }
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                assert false;
            }
        };

        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        assert states.isEmpty();
        activator.deactivate(listener);
        assert !states.contains(Activator.ActivateState.ACTIVATING);
        assert !states.contains(Activator.ActivateState.ACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.size() == 2;
        activator.activate(listener);
        assert states.contains(Activator.ActivateState.ACTIVATING);
        assert states.contains(Activator.ActivateState.ACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.size() == 4;
    }

    @Test
    public void testDelayedStateChange() throws Exception {
        ActivatorMock activator = new ActivatorMock(true, true);
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        activator.deactivate();
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
        Date start = new Date();
        new ActivatorToggleCommand(Duration.ofSeconds(3)).execute(activator);
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 3000, 100);
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
    }

    @Test
    public void testDelayedStateChangeListener() throws Exception {
        final ActivatorMock activator = new ActivatorMock(true, true);
        final Collection<Activator.ActivateState> states = new ArrayList<Activator.ActivateState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
                switch (event.getNewState()) {
                    case ACTIVATING:
                        assert activator.getCurrentState() != Activator.ActivateState.ACTIVATED;
                        break;
                    case DEACTIVATING:
                        assert activator.getCurrentState() != Activator.ActivateState.DEACTIVATED;
                        break;
                    case ACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.ACTIVATING;
                        break;
                    case DEACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATING;
                        break;
                }
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                assert false;
            }
        };

        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        assert states.isEmpty();
        Date start = new Date();
        new ActivatorToggleCommand(Duration.ofSeconds(3),listener).execute(activator);
        Date end = new Date();
        Assert.assertEquals((double) (end.getTime() - start.getTime()), 3000, 100);
        assert states.contains(Activator.ActivateState.ACTIVATING);
        assert states.contains(Activator.ActivateState.ACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.size() == 4;
        activator.activate(listener);
        assert states.size() == 6;
        activator.deactivate(listener);
        assert states.size() == 8;
    }
}

