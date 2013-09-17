package net.audumla.spacetime.astrological;

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

import java.util.Calendar;

import net.audumla.spacetime.Geolocation;
import net.audumla.spacetime.Location;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for the SunriseSunsetCalculator class.
 */
public class SunriseSunsetCalculatorTest extends BaseTestCase {

    private SunriseSunsetCalculator calc;

    @Before
    public void setup() {
        // November 1, 2008
        super.setup(10, 1, 2008);
        calc = new SunriseSunsetCalculator(location, "America/New_York");
    }

    @Test
    public void testComputeAstronomicalSunrise() {
        assertTimeEquals("06:01", calc.getAstronomicalSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeAstronomicalSunset() {
        assertTimeEquals("19:32", calc.getAstronomicalSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeNauticalSunrise() {
        assertTimeEquals("06:33", calc.getNauticalSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeNauticalSunset() {
        assertTimeEquals("19:00", calc.getNauticalSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeCivilSunrise() {
        assertTimeEquals("07:05", calc.getCivilSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeCivilSunset() {
        assertTimeEquals("18:28", calc.getCivilSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeOfficialSunrise() {
        assertTimeEquals("07:33", calc.getOfficialSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeOfficialSunset() {
        assertTimeEquals("18:00", calc.getOfficialSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testSpecificDateLocationAndTimezone() {
        Geolocation loc = new Location(55.03, 82.91,0.0);
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(loc, "GMT");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 4, 7);

        String officialSunriseForDate = calculator.getOfficialSunriseForDate(calendar);
        assertEquals("22:35", officialSunriseForDate);

        Calendar officialSunriseCalendarForDate = calculator.getOfficialSunriseCalendarForDate(calendar);
        assertEquals(22, officialSunriseCalendarForDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(35, officialSunriseCalendarForDate.get(Calendar.MINUTE));
        assertEquals(6, officialSunriseCalendarForDate.get(Calendar.DAY_OF_MONTH));
    }
}
