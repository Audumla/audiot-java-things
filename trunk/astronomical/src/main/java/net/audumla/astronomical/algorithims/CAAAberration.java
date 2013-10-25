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

public class CAAAberration {
    public static AberrationCoefficient[] g_AberrationCoefficients = {new AberrationCoefficient(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1719914, -2, -25, 0, 25, -13, 1578089, 156, 10, 32, 684185, -358), new AberrationCoefficient(0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6434, 141, 28007, -107, 25697, -95, -5904, -130, 11141, -48, -2559, -55), new AberrationCoefficient(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 715, 0, 0, 0, 6, 0, -657, 0, -15, 0, -282, 0), new AberrationCoefficient(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 715, 0, 0, 0, 0, 0, -656, 0, 0, 0, -285, 0), new AberrationCoefficient(0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 486, -5, -236, -4, -216, -4, -446, 5, -94, 0, -193, 0), new AberrationCoefficient(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 159, 0, 0, 0, 2, 0, -147, 0, -6, 0, -61, 0), new AberrationCoefficient(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 26, 0, 0, 0, -59, 0), new AberrationCoefficient(0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 39, 0, 0, 0, 0, 0, -36, 0, 0, 0, -16, 0), new AberrationCoefficient(0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 33, 0, -10, 0, -9, 0, -30, 0, -5, 0, -13, 0), new AberrationCoefficient(0, 2, 0, -1, 0, 0, 0, 0, 0, 0, 0, 31, 0, 1, 0, 1, 0, -28, 0, 0, 0, -12, 0), new AberrationCoefficient(0, 3, -8, 3, 0, 0, 0, 0, 0, 0, 0, 8, 0, -28, 0, 25, 0, 8, 0, 11, 0, 3, 0), new AberrationCoefficient(0, 5, -8, 3, 0, 0, 0, 0, 0, 0, 0, 8, 0, -28, 0, -25, 0, -8, 0, -11, 0, -3, 0), new AberrationCoefficient(2, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, -19, 0, 0, 0, -8, 0), new AberrationCoefficient(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -19, 0, 0, 0, 0, 0, 17, 0, 0, 0, 8, 0), new AberrationCoefficient(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 17, 0, 0, 0, 0, 0, -16, 0, 0, 0, -7, 0), new AberrationCoefficient(0, 1, 0, -2, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 15, 0, 1, 0, 7, 0), new AberrationCoefficient(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 16, 0, 0, 0, 1, 0, -15, 0, -3, 0, -6, 0), new AberrationCoefficient(0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 11, 0, -1, 0, -1, 0, -10, 0, -1, 0, -5, 0), new AberrationCoefficient(2, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -11, 0, -10, 0, 0, 0, -4, 0, 0, 0), new AberrationCoefficient(0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, -11, 0, -2, 0, -2, 0, 9, 0, -1, 0, 4, 0), new AberrationCoefficient(0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, -7, 0, -8, 0, -8, 0, 6, 0, -3, 0, 3, 0), new AberrationCoefficient(0, 3, 0, -2, 0, 0, 0, 0, 0, 0, 0, -10, 0, 0, 0, 0, 0, 9, 0, 0, 0, 4, 0), new AberrationCoefficient(1, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, -9, 0, 0, 0, 0, 0, -9, 0, 0, 0, -4, 0), new AberrationCoefficient(2, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, -9, 0, 0, 0, 0, 0, -8, 0, 0, 0, -4, 0), new AberrationCoefficient(0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, -9, 0, -8, 0, 0, 0, -3, 0, 0, 0), new AberrationCoefficient(2, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -9, 0, 8, 0, 0, 0, 3, 0, 0, 0), new AberrationCoefficient(0, 3, -2, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, -8, 0, 0, 0, -3, 0), new AberrationCoefficient(0, 0, 0, 0, 0, 0, 0, 1, 2, -1, 0, 8, 0, 0, 0, 0, 0, -7, 0, 0, 0, -3, 0), new AberrationCoefficient(8, -12, 0, 0, 0, 0, 0, 0, 0, 0, 0, -4, 0, -7, 0, -6, 0, 4, 0, -3, 0, 2, 0), new AberrationCoefficient(8, -14, 0, 0, 0, 0, 0, 0, 0, 0, 0, -4, 0, -7, 0, 6, 0, -4, 0, 3, 0, -2, 0), new AberrationCoefficient(0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, -6, 0, -5, 0, -4, 0, 5, 0, -2, 0, 2, 0), new AberrationCoefficient(3, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, 0, -2, 0, -7, 0, 1, 0, -4, 0), new AberrationCoefficient(0, 2, 0, -2, 0, 0, 0, 0, 0, 0, 0, 4, 0, -6, 0, -5, 0, -4, 0, -2, 0, -2, 0), new AberrationCoefficient(3, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -7, 0, -6, 0, 0, 0, -3, 0, 0, 0), new AberrationCoefficient(0, 2, -2, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, -5, 0, -4, 0, -5, 0, -2, 0, -2, 0), new AberrationCoefficient(0, 0, 0, 0, 0, 0, 0, 1, -2, 0, 0, 5, 0, 0, 0, 0, 0, -5, 0, 0, 0, -2, 0)};

