package net.audumla.climate;
/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * User: audumla
 * JulianDate: 7/08/13
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
        now = DateUtils.setMonths(now, 9);
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
