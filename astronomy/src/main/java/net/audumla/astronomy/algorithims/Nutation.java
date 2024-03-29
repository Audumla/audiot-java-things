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


public class Nutation {
    public static NutationCoefficient[] g_NutationCoefficients = {new NutationCoefficient(0, 0, 0, 0, 1, -171996, -174.2, 92025, 8.9), new NutationCoefficient(-2, 0, 0, 2, 2, -13187, -1.6, 5736, -3.1), new NutationCoefficient(0, 0, 0, 2, 2, -2274, -0.2, 977, -0.5), new NutationCoefficient(0, 0, 0, 0, 2, 2062, 0.2, -895, 0.5), new NutationCoefficient(0, 1, 0, 0, 0, 1426, -3.4, 54, -0.1), new NutationCoefficient(0, 0, 1, 0, 0, 712, 0.1, -7, 0), new NutationCoefficient(-2, 1, 0, 2, 2, -517, 1.2, 224, -0.6), new NutationCoefficient(0, 0, 0, 2, 1, -386, -0.4, 200, 0), new NutationCoefficient(0, 0, 1, 2, 2, -301, 0, 129, -0.1), new NutationCoefficient(-2, -1, 0, 2, 2, 217, -0.5, -95, 0.3), new NutationCoefficient(-2, 0, 1, 0, 0, -158, 0, 0, 0), new NutationCoefficient(-2, 0, 0, 2, 1, 129, 0.1, -70, 0), new NutationCoefficient(0, 0, -1, 2, 2, 123, 0, -53, 0), new NutationCoefficient(2, 0, 0, 0, 0, 63, 0, 0, 0), new NutationCoefficient(0, 0, 1, 0, 1, 63, 0.1, -33, 0), new NutationCoefficient(2, 0, -1, 2, 2, -59, 0, 26, 0), new NutationCoefficient(0, 0, -1, 0, 1, -58, -0.1, 32, 0), new NutationCoefficient(0, 0, 1, 2, 1, -51, 0, 27, 0), new NutationCoefficient(-2, 0, 2, 0, 0, 48, 0, 0, 0), new NutationCoefficient(0, 0, -2, 2, 1, 46, 0, -24, 0), new NutationCoefficient(2, 0, 0, 2, 2, -38, 0, 16, 0), new NutationCoefficient(0, 0, 2, 2, 2, -31, 0, 13, 0), new NutationCoefficient(0, 0, 2, 0, 0, 29, 0, 0, 0), new NutationCoefficient(-2, 0, 1, 2, 2, 29, 0, -12, 0), new NutationCoefficient(0, 0, 0, 2, 0, 26, 0, 0, 0), new NutationCoefficient(-2, 0, 0, 2, 0, -22, 0, 0, 0), new NutationCoefficient(0, 0, -1, 2, 1, 21, 0, -10, 0), new NutationCoefficient(0, 2, 0, 0, 0, 17, -0.1, 0, 0), new NutationCoefficient(2, 0, -1, 0, 1, 16, 0, -8, 0), new NutationCoefficient(-2, 2, 0, 2, 2, -16, 0.1, 7, 0), new NutationCoefficient(0, 1, 0, 0, 1, -15, 0, 9, 0), new NutationCoefficient(-2, 0, 1, 0, 1, -13, 0, 7, 0), new NutationCoefficient(0, -1, 0, 0, 1, -12, 0, 6, 0), new NutationCoefficient(0, 0, 2, -2, 0, 11, 0, 0, 0), new NutationCoefficient(2, 0, -1, 2, 1, -10, 0, 5, 0), new NutationCoefficient(2, 0, 1, 2, 2, -8, 0, 3, 0), new NutationCoefficient(0, 1, 0, 2, 2, 7, 0, -3, 0), new NutationCoefficient(-2, 1, 1, 0, 0, -7, 0, 0, 0), new NutationCoefficient(0, -1, 0, 2, 2, -7, 0, 3, 0), new NutationCoefficient(2, 0, 0, 2, 1, -7, 0, 3, 0), new NutationCoefficient(2, 0, 1, 0, 0, 6, 0, 0, 0), new NutationCoefficient(-2, 0, 2, 2, 2, 6, 0, -3, 0), new NutationCoefficient(-2, 0, 1, 2, 1, 6, 0, -3, 0), new NutationCoefficient(2, 0, -2, 0, 1, -6, 0, 3, 0), new NutationCoefficient(2, 0, 0, 0, 1, -6, 0, 3, 0), new NutationCoefficient(0, -1, 1, 0, 0, 5, 0, 0, 0), new NutationCoefficient(-2, -1, 0, 2, 1, -5, 0, 3, 0), new NutationCoefficient(-2, 0, 0, 0, 1, -5, 0, 3, 0), new NutationCoefficient(0, 0, 2, 2, 1, -5, 0, 3, 0), new NutationCoefficient(-2, 0, 2, 0, 1, 4, 0, 0, 0), new NutationCoefficient(-2, 1, 0, 2, 1, 4, 0, 0, 0), new NutationCoefficient(0, 0, 1, -2, 0, 4, 0, 0, 0), new NutationCoefficient(-1, 0, 1, 0, 0, -4, 0, 0, 0), new NutationCoefficient(-2, 1, 0, 0, 0, -4, 0, 0, 0), new NutationCoefficient(1, 0, 0, 0, 0, -4, 0, 0, 0), new NutationCoefficient(0, 0, 1, 2, 0, 3, 0, 0, 0), new NutationCoefficient(0, 0, -2, 2, 2, -3, 0, 0, 0), new NutationCoefficient(-1, -1, 1, 0, 0, -3, 0, 0, 0), new NutationCoefficient(0, 1, 1, 0, 0, -3, 0, 0, 0), new NutationCoefficient(0, -1, 1, 2, 2, -3, 0, 0, 0), new NutationCoefficient(2, -1, -1, 2, 2, -3, 0, 0, 0), new NutationCoefficient(0, 0, 3, 2, 2, -3, 0, 0, 0), new NutationCoefficient(2, -1, 0, 2, 2, -3, 0, 0, 0)};