    public static CAA3DCoordinate EarthVelocity(double JD) {
        double T = (JD - 2451545) / 36525;
        double L2 = 3.1761467 + 1021.3285546 * T;
        double L3 = 1.7534703 + 628.3075849 * T;
        double L4 = 6.2034809 + 334.0612431 * T;
        double L5 = 0.5995465 + 52.9690965 * T;
        double L6 = 0.8740168 + 21.3299095 * T;
        double L7 = 5.4812939 + 7.4781599 * T;
        double L8 = 5.3118863 + 3.8133036 * T;
        double Ldash = 3.8103444 + 8399.6847337 * T;
        double D = 5.1984667 + 7771.3771486 * T;
        double Mdash = 2.3555559 + 8328.6914289 * T;
        double F = 1.6279052 + 8433.4661601 * T;

        CAA3DCoordinate velocity = new CAA3DCoordinate();

        int nAberrationCoefficients = g_AberrationCoefficients.length;
        for (AberrationCoefficient g_AberrationCoefficient : g_AberrationCoefficients) {
            double Argument = g_AberrationCoefficient.L2 * L2 + g_AberrationCoefficient.L3 * L3 + g_AberrationCoefficient.L4 * L4 + g_AberrationCoefficient.L5 * L5 + g_AberrationCoefficient.L6 * L6 + g_AberrationCoefficient.L7 * L7 + g_AberrationCoefficient.L8 * L8 + g_AberrationCoefficient.Ldash * Ldash + g_AberrationCoefficient.D * D + g_AberrationCoefficient.Mdash * Mdash + g_AberrationCoefficient.F * F;
            velocity.X += (g_AberrationCoefficient.xsin + g_AberrationCoefficient.xsint * T) * Math.sin(Argument);
            velocity.X += (g_AberrationCoefficient.xcos + g_AberrationCoefficient.xcost * T) * Math.cos(Argument);

            velocity.Y += (g_AberrationCoefficient.ysin + g_AberrationCoefficient.ysint * T) * Math.sin(Argument);
            velocity.Y += (g_AberrationCoefficient.ycos + g_AberrationCoefficient.ycost * T) * Math.cos(Argument);

            velocity.Z += (g_AberrationCoefficient.zsin + g_AberrationCoefficient.zsint * T) * Math.sin(Argument);
            velocity.Z += (g_AberrationCoefficient.zcos + g_AberrationCoefficient.zcost * T) * Math.cos(Argument);
        }

        return velocity;
    }

    public static CAA2DCoordinate EquatorialAberration(double Alpha, double Delta, double JD) {
        //Convert to radians
        Alpha = CAACoordinateTransformation.DegreesToRadians(Alpha * 15);
        Delta = CAACoordinateTransformation.DegreesToRadians(Delta);

        double cosAlpha = Math.cos(Alpha);
        double sinAlpha = Math.sin(Alpha);
        double cosDelta = Math.cos(Delta);
        double sinDelta = Math.sin(Delta);

        CAA3DCoordinate velocity = EarthVelocity(JD);

        //What is the return value
        CAA2DCoordinate aberration = new CAA2DCoordinate();

        aberration.X = CAACoordinateTransformation.RadiansToHours((velocity.Y * cosAlpha - velocity.X * sinAlpha) / (17314463350.0 * cosDelta));
        aberration.Y = CAACoordinateTransformation.RadiansToDegrees(-(((velocity.X * cosAlpha + velocity.Y * sinAlpha) * sinDelta - velocity.Z * cosDelta) / 17314463350.0));

        return aberration;
    }

    public static CAA2DCoordinate EclipticAberration(double Lambda, double Beta, double JD) {
        //What is the return value
        CAA2DCoordinate aberration = new CAA2DCoordinate();

        double T = (JD - 2451545) / 36525;
        double Tsquared = T * T;
        double e = 0.016708634 - 0.000042037 * T - 0.0000001267 * Tsquared;
        double pi = 102.93735 + 1.71946 * T + 0.00046 * Tsquared;
        double k = 20.49552;
        double SunLongitude = CAASun.GeometricEclipticLongitude(JD);

        //Convert to radians
        pi = CAACoordinateTransformation.DegreesToRadians(pi);
        Lambda = CAACoordinateTransformation.DegreesToRadians(Lambda);
        Beta = CAACoordinateTransformation.DegreesToRadians(Beta);
        SunLongitude = CAACoordinateTransformation.DegreesToRadians(SunLongitude);

        aberration.X = (-k * Math.cos(SunLongitude - Lambda) + e * k * Math.cos(pi - Lambda)) / Math.cos(Beta) / 3600;
        aberration.Y = -k * Math.sin(Beta) * (Math.sin(SunLongitude - Lambda) - e * Math.sin(pi - Lambda)) / 3600;

        return aberration;
    }

    public static class AberrationCoefficient {
        public AberrationCoefficient(int l2, int l3, int l4, int l5, int l6, int l7, int l8, int ldash, int d, int mdash, int f, int xsin, int xsint, int xcos, int xcost, int ysin, int ysint, int ycos, int ycost, int zsin, int zsint, int zcos, int zcost) {
            L2 = l2;
            L3 = l3;
            L4 = l4;
            L5 = l5;
            L6 = l6;
            L7 = l7;
            L8 = l8;
            Ldash = ldash;
            D = d;
            Mdash = mdash;
            F = f;
            this.xsin = xsin;
            this.xsint = xsint;
            this.xcos = xcos;
            this.xcost = xcost;
            this.ysin = ysin;
            this.ysint = ysint;
            this.ycos = ycos;
            this.ycost = ycost;
            this.zsin = zsin;
            this.zsint = zsint;
            this.zcos = zcos;
            this.zcost = zcost;
        }

        public int L2;
        public int L3;
        public int L4;
        public int L5;
        public int L6;
        public int L7;
        public int L8;
        public int Ldash;
        public int D;
        public int Mdash;
        public int F;
        public int xsin;
        public int xsint;
        public int xcos;
        public int xcost;
        public int ysin;
        public int ysint;
        public int ycos;
        public int ycost;
        public int zsin;
        public int zsint;
        public int zcos;
        public int zcost;
    }
}