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

/**
 * Defines the solar declination used in computing the sunrise/sunset.
 */
public class Zenith {
    /**
     * Astronomical sunrise/set is when the sun is 18 degrees below the horizon.
     */
    public static final Zenith ASTRONOMICAL = new Zenith(108);

    /**
     * Nautical sunrise/set is when the sun is 12 degrees below the horizon.
     */
    public static final Zenith NAUTICAL = new Zenith(102);

    /**
     * Civil sunrise/set (dawn/dusk) is when the sun is 6 degrees below the horizon.
     */
    public static final Zenith CIVIL = new Zenith(96);

    /**
     * Official sunrise/set is when the sun is 50' below the horizon.
     */
    public static final Zenith OFFICIAL = new Zenith(90.8333);

    private final double degrees;

    public Zenith(double degrees) {
        this.degrees = degrees;
    }

    public double degrees() {
        return degrees;
    }
}
