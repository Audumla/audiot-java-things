package net.audumla.astronomical;

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
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class SolarEventCalculatorTest extends BaseTestCase {

    private SolarEventCalculator calc;

    @Before
    public void setupCalculator() {
        super.setup(10, 1, 2008);
        calc = new SolarEventCalculator(location, "America/New_York");
    }

    @Test
    public void testComputeSunriseTime() {
        String localSunriseTime = "07:05";
        assertEquals(localSunriseTime, calc.computeSunriseTime(Zenith.CIVIL, eventDate));
    }

    @Test
    public void testComputeSunsetTime() {
        String localSunsetTime = "18:28";
        assertEquals(localSunsetTime, calc.computeSunsetTime(Zenith.CIVIL, eventDate));
    }

    @Test
    public void testGetLocalTimeAsCalendar() {
        Calendar localTime = calc.getLocalTimeAsCalendar(BigDecimal.valueOf(15.5D), Calendar.getInstance());
        assertEquals(15, localTime.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, localTime.get(Calendar.MINUTE));
    }

    @Test
    public void testGetLocalTimeAsCalendarForZero() {
        Calendar localTime = calc.getLocalTimeAsCalendar(BigDecimal.valueOf(0.0D), Calendar.getInstance());
        assertEquals(0, localTime.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, localTime.get(Calendar.MINUTE));
    }

    @Test
    public void testGetLocalTimeAsCalendarForNegative() {
        Calendar localTime = calc.getLocalTimeAsCalendar(BigDecimal.valueOf(-10.0D), Calendar.getInstance());
        assertEquals(14, localTime.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, localTime.get(Calendar.MINUTE));
    }
}
