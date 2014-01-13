package net.audumla.devices.activator;

import net.audumla.devices.activator.event.ActivatorToggleCommand;
import net.audumla.devices.event.EventScheduler;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FailingNonBlockingActivatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testStateChangeAllFailure() throws Exception {
        Activator activator = new ActivatorMock(false, false);
        EventScheduler.getInstance().registerEventTarget(activator);
        stateChangeAllFailure(activator);
    }

    @Test
    public void testStateChangeAllFailureException() throws Exception {
        Activator activator = new ExceptionActivatorMock(false, false);
        EventScheduler.getInstance().registerEventTarget(activator);
        stateChangeAllFailure(activator);
    }

    public void stateChangeAllFailure(Activator activator) throws Exception {
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        EventScheduler.getInstance().scheduleEvent(activator, new ActivatorToggleCommand(Duration.ofSeconds(1)));
//        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        synchronized (this) {
            try {
                this.wait(1100);
                assert activator.getCurrentState() == ActivatorState.UNKNOWN;
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
    }

    @Test
    public void testStateChangeActivateFailure() throws Exception {
        Activator activator = new ActivatorMock(false, true);
        EventScheduler.getInstance().registerEventTarget(activator);
        stateChangeActivateFailure(activator);
    }

    @Test
    public void testStateChangeActivateFailureException() throws Exception {
        Activator activator = new ExceptionActivatorMock(false, true);
        EventScheduler.getInstance().registerEventTarget(activator);
        stateChangeActivateFailure(activator);
    }

    public void stateChangeActivateFailure(Activator activator) throws Exception {
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        EventScheduler.getInstance().scheduleEvent(activator, new ActivatorToggleCommand(Duration.ofSeconds(1)));
//        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
        synchronized (this) {
            try {
                this.wait(100);
                assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
                this.wait(1100);
                assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
    }

    @Test
    public void testStateChangeListener() throws Exception {
        final Activator activator = new ActivatorMock(false, false);
        EventScheduler.getInstance().registerEventTarget(activator);
        stateChangeListener(activator);
    }

    @Test
    public void testStateChangeListenerException() throws Exception {
        final Activator activator = new ExceptionActivatorMock(false, false);
        EventScheduler.getInstance().registerEventTarget(activator);
        stateChangeListener(activator);
    }

    public void stateChangeListener(Activator activator) throws Exception {
        final Collection<ActivatorState> states = new ArrayList<ActivatorState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Throwable ex, String message) {
                states.add(event.getNewState());
            }
        };

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert states.isEmpty();
        EventScheduler.getInstance().scheduleEvent(activator, new ActivatorToggleCommand(Duration.ofSeconds(3), listener));
        synchronized (this) {
            try {
                this.wait(3100);
            } catch (Throwable t) {

            }
        }
        assert states.contains(ActivatorState.ACTIVATED);
        assert states.contains(ActivatorState.DEACTIVATED);
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert states.size() == 2;
    }

}
