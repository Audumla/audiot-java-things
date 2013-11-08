package net.audumla.astronomy;

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

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class SeasonTest {
    private static final Logger logger = LoggerFactory.getLogger(SeasonTest.class);

    @Test
    public void testWinterStart() throws Exception {
        SeasonalEvent event = new SeasonalEvent(SeasonalEvent.WINTER_START, Geolocation.newGeoLocation(30, 0, 0));
        Date et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.DECEMBER;

        event = new SeasonalEvent(SeasonalEvent.WINTER_START, Geolocation.newGeoLocation(-30, 0, 0));
        et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.JUNE;
    }

    @Test
    public void testSummerStart() throws Exception {
        SeasonalEvent event = new SeasonalEvent(SeasonalEvent.SUMMER_START, Geolocation.newGeoLocation(30, 0, 0));
        Date et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.JUNE;

        event = new SeasonalEvent(SeasonalEvent.SUMMER_START, Geolocation.newGeoLocation(-30, 0, 0));
        et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.DECEMBER;
    }

    @Test
    public void testAutumnStart() throws Exception {
        SeasonalEvent event = new SeasonalEvent(SeasonalEvent.AUTUMN_START, Geolocation.newGeoLocation(30, 0, 0));
        Date et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.SEPTEMBER;

        event = new SeasonalEvent(SeasonalEvent.AUTUMN_START, Geolocation.newGeoLocation(-30, 0, 0));
        et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.MARCH;
    }

    @Test
    public void testSpringStart() throws Exception {
        SeasonalEvent event = new SeasonalEvent(SeasonalEvent.SPRING_START, Geolocation.newGeoLocation(30, 0, 0));
        Date et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.MARCH;

        event = new SeasonalEvent(SeasonalEvent.SPRING_START, Geolocation.newGeoLocation(-30, 0, 0));
        et = event.calculateEventFrom(new Date());
        assert DateUtils.toCalendar(et).get(Calendar.MONTH) == Calendar.SEPTEMBER;
    }
}
