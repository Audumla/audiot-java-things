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

public class CAARiseTransitSet {
    public static double ConstraintM(double M) {
        // converted M C++ parameter reference to return value

        while (M > 1) {
            M -= 1;
        }
        while (M < 0) {
            M += 1;
        }
        return M;
    }

    public static double CalculateTransit(double Alpha2, double theta0, double Longitude) {
        //Calculate and ensure the M0 is in the range 0 to +1
        double M0 = (Alpha2 * 15 + Longitude - theta0) / 360;
        ConstraintM(M0);

        return M0;
    }

    public static double[]  CalculateRiseSet(double M0, double cosH0, CAARiseTransitSetDetails details) {
        double M1 = 0;
        double M2 = 0;

        if ((cosH0 > -1) && (cosH0 < 1)) {
            details.riseValid = true;
            details.setValid = true;
            details.transitAboveHorizon = true;

            double H0 = Math.acos(cosH0);
            H0 = CAACoordinateTransformation.RadiansToDegrees(H0);

            //Calculate and ensure the M1 and M2 is in the range 0 to +1
            M1 = M0 - H0 / 360;
            M2 = M0 + H0 / 360;

            M1 = ConstraintM(M1);
            M2 = ConstraintM(M2);
        } else if (cosH0 < 1) {
            details.transitAboveHorizon = true;
        }
        return new double[]{M1, M2};
    }

    public static double[] CorrectRAValuesForInterpolation(double[] alphas) {
        //Ensure the RA values are corrected for interpolation. Due to important Remark 2 by Meeus on Interopolation of RA values
        if ((alphas[1] - alphas[0]) > 12.0) {
            alphas[0] += 24;
        } else if ((alphas[1] - alphas[0]) < -12.0) {
            alphas[1] += 24;
        }
        if ((alphas[2] - alphas[1]) > 12.0) {
            alphas[1] += 24;
        } else if ((alphas[2] - alphas[1]) < -12.0) {
            alphas[2] += 24;
        }
        return alphas;
    }

    public static double CalculateRiseHelper(CAARiseTransitSetDetails details, double theta0, double deltaT, double Alpha1, double Delta1, double Alpha2, double Delta2, double Alpha3, double Delta3, double Longitude, double Latitude, double LatitudeRad, double h0, double M1) {
        // converted M1 C++ parameter reference to return value
        for (int i = 0; i < 2; i++) {
            //Calculate the details of rising
            if (details.riseValid) {
                double theta1 = theta0 + 360.985647 * M1;
                theta1 = CAACoordinateTransformation.MapTo0To360Range(theta1);

                double n = M1 + deltaT / 86400;

                double Alpha = CAAInterpolate.Interpolate(n, Alpha1, Alpha2, Alpha3);
                double Delta = CAAInterpolate.Interpolate(n, Delta1, Delta2, Delta3);

                double H = theta1 - Longitude - Alpha * 15;
                CAA2DCoordinate Horizontal = CAACoordinateTransformation.Equatorial2Horizontal(H / 15, Delta, Latitude);

                double DeltaM = (Horizontal.Y - h0) / (360 * Math.cos(CAACoordinateTransformation.DegreesToRadians(Delta)) * Math.cos(LatitudeRad) * Math.sin(CAACoordinateTransformation.DegreesToRadians(H)));
                M1 += DeltaM;
            }
        }
        return M1;
    }

    public static double CalculateSetHelper(CAARiseTransitSetDetails details, double theta0, double deltaT, double Alpha1, double Delta1, double Alpha2, double Delta2, double Alpha3, double Delta3, double Longitude, double Latitude, double LatitudeRad, double h0, double M2) {
        // converted M2 C++ parameter reference to return value
        for (int i = 0; i < 2; i++) {
            //Calculate the details of setting
            if (details.setValid) {
                double theta1 = theta0 + 360.985647 * M2;
                theta1 = CAACoordinateTransformation.MapTo0To360Range(theta1);

                double n = M2 + deltaT / 86400;

                double Alpha = CAAInterpolate.Interpolate(n, Alpha1, Alpha2, Alpha3);
                double Delta = CAAInterpolate.Interpolate(n, Delta1, Delta2, Delta3);

                double H = theta1 - Longitude - Alpha * 15;
                CAA2DCoordinate Horizontal = CAACoordinateTransformation.Equatorial2Horizontal(H / 15, Delta, Latitude);

                double DeltaM = (Horizontal.Y - h0) / (360 * Math.cos(CAACoordinateTransformation.DegreesToRadians(Delta)) * Math.cos(LatitudeRad) * Math.sin(CAACoordinateTransformation.DegreesToRadians(H)));
                M2 += DeltaM;
            }
        }
        return M2;
    }

