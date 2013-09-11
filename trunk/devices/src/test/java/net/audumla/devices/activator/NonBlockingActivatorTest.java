package net.audumla.devices.activator;

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
    public void testStateChange() {
        SucceedingActivator activator = new SucceedingActivator();
        assert activator.getCurrentState() == Activator.ActivateState.UNKNOWN;
        activator.deactivate();
        assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATED;
        activator.activate();
        assert activator.getCurrentState() == Activator.ActivateState.ACTIVATED;
    }

    @Test
    public void testStateChangeListener() {
        final SucceedingActivator activator = new SucceedingActivator();
        final Collection<Activator.ActivateState> states = new ArrayList<Activator.ActivateState>();

        final ActivatorListener listener = new ActivatorListener() {
            @Override
            public void onStateChange(ActivatorStateChangeEvent event) {
                states.add(event.getNewState());
                switch (event.getNewState()) {
                    case ACTIVATING: assert activator.getCurrentState() != Activator.ActivateState.ACTIVATED; break;
                    case DEACTIVATING: assert activator.getCurrentState() != Activator.ActivateState.DEACTIVATED; break;
                    case ACTIVATED: assert activator.getCurrentState() == Activator.ActivateState.ACTIVATING; break;
                    case DEACTIVATED: assert activator.getCurrentState() == Activator.ActivateState.DEACTIVATING; break;
                }
            }

            @Override
            public void onStateChangeFailure(ActivatorStateChangeEvent event, Exception ex, String message) {
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

}
