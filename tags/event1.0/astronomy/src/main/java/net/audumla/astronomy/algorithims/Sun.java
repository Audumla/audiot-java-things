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
public class Sun extends EllipticalObject {

    /**
     * Astronomical sunrise/set is when the sun is 18 degrees below the horizon.
     */
    public static final double ASTRONOMICAL = -18;
    /**
     * Nautical sunrise/set is when the sun is 12 degrees below the horizon.
     */
    public static final double NAUTICAL = -12;
    /**
     * Civil sunrise/set (dawn/dusk) is when the sun is 6 degrees below the horizon.
     */
    public static final double CIVIL = -6;
    /**
     * Official sunrise/set is when the sun is 50' below the horizon.
     */
    public static final double OFFICIAL = 0.83333;
    protected Earth earth = new Earth();

    public Sun() {
    }

    public double geometricEclipticLongitude(double JD) {
        return CoordinateTransformation.MapTo0To360Range(earth.eclipticLongitude(JD) + 180);
    }

    public double geometricEclipticLatitude(double JD) {
        return -earth.eclipticLatitude(JD);
    }

    public double geometricEclipticLongitudeJ2000(double JD) {
        return CoordinateTransformation.MapTo0To360Range(earth.eclipticLongitudeJ2000(JD) + 180);
    }

    public double geometricEclipticLatitudeJ2000(double JD) {
        return -earth.eclipticLatitudeJ2000(JD);
    }

    public double geometricFK5EclipticLongitude(double JD) {
        //Convert to the FK5 stystem
        double Longitude = geometricEclipticLongitude(JD);
        double Latitude = geometricEclipticLatitude(JD);
        Longitude += FK5.correctionInLongitude(Longitude, Latitude, JD);

        return Longitude;
    }

    public double geometricFK5EclipticLatitude(double JD) {
        //Convert to the FK5 stystem
        double Longitude = geometricEclipticLongitude(JD);
        double Latitude = geometricEclipticLatitude(JD);
        double SunLatCorrection = FK5.correctionInLatitude(Longitude, JD);
        Latitude += SunLatCorrection;

        return Latitude;
    }

    public double apparentEclipticLongitude(double JD) {
        double Longitude = geometricFK5EclipticLongitude(JD);

        //Apply the correction in longitude due to nutation
        Longitude += CoordinateTransformation.dMSToDegrees(0, 0, Nutation.nutationInLongitude(JD), true);

        //Apply the correction in longitude due to aberration
        double R = earth.radiusVector(JD);
        Longitude -= CoordinateTransformation.dMSToDegrees(0, 0, 20.4898 / R, true);

        return Longitude;
    }

    public double apparentEclipticLatitude(double JD) {
        return geometricFK5EclipticLatitude(JD);
    }

    public Coordinate3D equatorialRectangularCoordinatesMeanEquinox(double JD) {
        double Longitude = CoordinateTransformation.degreesToRadians(geometricFK5EclipticLongitude(JD));
        double Latitude = CoordinateTransformation.degreesToRadians(geometricFK5EclipticLatitude(JD));
        double R = earth.radiusVector(JD);
        double epsilon = CoordinateTransformation.degreesToRadians(Nutation.meanObliquityOfEcliptic(JD));

        Coordinate3D value = new Coordinate3D();
        value.X = R * Math.cos(Latitude) * Math.cos(Longitude);
        value.Y = R * (Math.cos(Latitude) * Math.sin(Longitude) * Math.cos(epsilon) - Math.sin(Latitude) * Math.sin(epsilon));
        value.Z = R * (Math.cos(Latitude) * Math.sin(Longitude) * Math.sin(epsilon) + Math.sin(Latitude) * Math.cos(epsilon));

        return value;
    }

    public Coordinate3D eclipticRectangularCoordinatesJ2000(double JD) {
        double Longitude = geometricEclipticLongitudeJ2000(JD);
        Longitude = CoordinateTransformation.degreesToRadians(Longitude);
        double Latitude = geometricEclipticLatitudeJ2000(JD);
        Latitude = CoordinateTransformation.degreesToRadians(Latitude);
        double R = earth.radiusVector(JD);

        Coordinate3D value = new Coordinate3D();
        double coslatitude = Math.cos(Latitude);
        value.X = R * coslatitude * Math.cos(Longitude);
        value.Y = R * coslatitude * Math.sin(Longitude);
        value.Z = R * Math.sin(Latitude);

        return value;
    }

    public Coordinate3D equatorialRectangularCoordinatesJ2000(double JD) {
        Coordinate3D value = eclipticRectangularCoordinatesJ2000(JD);
        value = FK5.ConvertVSOPToFK5J2000(value);

        return value;
    }

    public Coordinate3D equatorialRectangularCoordinatesB1950(double JD) {
        Coordinate3D value = eclipticRectangularCoordinatesJ2000(JD);
        value = FK5.ConvertVSOPToFK5B1950(value);

        return value;
    }

    public Coordinate3D equatorialRectangularCoordinatesAnyEquinox(double JD, double JDEquinox) {
        Coordinate3D value = equatorialRectangularCoordinatesJ2000(JD);
        value = FK5.ConvertVSOPToFK5AnyEquinox(value, JDEquinox);

        return value;
    }

    @Override
    public double eclipticLongitude(double JD) {
        return geometricEclipticLongitude(JD);
    }

    @Override
    public double eclipticLatitude(double JD) {
        return geometricEclipticLatitude(JD);
    }

    @Override
    public double radiusVector(double JD) {
        return earth.radiusVector(JD);
    }
}