    public static double CalculateTransitHelper(double theta0, double deltaT, double Alpha1, double Alpha2, double Alpha3, double Longitude, double M0) {
        // converted M0 C++ parameter reference to return value
        for (int i = 0; i < 2; i++) {
            //Calculate the details of transit
            double theta1 = theta0 + 360.985647 * M0;
            theta1 = CAACoordinateTransformation.MapTo0To360Range(theta1);

            double n = M0 + deltaT / 86400;

            double Alpha = CAAInterpolate.Interpolate(n, Alpha1, Alpha2, Alpha3);

            double H = theta1 - Longitude - Alpha * 15;
            H = CAACoordinateTransformation.MapTo0To360Range(H);
            if (H > 180) {
                H -= 360;
            }

            double DeltaM = -H / 360;
            M0 += DeltaM;
        }
        return M0;
    }

    public static CAARiseTransitSetDetails Calculate(double JD, double Alpha1, double Delta1, double Alpha2, double Delta2, double Alpha3, double Delta3, double Longitude, double Latitude, double h0) {
        //What will be the return value
        CAARiseTransitSetDetails details = new CAARiseTransitSetDetails(new CAADate(JD,true));
        details.riseValid = false;
        details.setValid = false;
        details.transitAboveHorizon = false;

        //Calculate the sidereal time
        double theta0 = CAASidereal.apparentGreenwichSiderealTime(JD);
        theta0 *= 15; //Express it as degrees

        //Calculate deltat
        double deltaT = CAADynamicalTime.DeltaT(JD);

        //Convert values to radians
        double Delta2Rad = CAACoordinateTransformation.DegreesToRadians(Delta2);
        double LatitudeRad = CAACoordinateTransformation.DegreesToRadians(Latitude);

        //Convert the standard latitude to radians
        double h0Rad = CAACoordinateTransformation.DegreesToRadians(h0);

        //Calculate cosH0
        double cosH0 = (Math.sin(h0Rad) - Math.sin(LatitudeRad) * Math.sin(Delta2Rad)) / (Math.cos(LatitudeRad) * Math.cos(Delta2Rad));

        //Calculate M0
        double M0 = CalculateTransit(Alpha2, theta0, Longitude);

        //Calculate M1 & M2
        double[] riseSet = CalculateRiseSet(M0, cosH0, details);
        double M1 = riseSet[0];
        double M2 = riseSet[1];

        //Ensure the RA values are corrected for interpolation. Due to important Remark 2 by Meeus on Interopolation of RA values
        double[] alphas = CorrectRAValuesForInterpolation(new double[]{Alpha1, Alpha2, Alpha3});
        Alpha1 = alphas[0];
        Alpha2 = alphas[1];
        Alpha3 = alphas[2];

        //Do the main work
        M0 = CalculateTransitHelper(theta0, deltaT, Alpha1, Alpha2, Alpha3, Longitude, M0);
        M1 = CalculateRiseHelper(details, theta0, deltaT, Alpha1, Delta1, Alpha2, Delta2, Alpha3, Delta3, Longitude, Latitude, LatitudeRad, h0, M1);
        M2 = CalculateSetHelper(details, theta0, deltaT, Alpha1, Delta1, Alpha2, Delta2, Alpha3, Delta3, Longitude, Latitude, LatitudeRad, h0, M2);

        details.rise = details.riseValid ? (M1 * 24) : 0.0;
        details.set = details.setValid ? (M2 * 24) : 0.0;
        details.transit = M0 * 24; //We always return the transit time even if it occurs below the horizon

        return details;
    }

    public static class CAARiseTransitSetDetails {
        //Member variables
        private final CAADate referenceTime;
        public boolean riseValid;
        public double rise;
        public boolean transitAboveHorizon;
        public double transit;
        public boolean setValid;
        public double set;

        //Constructors / Destructors
        public CAARiseTransitSetDetails(CAADate referenceTime) {
            this.riseValid = false;
            this.rise = 0;
            this.transitAboveHorizon = false;
            this.transit = 0;
            this.setValid = false;
            this.set = 0;
            this.referenceTime = referenceTime;
        }

        public CAADate getRise() {
            double rtsJD = (referenceTime.Julian() + ((set) / 24.00));
            return new CAADate(rtsJD, true);
        }

        public CAADate getSet() {
            double rtsJD = (referenceTime.Julian() + ((set) / 24.00));
            return new CAADate(rtsJD, true);
        }

        public boolean isRiseValid() {
            return riseValid;
        }

        public boolean isTransitAboveHorizon() {
            return transitAboveHorizon;
        }

        public double getTransit() {
            return transit;
        }

        public boolean isSetValid() {
            return setValid;
        }
    }
}