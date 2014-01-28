package net.audumla.devices.activator;

import net.audumla.automate.event.ThreadPoolDispatcher;
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
        EventTargetActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeAllFailure(activator);
    }

    @Test
    public void testStateChangeAllFailureException() throws Exception {
        EventTargetActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeAllFailure(activator);
    }

    public void stateChangeAllFailure(EventTargetActivator activator) throws Exception {
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
        EventTargetActivator activator = new ActivatorMock(false, true);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeActivateFailure(activator);
    }

    @Test
    public void testStateChangeActivateFailureException() throws Exception {
        EventTargetActivator activator = new ActivatorMock(false, true);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeActivateFailure(activator);
    }

    public void stateChangeActivateFailure(EventTargetActivator activator) throws Exception {
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
        final EventTargetActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeListener(activator);
    }

    @Test
    public void testStateChangeListenerException() throws Exception {
        final EventTargetActivator activator = new ActivatorMock(false, false);
        new ThreadPoolDispatcher().registerEventTarget(activator);
        stateChangeListener(activator);
    }

    public void stateChangeListener(EventTargetActivator activator) throws Exception {
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
