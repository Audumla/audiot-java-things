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

import net.audumla.astronomical.Geolocation;

public class EquinoxesAndSolstices {

    protected static Sun sun = new Sun();

    public static double marchEquinox(int Year,double latitude) {
        //calculate the approximate date
        double JDE;
        if (Year <= 1000) {
            double Y = Year / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 1721139.29189 + 365242.13740 * Y + 0.06134 * Ysquared + 0.00111 * Ycubed - 0.00071 * Y4;
        } else {
            double Y = (Year - 2000) / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 2451623.80984 + 365242.37404 * Y + 0.05169 * Ysquared - 0.00411 * Ycubed - 0.00057 * Y4;
        }

        double Correction;
        do {
            double SunLongitude = sun.apparentEclipticLongitude(JDE);
            Correction = 58 * Math.sin(CoordinateTransformation.degreesToRadians(-SunLongitude));
            JDE += Correction;
        }
        while (Math.abs(Correction) > 0.00001); //Corresponds to an error of 0.86 of a second

        return JDE;
    }

    public static double juneSolstice(int Year,double latitude) {
        //calculate the approximate date
        double JDE;
        if (Year <= 1000) {
            double Y = Year / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 1721233.25401 + 365241.72562 * Y - 0.05323 * Ysquared + 0.00907 * Ycubed + 0.00025 * Y4;
        } else {
            double Y = (Year - 2000) / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 2451716.56767 + 365241.62603 * Y + 0.00325 * Ysquared + 0.00888 * Ycubed - 0.00030 * Y4;
        }

        double Correction;
        do {
            double SunLongitude = sun.apparentEclipticLongitude(JDE);
            Correction = 58 * Math.sin(CoordinateTransformation.degreesToRadians(90 - SunLongitude));
            JDE += Correction;
        }
        while (Math.abs(Correction) > 0.00001); //Corresponds to an error of 0.86 of a second

        return JDE;
    }

    public static double septemberEquinox(int Year,double latitude) {
        //calculate the approximate date
        double JDE;
        if (Year <= 1000) {
            double Y = Year / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 1721325.70455 + 365242.49558 * Y - 0.11677 * Ysquared - 0.00297 * Ycubed + 0.00074 * Y4;
        } else {
            double Y = (Year - 2000) / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 2451810.21715 + 365242.01767 * Y - 0.11575 * Ysquared + 0.00337 * Ycubed + 0.00078 * Y4;
        }

        double Correction;
        do {
            double SunLongitude = sun.apparentEclipticLongitude(JDE);
            Correction = 58 * Math.sin(CoordinateTransformation.degreesToRadians(180 - SunLongitude));
            JDE += Correction;
        }
        while (Math.abs(Correction) > 0.00001); //Corresponds to an error of 0.86 of a second

        return JDE;
    }

    public static double decemberSolstice(int Year,double latitude) {
        //calculate the approximate date
        double JDE;
        if (Year <= 1000) {
            double Y = Year / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 1721414.39987 + 365242.88257 * Y - 0.00769 * Ysquared - 0.00933 * Ycubed - 0.00006 * Y4;
        } else {
            double Y = (Year - 2000) / 1000.0;
            double Ysquared = Y * Y;
            double Ycubed = Ysquared * Y;
            double Y4 = Ycubed * Y;
            JDE = 2451900.05952 + 365242.74049 * Y - 0.06223 * Ysquared - 0.00823 * Ycubed + 0.00032 * Y4;
        }

        double Correction;
        do {
            double SunLongitude = sun.apparentEclipticLongitude(JDE);
            Correction = 58 * Math.sin(CoordinateTransformation.degreesToRadians(270 - SunLongitude));
            JDE += Correction;
        }
        while (Math.abs(Correction) > 0.00001); //Corresponds to an error of 0.86 of a second

        return JDE;
    }

    public static double summerSolstice(int Year,double latitude) {
        if (latitude >= 0) {
            return juneSolstice(Year,latitude);
        } else {
            return decemberSolstice(Year,latitude);
        }
    }

    public static double winterSolstice(int Year,double latitude) {
        if (latitude >= 0) {
            return decemberSolstice(Year,latitude);
        } else {
            return juneSolstice(Year,latitude);
        }
    }

    public static double autumnEquinox(int Year,double latitude) {
        if (latitude >= 0) {
            return septemberEquinox(Year,latitude);
        } else {
            return septemberEquinox(Year,latitude);
        }
    }

    public static double springEquinox(int Year,double latitude) {
        if (latitude >= 0) {
            return marchEquinox(Year,latitude);
        } else {
            return septemberEquinox(Year,latitude);
        }
    }

    public static double lengthOfSpring(int Year,double latitude) {
        if (latitude >= 0) {
            return juneSolstice(Year,latitude) - marchEquinox(Year,latitude);
        } else {
            return decemberSolstice(Year,latitude) - septemberEquinox(Year,latitude);
        }
    }

    public static double lengthOfSummer(int Year,double latitude) {
        if (latitude >= 0) {
            return septemberEquinox(Year,latitude) - juneSolstice(Year,latitude);
        } else {
            return marchEquinox(Year + 1,latitude) - decemberSolstice(Year,latitude);
        }
    }

    public static double lengthOfAutumn(int Year,double latitude) {
        if (latitude >= 0) {
            return decemberSolstice(Year,latitude) - septemberEquinox(Year,latitude);
        } else {
            return juneSolstice(Year,latitude) - marchEquinox(Year,latitude);
        }
    }

    public static double lengthOfWinter(int Year,double latitude) {
        if (latitude >= 0) {
            return marchEquinox(Year + 1,latitude) - decemberSolstice(Year,latitude);
        } else {
            return septemberEquinox(Year,latitude) - juneSolstice(Year,latitude);
        }
    }
}