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

/**
 * Class description
 *
 * @author Marius Gleeson
 */
public class RiseTransitSet {

    /**
     * Method description
     *
     * @param M
     * @return
     */
    public static double constraintM(double M) {

        // converted M C++ parameter reference to return value
        while (M > 1) {
            M -= 1;
        }

        while (M < 0) {
            M += 1;
        }

        return M;
    }

    /**
     * Method description
     *
     * @param Alpha2
     * @param theta0
     * @param Longitude
     * @return
     */
    public static double calculateTransit(double Alpha2, double theta0, double Longitude) {

        // Calculate and ensure the M0 is in the range 0 to +1
        double M0 = (Alpha2 * 15 + Longitude - theta0) / 360;

        constraintM(M0);

        return M0;
    }

    /**
     * Method description
     *
     * @param M0
     * @param cosH0
     * @param details
     * @return
     */
    public static double[] calculateRiseSet(double M0, double cosH0, JulianTransitDetails details) {
        double M1 = 0;
        double M2 = 0;

        if ((cosH0 > -1) && (cosH0 < 1)) {
            details.setRiseValid(true);
            details.setSetValid(true);
            details.setTransitAboveHorizon(true);

            double H0 = Math.acos(cosH0);

            H0 = CoordinateTransformation.radiansToDegrees(H0);

            // Calculate and ensure the M1 and M2 is in the range 0 to +1
            M1 = M0 - H0 / 360;
            M2 = M0 + H0 / 360;
            M1 = constraintM(M1);
            M2 = constraintM(M2);
        } else if (cosH0 < 1) {
            details.setTransitAboveHorizon(true);
        }

        return new double[]{M1, M2};
    }

