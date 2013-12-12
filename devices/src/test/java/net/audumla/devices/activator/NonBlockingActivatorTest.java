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
public class NonBlockingActivatorTest {

    @Before
    public void setUp() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
    }

    @Test
    public void testStateChange() throws Exception {
        ActivatorMock activator = new ActivatorMock(true, true);
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        activator.deactivate();
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
        new ActivatorToggleCommand(new QuartzScheduledExecutorService(),activator,2).call();
        assert activator.getCurrentState() == Activator.ActivateState.ACTIVATED;
        synchronized (this) {
            try {
                this.wait(1000);
                assert activator.getCurrentState() == Activator.ActivateState.ACTIVATED;
                this.wait(2100);
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
    }

    @Test
    public void testStateChangeListener() throws Exception {
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
        new ActivatorToggleCommand(new QuartzScheduledExecutorService(),activator,2,listener).call();
        synchronized (this) {
            try {
                this.wait(1000);
                assert states.contains(Activator.ActivateState.ACTIVATING);
                assert states.contains(Activator.ActivateState.ACTIVATED);
                assert activator.getCurrentState() == Activator.ActivateState.ACTIVATED;
                assert states.size() == 2;
                this.wait(2100);
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
        assert states.contains(Activator.ActivateState.DEACTIVATING);
        assert states.contains(Activator.ActivateState.DEACTIVATED);
        assert states.size() == 4;
    }

}
