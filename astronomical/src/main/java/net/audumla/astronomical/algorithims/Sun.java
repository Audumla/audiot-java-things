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
public class Sun {

    public static double geometricEclipticLongitude(double JD) {
        return CoordinateTransformation.MapTo0To360Range(Earth.eclipticLongitude(JD) + 180);
    }

    public static double geometricEclipticLatitude(double JD) {
        return -Earth.eclipticLatitude(JD);
    }

    public static double GeometricEclipticLongitudeJ2000(double JD) {
        return CoordinateTransformation.MapTo0To360Range(Earth.EclipticLongitudeJ2000(JD) + 180);
    }

    public static double GeometricEclipticLatitudeJ2000(double JD) {
        return -Earth.EclipticLatitudeJ2000(JD);
    }

    public static double GeometricFK5EclipticLongitude(double JD) {
        //Convert to the FK5 stystem
        double Longitude = geometricEclipticLongitude(JD);
        double Latitude = geometricEclipticLatitude(JD);
        Longitude += FK5.correctionInLongitude(Longitude, Latitude, JD);

        return Longitude;
    }

    public static double GeometricFK5EclipticLatitude(double JD) {
        //Convert to the FK5 stystem
        double Longitude = geometricEclipticLongitude(JD);
        double Latitude = geometricEclipticLatitude(JD);
        double SunLatCorrection = FK5.correctionInLatitude(Longitude, JD);
        Latitude += SunLatCorrection;

        return Latitude;
    }

    public static double apparentEclipticLongitude(double JD) {
        double Longitude = GeometricFK5EclipticLongitude(JD);

        //Apply the correction in longitude due to nutation
        Longitude += CoordinateTransformation.dMSToDegrees(0, 0, Nutation.nutationInLongitude(JD), true);

        //Apply the correction in longitude due to aberration
        double R = Earth.radiusVector(JD);
        Longitude -= CoordinateTransformation.dMSToDegrees(0, 0, 20.4898 / R, true);

        return Longitude;
    }

    public static double apparentEclipticLatitude(double JD) {
        return GeometricFK5EclipticLatitude(JD);
    }

    public static Coordinate3D equatorialRectangularCoordinatesMeanEquinox(double JD) {
        double Longitude = CoordinateTransformation.degreesToRadians(GeometricFK5EclipticLongitude(JD));
        double Latitude = CoordinateTransformation.degreesToRadians(GeometricFK5EclipticLatitude(JD));
        double R = Earth.radiusVector(JD);
        double epsilon = CoordinateTransformation.degreesToRadians(Nutation.meanObliquityOfEcliptic(JD));

        Coordinate3D value = new Coordinate3D();
        value.X = R * Math.cos(Latitude) * Math.cos(Longitude);
        value.Y = R * (Math.cos(Latitude) * Math.sin(Longitude) * Math.cos(epsilon) -Math.sin(Latitude) * Math.sin(epsilon));
        value.Z = R * (Math.cos(Latitude) * Math.sin(Longitude) * Math.sin(epsilon) + Math.sin(Latitude) * Math.cos(epsilon));

        return value;
    }

    public static Coordinate3D EclipticRectangularCoordinatesJ2000(double JD) {
        double Longitude = GeometricEclipticLongitudeJ2000(JD);
        Longitude = CoordinateTransformation.degreesToRadians(Longitude);
        double Latitude = GeometricEclipticLatitudeJ2000(JD);
        Latitude = CoordinateTransformation.degreesToRadians(Latitude);
        double R = Earth.radiusVector(JD);

        Coordinate3D value = new Coordinate3D();
        double coslatitude = Math.cos(Latitude);
        value.X = R * coslatitude * Math.cos(Longitude);
        value.Y = R * coslatitude * Math.sin(Longitude);
        value.Z = R * Math.sin(Latitude);

        return value;
    }

    public static Coordinate3D EquatorialRectangularCoordinatesJ2000(double JD) {
        Coordinate3D value = EclipticRectangularCoordinatesJ2000(JD);
        value = FK5.ConvertVSOPToFK5J2000(value);

        return value;
    }

    public static Coordinate3D EquatorialRectangularCoordinatesB1950(double JD) {
        Coordinate3D value = EclipticRectangularCoordinatesJ2000(JD);
        value = FK5.ConvertVSOPToFK5B1950(value);

        return value;
    }

    public static Coordinate3D equatorialRectangularCoordinatesAnyEquinox(double JD, double JDEquinox) {
        Coordinate3D value = EquatorialRectangularCoordinatesJ2000(JD);
        value = FK5.ConvertVSOPToFK5AnyEquinox(value, JDEquinox);

        return value;
    }
}
