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

import java.util.Date;

/**
 * Interface description
 *
 * @author         Marius Gleeson
 */
public interface CelestialObject {
    public static final CelestialObject SUN = new net.audumla.astronomy.algorithims.Sun();
    public static final CelestialObject EARTH = new net.audumla.astronomy.algorithims.Earth();

    public enum Inclination {
        /**
         * Astronomical sunrise/set is when the sun is 18 degrees below the horizon.
         */
        ASTRONOMICAL (-18),
        /**
         * Nautical sunrise/set is when the sun is 12 degrees below the horizon.
         */
        NAUTICAL (-12),
        /**
         * Civil sunrise/set (dawn/dusk) is when the sun is 6 degrees below the horizon.
         */
        CIVIL (-6),
        /**
         * Official sunrise/set is when the sun is 50' below the horizon.
         */
        OFFICIAL (0.83333);

        private final double angle;

        Inclination(double angle) {
            this.angle = angle;
        }

        public double getAngle() {
            return angle;
        }
    }

    /**
     *
     *
     * @param date      The Day that will be used to base the transit time on.
     * @param location  The location on the EARTH to measure the transit of the object
     * @param altitude  The "standard" altitude in degrees i.e. the geometric altitude of the centre of the body at the time of the apparent rising or setting. For stars and planets, you would normally use -0.5667, for the SUN you would use -0.8333 and for the moon you would use 0.7275 * PI - 0.5666 where PI is the Moon's horizontal parallax in degrees (If no great accuracy is required, the mean value of h0 = 0.125 can be used).
     *
     * @return Returns details about the objects transit for a given day
     */
    TransitDetails getTransitDetails(Date date, Geolocation location, double altitude);
}



