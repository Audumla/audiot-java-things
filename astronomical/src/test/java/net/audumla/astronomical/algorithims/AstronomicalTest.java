package net.audumla.astronomical.algorithims;

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

import net.audumla.Time;
import net.audumla.astronomical.Geolocation;
import net.audumla.astronomical.Location;
import net.audumla.astronomical.OrbitingObject;
import net.audumla.astronomical.TransitDetails;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AstronomicalTest {
    private static final Logger logger = LoggerFactory.getLogger(AstronomicalTest.class);
    private TimeZone deftz;

    @Before
    public void setUp() throws Exception {
        deftz = TimeZone.getDefault();
    }

    @After
    public void tearDown() throws Exception {
        TimeZone.setDefault(deftz);
    }

    @Test
    public void testSunrise() throws Exception {

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Melbourne"));
        JulianDate CalcDate = new JulianDate(2009, 8, 8, true);
        double JD = CalcDate.julian();
        EllipticalObject ao = new Sun();
        EllipticalPlanetaryDetails aoDetails = Elliptical.calculate(JD - 1, ao);
        double Alpha1 = aoDetails.ApparentGeocentricRA;
        double Delta1 = aoDetails.ApparentGeocentricDeclination;
        aoDetails = Elliptical.calculate(JD, ao);
        double Alpha2 = aoDetails.ApparentGeocentricRA;
        double Delta2 = aoDetails.ApparentGeocentricDeclination;
        aoDetails = Elliptical.calculate(JD + 1, ao);
        double Alpha3 = aoDetails.ApparentGeocentricRA;
        double Delta3 = aoDetails.ApparentGeocentricDeclination;

        Location loc = new Location(-37.70461920, 145.1030275, 0.0);

        JulianTransitDetails RiseTransitSetTime = RiseTransitSet.calculate(JD, Alpha1, Delta1, Alpha2, Delta2, Alpha3, Delta3, loc.getLongitude(Geolocation.Direction.WEST), loc.getLatitude(Geolocation.Direction.NORTH), -6);

        java.util.Date rise = RiseTransitSetTime.getJulianRise().toDate();
        java.util.Date set = RiseTransitSetTime.getJulianSet().toDate();

        logger.debug("Melbourne - Julian");
        logger.debug("Date    : " + new JulianDate(JD,true).toDate());
        logger.debug("Julian  : " + JD);
        logger.debug("Sunrise : Algorithms: " + rise + " : " + rise.getTime());
        logger.debug("Sunset  : Algorithms: " + set + " : " + set.getTime());

        Assert.assertEquals(rise.getTime(),1249764251677l,1000);
        Assert.assertEquals(set.getTime(),1249718740677l,1000);


    }

    @Test
    public void testDateConversion() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Melbourne"));
        java.util.Date date = new java.util.Date();
        JulianDate cDate = new JulianDate(date);
        logger.debug("Algorithms: " + cDate.toDate() + " : " +cDate.toDate().getTime() );
        logger.debug("Algorithms: " + date + " : " + date.getTime());
        Assert.assertEquals(cDate.toDate().getTime(),date.getTime(),1100);

    }

    @Test
    public void testDateConversion2() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Melbourne"));
        java.util.Date date = new Date();
        date = DateUtils.setYears(date,2013);
        date = DateUtils.setMinutes(date, 0);
        date = DateUtils.setMonths(date, 0);
        date = DateUtils.setMilliseconds(date, 0);
        date = DateUtils.setSeconds(date, 0);
        date = DateUtils.setDays(date, 1);
        date = DateUtils.setHours(date, 0);
        JulianDate cDate = new JulianDate(date);
        logger.debug("Algorithms: " + cDate.toDate() + " : " +cDate.toDate().getTime() );
        logger.debug("Algorithms: " + date + " : " + date.getTime());
        Assert.assertEquals(cDate.toDate().getTime(),date.getTime(),1100);

    }

    @Test
    public void testWrapperMethodsMelbourne() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Melbourne"));
        OrbitingObject sun = OrbitingObject.Sun;
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(0);
        c.set(Calendar.YEAR, 2009);
        c.set(Calendar.MONTH, Calendar.AUGUST);
        c.set(Calendar.DAY_OF_MONTH, 8);

        Date date = c.getTime();
        Location location = new Location(-37.70461920, 145.1030275, 0.0);
        TransitDetails details = sun.getTransitDetails(date,location,Sun.CIVIL);
        logger.debug("Melbourne");
        logger.debug("Date    : " + date);
        logger.debug("Julian  : " + new JulianDate(date).julian());
        logger.debug("Sunrise : Algorithms: " + details.getRiseTime() + " : " + details.getRiseTime().getTime());
        logger.debug("Sunset  : Algorithms: " + details.getSetTime() + " : " + details.getSetTime().getTime());
        Assert.assertEquals(details.getRiseTime().getTime(), 1249677914422l, 1000);
        Assert.assertEquals(details.getSetTime().getTime(), 1249718740422l, 1000);
    }

    @Test
    public void testWrapperMethodsMexico() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Mexico_City"));
        OrbitingObject sun = new Sun();
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(0);
        c.set(Calendar.YEAR, 2009);
        c.set(Calendar.MONTH, Calendar.AUGUST);
        c.set(Calendar.DAY_OF_MONTH, 8);

        Date date = c.getTime();
        Location location = new Location();
        location.setLatitude(19.4328, Geolocation.Direction.NORTH);
        location.setLongitude(99.1333, Geolocation.Direction.WEST);
        TransitDetails details = sun.getTransitDetails(date,location,Sun.CIVIL);
        logger.debug("Mexico");
        logger.debug("Date    : " + date);
        logger.debug("Julian  : " + new JulianDate(date).julian());
        logger.debug("Sunrise : Algorithms: " + details.getRiseTime() + " : " + details.getRiseTime().getTime());
        logger.debug("Sunset  : Algorithms: " + details.getSetTime() + " : " + details.getSetTime().getTime());
        Assert.assertEquals(details.getRiseTime().getTime(), 1249732325341l, 1000);
        Assert.assertEquals(details.getSetTime().getTime(), 1249781509341l, 1000);
    }

    @Test
    public void testWrapperMethodsRome() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));
        OrbitingObject sun = new Sun();
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(0);
        c.set(Calendar.YEAR, 2009);
        c.set(Calendar.MONTH, Calendar.AUGUST);
        c.set(Calendar.DAY_OF_MONTH, 8);

        Date date = c.getTime();
        Location location = new Location();
        location.setLatitude(41.9000, Geolocation.Direction.NORTH);
        location.setLongitude(12.5000, Geolocation.Direction.EAST);
        TransitDetails details = sun.getTransitDetails(date,location,Sun.CIVIL);
        logger.debug("Rome");
        logger.debug("Date    : " + date);
        logger.debug("Julian  : " + new JulianDate(date).julian());
        logger.debug("Sunrise : Algorithms: " + details.getRiseTime() + " : " + details.getRiseTime().getTime());
        logger.debug("Sunset  : Algorithms: " + details.getSetTime() + " : " + details.getSetTime().getTime());
        Assert.assertEquals(details.getRiseTime().getTime(), 1249702797298l, 1000);
        Assert.assertEquals(details.getSetTime().getTime(), 1249757426299l, 1000);
    }
}