    /**
     * Method description
     *
     * @param alphas
     * @return
     */
    public static double[] correctRAValuesForInterpolation(double[] alphas) {

        // Ensure the RA values are corrected for interpolation. Due to important Remark 2 by Meeus on Interopolation of RA values
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

    /**
     * Method description
     *
     * @param details
     * @param theta0
     * @param deltaT
     * @param Alpha1
     * @param Delta1
     * @param Alpha2
     * @param Delta2
     * @param Alpha3
     * @param Delta3
     * @param Longitude
     * @param Latitude
     * @param LatitudeRad
     * @param h0
     * @param M1
     * @return
     */
    public static double calculateRiseHelper(JulianTransitDetails details, double theta0, double deltaT, double Alpha1, double Delta1,
                                             double Alpha2, double Delta2, double Alpha3, double Delta3, double Longitude, double Latitude, double LatitudeRad, double h0,
                                             double M1) {

        // converted M1 C++ parameter reference to return value
        for (int i = 0; i < 2; i++) {

            // Calculate the details of rising
            if (details.isRiseValid()) {
                double theta1 = theta0 + 360.985647 * M1;

                theta1 = CoordinateTransformation.MapTo0To360Range(theta1);

                double n = M1 + deltaT / 86400;
                double Alpha = Interpolate.interpolate(n, Alpha1, Alpha2, Alpha3);
                double Delta = Interpolate.interpolate(n, Delta1, Delta2, Delta3);
                double H = theta1 - Longitude - Alpha * 15;
                Coordinate2D Horizontal = CoordinateTransformation.Equatorial2Horizontal(H / 15, Delta, Latitude);
                double DeltaM = (Horizontal.Y - h0)
                        / (360 * Math.cos(CoordinateTransformation.degreesToRadians(Delta)) * Math.cos(LatitudeRad)
                        * Math.sin(CoordinateTransformation.degreesToRadians(H)));

                M1 += DeltaM;
            }
        }

        return M1;
    }

    /**
     * Method description
     *
     * @param details
     * @param theta0
     * @param deltaT
     * @param Alpha1
     * @param Delta1
     * @param Alpha2
     * @param Delta2
     * @param Alpha3
     * @param Delta3
     * @param Longitude
     * @param Latitude
     * @param LatitudeRad
     * @param h0
     * @param M2
     * @return
     */
    public static double calculateSetHelper(JulianTransitDetails details, double theta0, double deltaT, double Alpha1, double Delta1,
                                            double Alpha2, double Delta2, double Alpha3, double Delta3, double Longitude, double Latitude, double LatitudeRad, double h0,
                                            double M2) {

        // converted M2 C++ parameter reference to return value
        for (int i = 0; i < 2; i++) {

            // Calculate the details of setting
            if (details.isSetValid()) {
                double theta1 = theta0 + 360.985647 * M2;

                theta1 = CoordinateTransformation.MapTo0To360Range(theta1);

                double n = M2 + deltaT / 86400;
                double Alpha = Interpolate.interpolate(n, Alpha1, Alpha2, Alpha3);
                double Delta = Interpolate.interpolate(n, Delta1, Delta2, Delta3);
                double H = theta1 - Longitude - Alpha * 15;
                Coordinate2D Horizontal = CoordinateTransformation.Equatorial2Horizontal(H / 15, Delta, Latitude);
                double DeltaM = (Horizontal.Y - h0)
                        / (360 * Math.cos(CoordinateTransformation.degreesToRadians(Delta)) * Math.cos(LatitudeRad)
                        * Math.sin(CoordinateTransformation.degreesToRadians(H)));

                M2 += DeltaM;
            }
        }

        return M2;
    }

    /**
     * Method description
     *
     * @param theta0
     * @param deltaT
     * @param Alpha1
     * @param Alpha2
     * @param Alpha3
     * @param Longitude
     * @param M0
     * @return
     */
    public static double calculateTransitHelper(double theta0, double deltaT, double Alpha1, double Alpha2, double Alpha3,
                                                double Longitude, double M0) {

        // converted M0 C++ parameter reference to return value
        for (int i = 0; i < 2; i++) {

            // Calculate the details of transit
            double theta1 = theta0 + 360.985647 * M0;

            theta1 = CoordinateTransformation.MapTo0To360Range(theta1);

            double n = M0 + deltaT / 86400;
            double Alpha = Interpolate.interpolate(n, Alpha1, Alpha2, Alpha3);
            double H = theta1 - Longitude - Alpha * 15;

            H = CoordinateTransformation.MapTo0To360Range(H);

            if (H > 180) {
                H -= 360;
            }

            double DeltaM = -H / 360;

            M0 += DeltaM;
        }

        return M0;
    }

    /**
     * Method description
     *
     * @param JD
     * @param Alpha1
     * @param Delta1
     * @param Alpha2
     * @param Delta2
     * @param Alpha3
     * @param Delta3
     * @param Longitude
     * @param Latitude
     * @param h0
     * @return
     */
    public static JulianTransitDetails calculate(double JD, double Alpha1, double Delta1, double Alpha2, double Delta2, double Alpha3,
                                                     double Delta3, double Longitude, double Latitude, double h0) {

        // What will be the return value
        JulianTransitDetails details = new JulianTransitDetails(new JulianDate(JD, true));

        details.setRiseValid(false);
        details.setSetValid(false);
        details.setTransitAboveHorizon(false);

        // Calculate the sidereal time
        double theta0 = Sidereal.apparentGreenwichSiderealTime(JD);

        theta0 *= 15;    // Express it as degrees

        // Calculate deltat
        double deltaT = DynamicalTime.deltaT(JD);

        // Convert values to radians
        double Delta2Rad = CoordinateTransformation.degreesToRadians(Delta2);
        double LatitudeRad = CoordinateTransformation.degreesToRadians(Latitude);

        // Convert the standard latitude to radians
        double h0Rad = CoordinateTransformation.degreesToRadians(h0);

        // Calculate cosH0
        double cosH0 = (Math.sin(h0Rad) - Math.sin(LatitudeRad) * Math.sin(Delta2Rad)) / (Math.cos(LatitudeRad) * Math.cos(Delta2Rad));

        // Calculate M0
        double M0 = calculateTransit(Alpha2, theta0, Longitude);

        // Calculate M1 & M2
        double[] riseSet = calculateRiseSet(M0, cosH0, details);
        double M1 = riseSet[0];
        double M2 = riseSet[1];

        // Ensure the RA values are corrected for interpolation. Due to important Remark 2 by Meeus on Interopolation of RA values
        double[] alphas = correctRAValuesForInterpolation(new double[]{Alpha1, Alpha2, Alpha3});

        Alpha1 = alphas[0];
        Alpha2 = alphas[1];
        Alpha3 = alphas[2];

        // Do the main work
        M0 = calculateTransitHelper(theta0, deltaT, Alpha1, Alpha2, Alpha3, Longitude, M0);
        M1 = calculateRiseHelper(details, theta0, deltaT, Alpha1, Delta1, Alpha2, Delta2, Alpha3, Delta3, Longitude, Latitude, LatitudeRad,
                h0, M1);
        M2 = calculateSetHelper(details, theta0, deltaT, Alpha1, Delta1, Alpha2, Delta2, Alpha3, Delta3, Longitude, Latitude, LatitudeRad,
                h0, M2);
        details.setRise(details.isRiseValid()
                ? (M1 * 24)
                : 0.0);
        details.setSet(details.isSetValid()
                ? (M2 * 24)
                : 0.0);
        details.setTransit(M0 * 24);    // We always return the transit time even if it occurs below the horizon

        return details;
    }

}


