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

public class CoordinateTransformation
{

    public static double degreesToRadians(double Degrees)
    {
        return Degrees * 0.017453292519943295769236907684886;
    }

    public static double radiansToDegrees(double Radians)
    {
        return Radians * 57.295779513082320876798154814105;
    }

    public static double radiansToHours(double Radians)
    {
        return Radians * 3.8197186342054880584532103209403;
    }

    public static double hoursToRadians(double Hours)
    {
        return Hours * 0.26179938779914943653855361527329;
    }

    public static double hoursToDegrees(double Hours)
    {
        return Hours * 15;
    }

    public static double degreesToHours(double Degrees)
    {
        return Degrees / 15;
    }

    public static double pI()
    {
        return 3.1415926535897932384626433832795;
    }

    public static double MapTo0To360Range(double Degrees)
    {
        double Value = Degrees;

        //map it to the range 0 - 360
        while (Value < 0)
        {
            Value += 360;
        }
        while (Value > 360)
        {
            Value -= 360;
        }

        return Value;
    }

    public static double MapTo0To24Range(double HourAngle)
    {
        double Value = HourAngle;

        //map it to the range 0 - 24
        while (Value < 0)
        {
            Value += 24;
        }
        while (Value > 24)
        {
            Value -= 24;
        }

        return Value;
    }
    public static Coordinate2D Equatorial2Ecliptic(double Alpha, double Delta, double Epsilon)
    {
        Alpha = hoursToRadians(Alpha);
        Delta = degreesToRadians(Delta);
        Epsilon = degreesToRadians(Epsilon);

        Coordinate2D Ecliptic = new Coordinate2D();
        Ecliptic.X = radiansToDegrees(Math.atan2(Math.sin(Alpha) * Math.cos(Epsilon) + Math.tan(Delta) * Math.sin(Epsilon), Math.cos(Alpha)));
        if (Ecliptic.X < 0)
        {
            Ecliptic.X += 360;
        }
        Ecliptic.Y = radiansToDegrees(Math.asin(Math.sin(Delta) * Math.cos(Epsilon) - Math.cos(Delta) * Math.sin(Epsilon) * Math.sin(Alpha)));

        return Ecliptic;
    }
    public static Coordinate2D Ecliptic2Equatorial(double Lambda, double Beta, double Epsilon)
    {
        Lambda = degreesToRadians(Lambda);
        Beta = degreesToRadians(Beta);
        Epsilon = degreesToRadians(Epsilon);

        Coordinate2D Equatorial = new Coordinate2D();
        Equatorial.X = radiansToHours(Math.atan2(Math.sin(Lambda) * Math.cos(Epsilon) - Math.tan(Beta) * Math.sin(Epsilon), Math.cos(Lambda)));
        if (Equatorial.X < 0)
        {
            Equatorial.X += 24;
        }
        Equatorial.Y = radiansToDegrees(Math.asin(Math.sin(Beta) * Math.cos(Epsilon) + Math.cos(Beta) * Math.sin(Epsilon) * Math.sin(Lambda)));

        return Equatorial;
    }
    public static Coordinate2D Equatorial2Horizontal(double LocalHourAngle, double Delta, double Latitude)
    {
        LocalHourAngle = hoursToRadians(LocalHourAngle);
        Delta = degreesToRadians(Delta);
        Latitude = degreesToRadians(Latitude);

        Coordinate2D Horizontal = new Coordinate2D();
        Horizontal.X = radiansToDegrees(Math.atan2(Math.sin(LocalHourAngle), Math.cos(LocalHourAngle) * Math.sin(Latitude) - Math.tan(Delta) * Math.cos(Latitude)));
        if (Horizontal.X < 0)
        {
            Horizontal.X += 360;
        }
        Horizontal.Y = radiansToDegrees(Math.asin(Math.sin(Latitude) * Math.sin(Delta) + Math.cos(Latitude) * Math.cos(Delta) * Math.cos(LocalHourAngle)));

        return Horizontal;
    }
    public static Coordinate2D Horizontal2Equatorial(double Azimuth, double Altitude, double Latitude)
    {
        //Convert from degress to radians
        Azimuth = degreesToRadians(Azimuth);
        Altitude = degreesToRadians(Altitude);
        Latitude = degreesToRadians(Latitude);

        Coordinate2D Equatorial = new Coordinate2D();
        Equatorial.X = radiansToHours(Math.atan2(Math.sin(Azimuth), Math.cos(Azimuth) * Math.sin(Latitude) + Math.tan(Altitude) * Math.cos(Latitude)));
        if (Equatorial.X < 0)
        {
            Equatorial.X += 24;
        }
        Equatorial.Y = radiansToDegrees(Math.asin(Math.sin(Latitude) * Math.sin(Altitude) - Math.cos(Latitude) * Math.cos(Altitude) * Math.cos(Azimuth)));

        return Equatorial;
    }
    public static Coordinate2D Equatorial2Galactic(double Alpha, double Delta)
    {
        Alpha = 192.25 - hoursToDegrees(Alpha);
        Alpha = degreesToRadians(Alpha);
        Delta = degreesToRadians(Delta);

        Coordinate2D Galactic = new Coordinate2D();
        Galactic.X = radiansToDegrees(Math.atan2(Math.sin(Alpha), Math.cos(Alpha) * Math.sin(degreesToRadians(27.4)) - Math.tan(Delta) * Math.cos(degreesToRadians(27.4))));
        Galactic.X = 303 - Galactic.X;
        if (Galactic.X >= 360)
        {
            Galactic.X -= 360;
        }
        Galactic.Y = radiansToDegrees(Math.asin(Math.sin(Delta) * Math.sin(degreesToRadians(27.4)) + Math.cos(Delta) * Math.cos(degreesToRadians(27.4)) * Math.cos(Alpha)));

        return Galactic;
    }
    public static Coordinate2D Galactic2Equatorial(double l, double b)
    {
        l -= 123;
        l = degreesToRadians(l);
        b = degreesToRadians(b);

        Coordinate2D Equatorial = new Coordinate2D();
        Equatorial.X = radiansToDegrees(Math.atan2(Math.sin(l), Math.cos(l) * Math.sin(degreesToRadians(27.4)) - Math.tan(b) * Math.cos(degreesToRadians(27.4))));
        Equatorial.X += 12.25;
        if (Equatorial.X < 0)
        {
            Equatorial.X += 360;
        }
        Equatorial.X = degreesToHours(Equatorial.X);
        Equatorial.Y = radiansToDegrees(Math.asin(Math.sin(b) * Math.sin(degreesToRadians(27.4)) + Math.cos(b) * Math.cos(degreesToRadians(27.4)) * Math.cos(l)));

        return Equatorial;
    }
    public static double dMSToDegrees(double Degrees, double Minutes, double Seconds) {
        return dMSToDegrees(Degrees, Minutes, Seconds, true);
    }

    public static double dMSToDegrees(double Degrees, double Minutes, double Seconds, boolean bPositive)
    {
        //validate our parameters
        if (!bPositive)
        {
            assert Degrees >= 0; //All parameters should be non negative if the "bPositive" parameter is false
            assert Minutes >= 0;
            assert Seconds >= 0;
        }

        if (bPositive)
        {
            return Degrees + Minutes / 60 + Seconds / 3600;
        }
        else
        {
            return -Degrees - Minutes / 60 - Seconds / 3600;
        }
    }
}