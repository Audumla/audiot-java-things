package net.audumla.devices.activator;

import org.junit.Assert;
import org.junit.Test;

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
                        assert false;
                        break;
                    case DEACTIVATED:
                        assert false;
                        break;
                }
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                states.add(event.getNewState());
                switch (event.getNewState()) {
                    case ACTIVATING:
                        assert false;
                        break;
                    case DEACTIVATING:
                        assert false;
                        break;
                    case ACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.ACTIVATING;
                        break;
                    case DEACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATING;
                        break;
                }
            }
        };

        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        assert states.isEmpty();
        activator.activate(listener);
        assert states.contains(Activator.ActivateState.ACTIVATING);
        assert states.contains(Activator.ActivateState.ACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert states.size() == 4;
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
    }

    public void failDelayActivate(Activator activator) throws Exception {
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
                        assert activator.getCurrentState() != Activator.ActivateState.ACTIVATING;
                        break;
                    case ACTIVATED:
                        assert false;
                        break;
                    case DEACTIVATED:
                        assert false;
                        break;
                }
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                states.add(event.getNewState());
                switch (event.getNewState()) {
                    case ACTIVATING:
                        assert false;
                        break;
                    case DEACTIVATING:
                        assert false;
                        break;
                    case ACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.ACTIVATING;
                        break;
                    case DEACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATING;
                        break;
                }
            }
        };

        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        assert states.isEmpty();
        new ActivatorToggleCommand(null,activator,2,listener).call();
        assert states.contains(Activator.ActivateState.ACTIVATING);
        assert states.contains(Activator.ActivateState.ACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert states.size() == 4;
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
    }

    public void failDeactivate(Activator activator) {
        final Collection<Activator.ActivateState> states = new ArrayList<Activator.ActivateState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
                switch (event.getNewState()) {
                    case ACTIVATING:
                        assert false;
                        break;
                    case DEACTIVATING:
                        assert activator.getCurrentState() != Activator.ActivateState.DEACTIVATED;
                        break;
                    case ACTIVATED:
                        assert false;
                        break;
                    case DEACTIVATED:
                        assert false;
                        break;
                }
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                states.add(event.getNewState());
                switch (event.getNewState()) {
                    case DEACTIVATING:
                        assert false;
                        break;
                    case ACTIVATING:
                        assert false;
                        break;
                    case DEACTIVATED:
                        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATING;
                        break;
                    case ACTIVATED:
                        assert false;
                        break;
                }
            }
        };

        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        assert states.isEmpty();
        activator.deactivate(listener);
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.size() == 2;
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
    }


}