    public static double nutationInLongitude(double JD) {
        double T = (JD - 2451545) / 36525;
        double Tsquared = T * T;
        double Tcubed = Tsquared * T;

        double D = 297.85036 + 445267.111480 * T - 0.0019142 * Tsquared + Tcubed / 189474;
        D = CoordinateTransformation.MapTo0To360Range(D);

        double M = 357.52772 + 35999.050340 * T - 0.0001603 * Tsquared - Tcubed / 300000;
        M = CoordinateTransformation.MapTo0To360Range(M);

        double Mprime = 134.96298 + 477198.867398 * T + 0.0086972 * Tsquared + Tcubed / 56250;
        Mprime = CoordinateTransformation.MapTo0To360Range(Mprime);

        double F = 93.27191 + 483202.017538 * T - 0.0036825 * Tsquared + Tcubed / 327270;
        F = CoordinateTransformation.MapTo0To360Range(F);

        double omega = 125.04452 - 1934.136261 * T + 0.0020708 * Tsquared + Tcubed / 450000;
        omega = CoordinateTransformation.MapTo0To360Range(omega);

        int nCoefficients = g_NutationCoefficients.length;
        double value = 0;
        for (int i = 0; i < nCoefficients; i++) {
            double argument = g_NutationCoefficients[i].D * D + g_NutationCoefficients[i].M * M + g_NutationCoefficients[i].Mprime * Mprime + g_NutationCoefficients[i].F * F + g_NutationCoefficients[i].omega * omega;
            double radargument = CoordinateTransformation.degreesToRadians(argument);
            value += (g_NutationCoefficients[i].sincoeff1 + g_NutationCoefficients[i].sincoeff2 * T) * Math.sin(radargument) * 0.0001;
        }

        return value;
    }

