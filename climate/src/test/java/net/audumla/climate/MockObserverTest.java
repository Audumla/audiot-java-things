package net.audumla.climate;
/**
 * User: audumla
 * Date: 7/08/13
 * Time: 11:33 AM
 */

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class MockObserverTest {
    private static final Logger logger = Logger.getLogger(MockObserverTest.class);


    @Test
    public void testMock() {
        Date now = new Date();
        now = DateUtils.setMonths(now,9);
        now = DateUtils.setDays(now, 1);

        ClimateObserver observer = new MockObserverClassDefinition();
        ClimateObserverCollectionHandler aggregateObserver = new ClimateObserverCollectionHandler(observer.getSource());
        aggregateObserver.addClimateObserverTop(observer);
        aggregateObserver.addClimateObserverTail(new DerivedClimateObserver(observer.getSource()));
        observer = aggregateObserver.buildClimateObserver();
        ClimateData data = observer.getClimateData(now);
        while (data != null && DateUtils.getFragmentInDays(now, Calendar.MONTH) < 29) {
//            logger.debug(data.getTime()+" - " +data.getEvapotranspiration());

            now = DateUtils.addDays(now, 1);
            data = observer.getClimateData(now);
        }
    }
}
