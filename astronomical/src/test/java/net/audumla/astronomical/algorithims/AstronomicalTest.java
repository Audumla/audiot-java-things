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

import net.audumla.astronomical.Location;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class AstronomicalTest {
    private static final Logger logger = LoggerFactory.getLogger(AstronomicalTest.class);

    @Test
    public void testSunrise() throws Exception {

        CAADate CalcDate = new CAADate(2009, 8, 8, true);
        double JD = CalcDate.Julian();
        CAAEllipticalPlanetaryDetails SunDetails = CAAElliptical.Calculate(JD - 1, CAAElliptical.EllipticalObject.SUN);
        double Alpha1 = SunDetails.ApparentGeocentricRA;
        double Delta1 = SunDetails.ApparentGeocentricDeclination;
        SunDetails = CAAElliptical.Calculate(JD, CAAElliptical.EllipticalObject.SUN);
        double Alpha2 = SunDetails.ApparentGeocentricRA;
        double Delta2 = SunDetails.ApparentGeocentricDeclination;
        SunDetails = CAAElliptical.Calculate(JD + 1, CAAElliptical.EllipticalObject.SUN);
        double Alpha3 = SunDetails.ApparentGeocentricRA;
        double Delta3 = SunDetails.ApparentGeocentricDeclination;


        Location loc = new Location(-37.70461920, -145.1030275, 0.0);

        CAARiseTransitSet.CAARiseTransitSetDetails RiseTransitSetTime = CAARiseTransitSet.Calculate(JD, Alpha1, Delta1, Alpha2, Delta2, Alpha3, Delta3, loc.getLongitude() , loc.getLatitude() , -6);

        Date set = RiseTransitSetTime.getSet().toDate();
        Date rise = RiseTransitSetTime.getRise().toDate();

        logger.debug("Sunrise : Algorithms: " + rise + " : " + rise.getTime());
        logger.debug("Sunset  : Algorithms: " + set + " : " + set.getTime());

        assert rise.getTime() == 1252442651737l;
        assert set.getTime() == 1252397140737l;


    }

    @Test
    public void testDateConversion() throws Exception {
        Date date = new Date();
        CAADate cDate = new CAADate(date);
        logger.debug("Algorithms: " + cDate.toDate() + " : " +cDate.toDate().getTime() );
        logger.debug("Algorithms: " + date + " : " + date.getTime());
        Assert.assertEquals(cDate.toDate().getTime(),date.getTime(),1100);

    }
}
