package net.audumla.devices.activator;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 11/09/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockingActivatorTest {

    @Test
    public void testStateChange() throws Exception {
        ActivatorMock activator = new ActivatorMock(true, true);
        assert activator.getState() == ActivatorState.UNKNOWN;
        activator.setState(ActivatorState.DEACTIVATED);
        assert activator.getState() == ActivatorState.DEACTIVATED;
        activator.setState(ActivatorState.ACTIVATED);
        assert activator.getState() == ActivatorState.ACTIVATED;
    }

}