    public static double nutationInObliquity(double JD) {
        double T = (JD - 2451545) / 36525;
        double Tsquared = T * T;
        double Tcubed = Tsquared * T;

        double D = 297.85036 + 445267.111480 * T - 0.0019142 * Tsquared + Tcubed / 189474;
        D = CoordinateTransformation.MapTo0To360Range(D);

        double M = 357.52772 + 35999.050340 * T - 0.0001603 * Tsquared - Tcubed / 300000;
        M = CoordinateTransformation.MapTo0To360Range(M);

        double Mprime = 134.96298 + 477198.867398 * T + 0.0086972 * Tsquared + Tcubed / 56250;
        Mprime = CoordinateTransformation.MapTo0To360Range(Mprime);

        double F = 93.27191 + 483202.017538 * T - 0.0036825 * Tsquared + Tcubed / 327270;
        F = CoordinateTransformation.MapTo0To360Range(F);

        double omega = 125.04452 - 1934.136261 * T + 0.0020708 * Tsquared + Tcubed / 450000;
        omega = CoordinateTransformation.MapTo0To360Range(omega);

        int nCoefficients = g_NutationCoefficients.length;
        double value = 0;
        for (int i = 0; i < nCoefficients; i++) {
            double argument = g_NutationCoefficients[i].D * D + g_NutationCoefficients[i].M * M + g_NutationCoefficients[i].Mprime * Mprime + g_NutationCoefficients[i].F * F + g_NutationCoefficients[i].omega * omega;
            double radargument = CoordinateTransformation.degreesToRadians(argument);
            value += (g_NutationCoefficients[i].coscoeff1 + g_NutationCoefficients[i].coscoeff2 * T) * Math.cos(radargument) * 0.0001;
        }

        return value;
    }

    public static double meanObliquityOfEcliptic(double JD) {
        double U = (JD - 2451545) / 3652500;
        double Usquared = U * U;
        double Ucubed = Usquared * U;
        double U4 = Ucubed * U;
        double U5 = U4 * U;
        double U6 = U5 * U;
        double U7 = U6 * U;
        double U8 = U7 * U;
        double U9 = U8 * U;
        double U10 = U9 * U;


        return CoordinateTransformation.dMSToDegrees(23, 26, 21.448, true) - CoordinateTransformation.dMSToDegrees(0, 0, 4680.93, true) * U - CoordinateTransformation.dMSToDegrees(0, 0, 1.55, true) * Usquared + CoordinateTransformation.dMSToDegrees(0, 0, 1999.25, true) * Ucubed - CoordinateTransformation.dMSToDegrees(0, 0, 51.38, true) * U4 - CoordinateTransformation.dMSToDegrees(0, 0, 249.67, true) * U5 - CoordinateTransformation.dMSToDegrees(0, 0, 39.05, true) * U6 + CoordinateTransformation.dMSToDegrees(0, 0, 7.12, true) * U7 + CoordinateTransformation.dMSToDegrees(0, 0, 27.87, true) * U8 + CoordinateTransformation.dMSToDegrees(0, 0, 5.79, true) * U9 + CoordinateTransformation.dMSToDegrees(0, 0, 2.45, true) * U10;
    }

    public static double trueObliquityOfEcliptic(double JD) {
        return meanObliquityOfEcliptic(JD) + CoordinateTransformation.dMSToDegrees(0, 0, nutationInObliquity(JD), true);
    }

    public static double nutationInRightAscension(double Alpha, double Delta, double Obliquity, double NutationInLongitude, double NutationInObliquity) {
        //Convert to radians
        Alpha = CoordinateTransformation.hoursToRadians(Alpha);
        Delta = CoordinateTransformation.degreesToRadians(Delta);
        Obliquity = CoordinateTransformation.degreesToRadians(Obliquity);

        return (Math.cos(Obliquity) + Math.sin(Obliquity) * Math.sin(Alpha) * Math.tan(Delta)) * NutationInLongitude - Math.cos(Alpha) * Math.tan(Delta) * NutationInObliquity;
    }

    public static double nutationInDeclination(double Alpha, double Obliquity, double NutationInLongitude, double NutationInObliquity) {
        //Convert to radians
        Alpha = CoordinateTransformation.hoursToRadians(Alpha);
        Obliquity = CoordinateTransformation.degreesToRadians(Obliquity);

        return Math.sin(Obliquity) * Math.cos(Alpha) * NutationInLongitude + Math.sin(Alpha) * NutationInObliquity;
    }

    public static class NutationCoefficient {
        public int D;
        public int M;
        public int Mprime;
        public int F;
        public int omega;
        public int sincoeff1;
        public double sincoeff2;
        public int coscoeff1;
        public double coscoeff2;

        public NutationCoefficient(int d, int m, int mprime, int f, int omega, int sincoeff1, double sincoeff2, int coscoeff1, double coscoeff2) {
            D = d;
            M = m;
            Mprime = mprime;
            F = f;
            this.omega = omega;
            this.sincoeff1 = sincoeff1;
            this.sincoeff2 = sincoeff2;
            this.coscoeff1 = coscoeff1;
            this.coscoeff2 = coscoeff2;
        }

    }
}