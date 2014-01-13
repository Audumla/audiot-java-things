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
public class NonBlockingActivatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testStateChange() throws Exception {
        ActivatorMock activator = new ActivatorMock(true, true);
        EventScheduler.getInstance().registerEventTarget(activator);
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        activator.setCurrentState(ActivatorState.DEACTIVATED);
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
        EventScheduler.getInstance().scheduleEvent(activator, new ActivatorToggleCommand(Duration.ofSeconds(2)));
//        assert activator.getCurrentState() == ActivatorState.ACTIVATED;
        synchronized (this) {
            try {
                this.wait(1000);
                assert activator.getCurrentState() == ActivatorState.ACTIVATED;
                this.wait(2100);
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
    }

    @Test
    public void testStateChangeListener() throws Exception {
        final ActivatorMock activator = new ActivatorMock(true, true);
        EventScheduler.getInstance().registerEventTarget(activator);
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
        EventScheduler.getInstance().scheduleEvent(activator, new ActivatorToggleCommand(Duration.ofSeconds(2), listener));
        synchronized (this) {
            try {
                this.wait(1000);
                assert states.contains(ActivatorState.ACTIVATED);
                assert activator.getCurrentState() == ActivatorState.ACTIVATED;
                assert states.size() == 1;
                this.wait(2100);
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
        assert states.contains(ActivatorState.DEACTIVATED);
        assert states.size() == 2;
    }

}
