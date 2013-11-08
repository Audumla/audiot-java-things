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


public class FK5 {

    /**
     * Method description
     *
     *
     * @param Longitude
     * @param Latitude
     * @param JD
     *
     * @return
     */
    public static double correctionInLongitude(double Longitude, double Latitude, double JD) {
        double T = (JD - 2451545) / 36525;
        double Ldash = Longitude - 1.397 * T - 0.00031 * T * T;

        // Convert to radians
        Ldash = CoordinateTransformation.degreesToRadians(Ldash);
        Latitude = CoordinateTransformation.degreesToRadians(Latitude);

        double value = -0.09033 + 0.03916 * (Math.cos(Ldash) + Math.sin(Ldash)) * Math.tan(Latitude);

        return CoordinateTransformation.dMSToDegrees(0, 0, value, true);
    }

    /**
     * Method description
     *
     *
     * @param Longitude
     * @param JD
     *
     * @return
     */
    public static double correctionInLatitude(double Longitude, double JD) {
        double T = (JD - 2451545) / 36525;
        double Ldash = Longitude - 1.397 * T - 0.00031 * T * T;

        // Convert to radians
        Ldash = CoordinateTransformation.degreesToRadians(Ldash);

        double value = 0.03916 * (Math.cos(Ldash) - Math.sin(Ldash));

        return CoordinateTransformation.dMSToDegrees(0, 0, value, true);
    }

    /**
     * Method description
     *
     *
     * @param value
     *
     * @return
     */
    public static Coordinate3D ConvertVSOPToFK5J2000(Coordinate3D value) {
        Coordinate3D result = new Coordinate3D();

        result.X = value.X + 0.000000440360 * value.Y - 0.000000190919 * value.Z;
        result.Y = -0.000000479966 * value.X + 0.917482137087 * value.Y - 0.397776982902 * value.Z;
        result.Z = 0.397776982902 * value.Y + 0.917482137087 * value.Z;

        return result;
    }

    /**
     * Method description
     *
     *
     * @param value
     *
     * @return
     */
    public static Coordinate3D ConvertVSOPToFK5B1950(Coordinate3D value) {
        Coordinate3D result = new Coordinate3D();

        result.X = 0.999925702634 * value.X + 0.012189716217 * value.Y + 0.000011134016 * value.Z;
        result.Y = -0.011179418036 * value.X + 0.917413998946 * value.Y - 0.397777041885 * value.Z;
        result.Z = -0.004859003787 * value.X + 0.397747363646 * value.Y + 0.917482111428 * value.Z;

        return result;
    }

    /**
     * Method description
     *
     *
     * @param value
     * @param JDEquinox
     *
     * @return
     */
    public static Coordinate3D ConvertVSOPToFK5AnyEquinox(Coordinate3D value, double JDEquinox) {
        double t = (JDEquinox - 2451545.0) / 36525;
        double tsquared = t * t;
        double tcubed = tsquared * t;
        double sigma = 2306.2181 * t + 0.30188 * tsquared + 0.017988 * tcubed;

        sigma = CoordinateTransformation.degreesToRadians(CoordinateTransformation.dMSToDegrees(0, 0, sigma, true));

        double zeta = 2306.2181 * t + 1.09468 * tsquared + 0.018203 * tcubed;

        zeta = CoordinateTransformation.degreesToRadians(CoordinateTransformation.dMSToDegrees(0, 0, zeta, true));

        double phi = 2004.3109 * t - 0.42665 * tsquared - 0.041833 * tcubed;

        phi = CoordinateTransformation.degreesToRadians(CoordinateTransformation.dMSToDegrees(0, 0, phi, true));

        double cossigma = Math.cos(sigma);
        double coszeta = Math.cos(zeta);
        double cosphi = Math.cos(phi);
        double sinsigma = Math.sin(sigma);
        double sinzeta = Math.sin(zeta);
        double sinphi = Math.sin(phi);
        double xx = cossigma * coszeta * cosphi - sinsigma * sinzeta;
        double xy = sinsigma * coszeta + cossigma * sinzeta * cosphi;
        double xz = cossigma * sinphi;
        double yx = -cossigma * sinzeta - sinsigma * coszeta * cosphi;
        double yy = cossigma * coszeta - sinsigma * sinzeta * cosphi;
        double yz = -sinsigma * sinphi;
        double zx = -coszeta * sinphi;
        double zy = -sinzeta * sinphi;
        double zz = cosphi;
        Coordinate3D result = new Coordinate3D();

        result.X = xx * value.X + yx * value.Y + zx * value.Z;
        result.Y = xy * value.X + yy * value.Y + zy * value.Z;
        result.Z = xz * value.X + yz * value.Y + zz * value.Z;

        return result;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
