package net.audumla.devices.activator;

import net.audumla.automate.event.ThreadPoolEventScheduler;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

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
        new ThreadPoolEventScheduler().registerEventTarget(activator);
        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        activator.updateState(ActivatorState.DEACTIVATED);
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
        activator.getScheduler().publishEvent(activator.getName(), new ToggleActivatorCommand(Duration.ofSeconds(2))).begin();
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
        new ThreadPoolEventScheduler().registerEventTarget(activator);
        ActivatorStateChangeEventTarget target = new ActivatorStateChangeEventTarget(activator);
        activator.getScheduler().registerEventTarget(target);

        assert activator.getCurrentState() == ActivatorState.UNKNOWN;
        assert target.states.isEmpty();
        activator.getScheduler().publishEvent(activator.getName(), new ToggleActivatorCommand(Duration.ofSeconds(2))).begin();
        synchronized (this) {
            try {
                this.wait(1000);
                assert target.states.contains(ActivatorState.ACTIVATED);
                assert activator.getCurrentState() == ActivatorState.ACTIVATED;
                assert target.states.size() == 1;
                this.wait(2100);
            } catch (InterruptedException e) {
                assert false;
            }
        }
        assert activator.getCurrentState() == ActivatorState.DEACTIVATED;
        assert target.states.contains(ActivatorState.DEACTIVATED);
        assert target.states.size() == 2;
    }

}
