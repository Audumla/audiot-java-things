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

public class CAASun {

    public static double GeometricEclipticLongitude(double JD) {
        return CAACoordinateTransformation.MapTo0To360Range(CAAEarth.EclipticLongitude(JD) + 180);
    }

    public static double GeometricEclipticLatitude(double JD) {
        return -CAAEarth.EclipticLatitude(JD);
    }

    public static double GeometricEclipticLongitudeJ2000(double JD) {
        return CAACoordinateTransformation.MapTo0To360Range(CAAEarth.EclipticLongitudeJ2000(JD) + 180);
    }

    public static double GeometricEclipticLatitudeJ2000(double JD) {
        return -CAAEarth.EclipticLatitudeJ2000(JD);
    }

    public static double GeometricFK5EclipticLongitude(double JD) {
        //Convert to the FK5 stystem
        double Longitude = GeometricEclipticLongitude(JD);
        double Latitude = GeometricEclipticLatitude(JD);
        Longitude += CAAFK5.CorrectionInLongitude(Longitude, Latitude, JD);

        return Longitude;
    }

    public static double GeometricFK5EclipticLatitude(double JD) {
        //Convert to the FK5 stystem
        double Longitude = GeometricEclipticLongitude(JD);
        double Latitude = GeometricEclipticLatitude(JD);
        double SunLatCorrection = CAAFK5.CorrectionInLatitude(Longitude, JD);
        Latitude += SunLatCorrection;

        return Latitude;
    }

    public static double ApparentEclipticLongitude(double JD) {
        double Longitude = GeometricFK5EclipticLongitude(JD);

        //Apply the correction in longitude due to nutation
        Longitude += CAACoordinateTransformation.DMSToDegrees(0, 0, CAANutation.NutationInLongitude(JD),true);

        //Apply the correction in longitude due to aberration
        double R = CAAEarth.RadiusVector(JD);
        Longitude -= CAACoordinateTransformation.DMSToDegrees(0, 0, 20.4898 / R,true);

        return Longitude;
    }

    public static double ApparentEclipticLatitude(double JD) {
        return GeometricFK5EclipticLatitude(JD);
    }

    public static CAA3DCoordinate EquatorialRectangularCoordinatesMeanEquinox(double JD) {
        double Longitude = CAACoordinateTransformation.DegreesToRadians(GeometricFK5EclipticLongitude(JD));
        double Latitude = CAACoordinateTransformation.DegreesToRadians(GeometricFK5EclipticLatitude(JD));
        double R = CAAEarth.RadiusVector(JD);
        double epsilon = CAACoordinateTransformation.DegreesToRadians(CAANutation.MeanObliquityOfEcliptic(JD));

        CAA3DCoordinate value = new CAA3DCoordinate();
        value.X = R * Math.cos(Latitude) * Math.cos(Longitude);
        value.Y = R * (Math.cos(Latitude) * Math.sin(Longitude) * Math.cos(epsilon) -Math.sin(Latitude) * Math.sin(epsilon));
        value.Z = R * (Math.cos(Latitude) * Math.sin(Longitude) * Math.sin(epsilon) + Math.sin(Latitude) * Math.cos(epsilon));

        return value;
    }

    public static CAA3DCoordinate EclipticRectangularCoordinatesJ2000(double JD) {
        double Longitude = GeometricEclipticLongitudeJ2000(JD);
        Longitude = CAACoordinateTransformation.DegreesToRadians(Longitude);
        double Latitude = GeometricEclipticLatitudeJ2000(JD);
        Latitude = CAACoordinateTransformation.DegreesToRadians(Latitude);
        double R = CAAEarth.RadiusVector(JD);

        CAA3DCoordinate value = new CAA3DCoordinate();
        double coslatitude = Math.cos(Latitude);
        value.X = R * coslatitude * Math.cos(Longitude);
        value.Y = R * coslatitude * Math.sin(Longitude);
        value.Z = R * Math.sin(Latitude);

        return value;
    }

    public static CAA3DCoordinate EquatorialRectangularCoordinatesJ2000(double JD) {
        CAA3DCoordinate value = EclipticRectangularCoordinatesJ2000(JD);
        value = CAAFK5.ConvertVSOPToFK5J2000(value);

        return value;
    }

    public static CAA3DCoordinate EquatorialRectangularCoordinatesB1950(double JD) {
        CAA3DCoordinate value = EclipticRectangularCoordinatesJ2000(JD);
        value = CAAFK5.ConvertVSOPToFK5B1950(value);

        return value;
    }

    public static CAA3DCoordinate EquatorialRectangularCoordinatesAnyEquinox(double JD, double JDEquinox) {
        CAA3DCoordinate value = EquatorialRectangularCoordinatesJ2000(JD);
        value = CAAFK5.ConvertVSOPToFK5AnyEquinox(value, JDEquinox);

        return value;
    }
}
