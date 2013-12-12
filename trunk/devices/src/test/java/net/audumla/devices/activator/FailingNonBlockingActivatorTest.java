package net.audumla.devices.activator;

import net.audumla.scheduler.quartz.QuartzScheduledExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

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
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
    }

    @Test
    public void testStateChangeAllFailure() throws Exception {
        Activator activator = new ActivatorMock(false, false);
        stateChangeAllFailure(activator);
    }

    @Test
    public void testStateChangeAllFailureException() throws Exception {
        Activator activator = new ExceptionActivatorMock(false, false);
        stateChangeAllFailure(activator);
    }

    public void stateChangeAllFailure(Activator activator) throws Exception {
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        new ActivatorToggleCommand(new QuartzScheduledExecutorService(),activator,1).call();
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        synchronized (this) {
            try {
                this.wait(1100);
                assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
    }

    @Test
    public void testStateChangeActivateFailure() throws Exception {
        Activator activator = new ActivatorMock(false, true);
        stateChangeActivateFailure(activator);
    }

    @Test
    public void testStateChangeActivateFailureException() throws Exception {
        Activator activator = new ExceptionActivatorMock(false, true);
        stateChangeActivateFailure(activator);
    }

    public void stateChangeActivateFailure(Activator activator) throws Exception {
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        new ActivatorToggleCommand(new QuartzScheduledExecutorService(),activator,1).call();
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
        synchronized (this) {
            try {
                this.wait(1100);
                assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
    }

    @Test
    public void testStateChangeListener() throws Exception {
        final Activator activator = new ActivatorMock(false, false);
        stateChangeListener(activator);
    }

    @Test
    public void testStateChangeListenerException() throws Exception {
        final Activator activator = new ExceptionActivatorMock(false, false);
        stateChangeListener(activator);
    }

    public void stateChangeListener(Activator activator) throws Exception {
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
                        break;
                    case DEACTIVATED:
                        break;
                }
            }
        };

        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        assert states.isEmpty();
        new ActivatorToggleCommand(new QuartzScheduledExecutorService(),activator,3,listener).call();
        assert states.contains(Activator.ActivateState.ACTIVATING);
        assert states.contains(Activator.ActivateState.ACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        assert states.size() == 4;
    }

}
