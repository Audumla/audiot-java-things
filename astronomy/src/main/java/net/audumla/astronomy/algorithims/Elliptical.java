package net.audumla.astronomy.algorithims;

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


public class Elliptical
{

    public static double distanceToLightTime(double Distance)
    {
        return Distance * 0.0057755183;
    }
    public static EllipticalPlanetaryDetails calculate(double JD, EllipticalObject object)
    {
        //What will the the return value
        EllipticalPlanetaryDetails details = new EllipticalPlanetaryDetails();

        double JD0 = JD;
        double L0 = 0;
        double B0 = 0;
        double R0 = 0;
        double cosB0 = 0;
        if (object instanceof Sun)
        {
            Earth earth = new Earth();
            L0 = earth.eclipticLongitude(JD0);
            B0 = earth.eclipticLatitude(JD0);
            R0 = earth.radiusVector(JD0);
            L0 = CoordinateTransformation.degreesToRadians(L0);
            B0 = CoordinateTransformation.degreesToRadians(B0);
            cosB0 = Math.cos(B0);
        }


        //Calculate the initial values
        double L = object.eclipticLongitude(JD0);
        double B = object.eclipticLatitude(JD0);
        double R = object.radiusVector(JD0);


        boolean bRecalc = true;
        boolean bFirstRecalc = true;
        double LPrevious = 0;
        double BPrevious = 0;
        double RPrevious = 0;
        while (bRecalc)
        {
            L = object.eclipticLongitude(JD0);
            B = object.eclipticLatitude(JD0);
            R = object.radiusVector(JD0);

            if (!bFirstRecalc)
            {
                bRecalc = ((Math.abs(L - LPrevious) > 0.00001) || (Math.abs(B - BPrevious) > 0.00001) || (Math.abs(R - RPrevious) > 0.000001));
                LPrevious = L;
                BPrevious = B;
                RPrevious = R;
            }
            else
            {
                bFirstRecalc = false;
            }

            //Calculate the new value
            if (bRecalc)
            {
                double distance;
                if (object instanceof Sun)
                {
                    double Lrad = CoordinateTransformation.degreesToRadians(L);
                    double Brad = CoordinateTransformation.degreesToRadians(B);
                    double cosB = Math.cos(Brad);
                    double cosL = Math.cos(Lrad);
                    double x = R * cosB * cosL - R0 * cosB0 * Math.cos(L0);
                    double y = R * cosB * Math.sin(Lrad) - R0 * cosB0 * Math.sin(L0);
                    double z = R * Math.sin(Brad) - R0 * Math.sin(B0);
                    distance = Math.sqrt(x * x + y * y + z * z);
                }
                else
                {
                    distance = R; //Distance to the sun from the earth is in fact the radius vector
                }

                //Prepare for the next loop around
                JD0 = JD - Elliptical.distanceToLightTime(distance);
            }
        }

        double Lrad = CoordinateTransformation.degreesToRadians(L);
        double Brad = CoordinateTransformation.degreesToRadians(B);
        double cosB = Math.cos(Brad);
        double cosL = Math.cos(Lrad);
        double x = R * cosB * cosL - R0 * cosB0 * Math.cos(L0);
        double y = R * cosB * Math.sin(Lrad) - R0 * cosB0 * Math.sin(L0);
        double z = R * Math.sin(Brad) - R0 * Math.sin(B0);
        double x2 = x * x;
        double y2 = y * y;

        details.ApparentGeocentricLatitude = CoordinateTransformation.radiansToDegrees(Math.atan2(z, Math.sqrt(x2 + y2)));
        details.ApparentGeocentricDistance = Math.sqrt(x2 + y2 + z * z);
        details.ApparentGeocentricLongitude = CoordinateTransformation.MapTo0To360Range(CoordinateTransformation.radiansToDegrees(Math.atan2(y, x)));
        details.ApparentLightTime = Elliptical.distanceToLightTime(details.ApparentGeocentricDistance);

        //Adjust for Aberration
        Coordinate2D Aberration = net.audumla.astronomy.algorithims.Aberration.eclipticAberration(details.ApparentGeocentricLongitude, details.ApparentGeocentricLatitude, JD);
        details.ApparentGeocentricLongitude += Aberration.X;
        details.ApparentGeocentricLatitude += Aberration.Y;

        //convert to the FK5 system
        double DeltaLong = FK5.correctionInLongitude(details.ApparentGeocentricLongitude, details.ApparentGeocentricLatitude, JD);
        details.ApparentGeocentricLatitude += FK5.correctionInLatitude(details.ApparentGeocentricLongitude, JD);
        details.ApparentGeocentricLongitude += DeltaLong;

        //Correct for nutation
        double NutationInLongitude = Nutation.nutationInLongitude(JD);
        double Epsilon = Nutation.trueObliquityOfEcliptic(JD);
        details.ApparentGeocentricLongitude += CoordinateTransformation.dMSToDegrees(0, 0, NutationInLongitude);

        //Convert to RA and Dec
        Coordinate2D ApparentEqu = CoordinateTransformation.Ecliptic2Equatorial(details.ApparentGeocentricLongitude, details.ApparentGeocentricLatitude, Epsilon);
        details.ApparentGeocentricRA = ApparentEqu.X;
        details.ApparentGeocentricDeclination = ApparentEqu.Y;

        return details;
    }
    public static double semiMajorAxisFromPerihelionDistance(double q, double e)
    {
        return q / (1 - e);
    }
    public static double meanMotionFromSemiMajorAxis(double a)
    {
        return 0.9856076686 / (a * Math.sqrt(a));
    }
    public static EllipticalObjectDetails calculate(double JD, EllipticalObjectElements elements)
    {
        double Epsilon = Nutation.meanObliquityOfEcliptic(elements.JDEquinox);

        double JD0 = JD;

        //What will be the return value
        EllipticalObjectDetails details = new EllipticalObjectDetails();

        Epsilon = CoordinateTransformation.degreesToRadians(Epsilon);
        double omega = CoordinateTransformation.degreesToRadians(elements.omega);
        double w = CoordinateTransformation.degreesToRadians(elements.w);
        double i = CoordinateTransformation.degreesToRadians(elements.i);

        double sinEpsilon = Math.sin(Epsilon);
        double cosEpsilon = Math.cos(Epsilon);
        double sinOmega = Math.sin(omega);
        double cosOmega = Math.cos(omega);
        double cosi = Math.cos(i);
        double sini = Math.sin(i);

        double F = cosOmega;
        double G = sinOmega * cosEpsilon;
        double H = sinOmega * sinEpsilon;
        double P = -sinOmega * cosi;
        double Q = cosOmega * cosi * cosEpsilon - sini * sinEpsilon;
        double R = cosOmega * cosi * sinEpsilon + sini * cosEpsilon;
        double a = Math.sqrt(F * F + P * P);
        double b = Math.sqrt(G * G + Q * Q);
        double c = Math.sqrt(H * H + R * R);
        double A = Math.atan2(F, P);
        double B = Math.atan2(G, Q);
        double C = Math.atan2(H, R);
        double n = Elliptical.meanMotionFromSemiMajorAxis(elements.a);

        Coordinate3D SunCoord = new Sun().equatorialRectangularCoordinatesAnyEquinox(JD, elements.JDEquinox);

        for (int j = 0; j < 2; j++)
        {
            double M = n * (JD0 - elements.T);
            double E = Kepler.calculate(M, elements.e);
            E = CoordinateTransformation.degreesToRadians(E);
            double v = 2 * Math.atan(Math.sqrt((1 + elements.e) / (1 - elements.e)) * Math.tan(E / 2));
            double r = elements.a * (1 - elements.e * Math.cos(E));
            double x = r * a * Math.sin(A + w + v);
            double y = r * b * Math.sin(B + w + v);
            double z = r * c * Math.sin(C + w + v);

            if (j == 0)
            {
                details.HeliocentricRectangularEquatorial.X = x;
                details.HeliocentricRectangularEquatorial.Y = y;
                details.HeliocentricRectangularEquatorial.Z = z;

                //Calculate the heliocentric ecliptic coordinates also
                double u = w + v;
                double cosu = Math.cos(u);
                double sinu = Math.sin(u);

                details.HeliocentricRectangularEcliptical.X = r * (cosOmega * cosu - sinOmega * sinu * cosi);
                details.HeliocentricRectangularEcliptical.Y = r * (sinOmega * cosu + cosOmega * sinu * cosi);
                details.HeliocentricRectangularEcliptical.Z = r * sini * sinu;

                details.HeliocentricEclipticLongitude = CoordinateTransformation.MapTo0To360Range(CoordinateTransformation.radiansToDegrees(Math.atan2(details.HeliocentricRectangularEcliptical.Y, details.HeliocentricRectangularEcliptical.X)));
                details.HeliocentricEclipticLatitude = CoordinateTransformation.radiansToDegrees(Math.asin(details.HeliocentricRectangularEcliptical.Z / r));
            }

            double psi = SunCoord.X + x;
            double nu = SunCoord.Y + y;
            double sigma = SunCoord.Z + z;

            double Alpha = Math.atan2(nu, psi);
            Alpha = CoordinateTransformation.radiansToDegrees(Alpha);
            double Delta = Math.atan2(sigma, Math.sqrt(psi * psi + nu * nu));
            Delta = CoordinateTransformation.radiansToDegrees(Delta);
            double Distance = Math.sqrt(psi * psi + nu * nu + sigma * sigma);

            if (j == 0)
            {
                details.TrueGeocentricRA = CoordinateTransformation.MapTo0To24Range(Alpha / 15);
                details.TrueGeocentricDeclination = Delta;
                details.TrueGeocentricDistance = Distance;
                details.TrueGeocentricLightTime = distanceToLightTime(Distance);
            }
            else
            {
                details.AstrometricGeocentricRA = CoordinateTransformation.MapTo0To24Range(Alpha / 15);
                details.AstrometricGeocentricDeclination = Delta;
                details.AstrometricGeocentricDistance = Distance;
                details.AstrometricGeocentricLightTime = distanceToLightTime(Distance);

                double RES = Math.sqrt(SunCoord.X * SunCoord.X + SunCoord.Y * SunCoord.Y + SunCoord.Z * SunCoord.Z);

                details.Elongation = Math.acos((RES * RES + Distance * Distance - r * r) / (2 * RES * Distance));
                details.Elongation = CoordinateTransformation.radiansToDegrees(details.Elongation);

                details.PhaseAngle = Math.acos((r * r + Distance * Distance - RES * RES) / (2 * r * Distance));
                details.PhaseAngle = CoordinateTransformation.radiansToDegrees(details.PhaseAngle);
            }

            if (j == 0) //Prepare for the next loop around
            {
                JD0 = JD - details.TrueGeocentricLightTime;
            }
        }

        return details;
    }
    public static double instantaneousVelocity(double r, double a)
    {
        return 42.1219 * Math.sqrt((1 / r) - (1 / (2 * a)));
    }
    public static double velocityAtPerihelion(double e, double a)
    {
        return 29.7847 / Math.sqrt(a) * Math.sqrt((1 + e) / (1 - e));
    }
    public static double velocityAtAphelion(double e, double a)
    {
        return 29.7847 / Math.sqrt(a) * Math.sqrt((1 - e) / (1 + e));
    }
    public static double lengthOfEllipse(double e, double a)
    {
        double b = a * Math.sqrt(1 - e * e);
        return CoordinateTransformation.pI() * (3 * (a + b) - Math.sqrt((a + 3 * b) * (3 * a + b)));
    }
    public static double cometMagnitude(double g, double delta, double k, double r)
    {
        return g + 5 * Math.log10(delta) + k * Math.log10(r);
    }
    public static double minorPlanetMagnitude(double H, double delta, double G, double r, double PhaseAngle)
    {
        //Convert from degrees to radians
        PhaseAngle = CoordinateTransformation.degreesToRadians(PhaseAngle);

        double phi1 = Math.exp(-3.33 * Math.pow(Math.tan(PhaseAngle / 2), 0.63));
        double phi2 = Math.exp(-1.87 * Math.pow(Math.tan(PhaseAngle / 2), 1.22));

        return H + 5 * Math.log10(r * delta) - 2.5 * Math.log10((1 - G) * phi1 + G * phi2);
    }
}