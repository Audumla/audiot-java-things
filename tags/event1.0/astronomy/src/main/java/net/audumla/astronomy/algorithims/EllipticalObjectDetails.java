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


public class EllipticalObjectDetails
{
    //Constructors / Destructors
    public EllipticalObjectDetails()
    {
        this.HeliocentricEclipticLongitude = 0;
        this.HeliocentricEclipticLatitude = 0;
        this.TrueGeocentricRA = 0;
        this.TrueGeocentricDeclination = 0;
        this.TrueGeocentricDistance = 0;
        this.TrueGeocentricLightTime = 0;
        this.AstrometricGeocentricRA = 0;
        this.AstrometricGeocentricDeclination = 0;
        this.AstrometricGeocentricDistance = 0;
        this.AstrometricGeocentricLightTime = 0;
        this.Elongation = 0;
        this.PhaseAngle = 0;
    }

    //Member variables
    public Coordinate3D HeliocentricRectangularEquatorial = new Coordinate3D();
    public Coordinate3D HeliocentricRectangularEcliptical = new Coordinate3D();
    public double HeliocentricEclipticLongitude;
    public double HeliocentricEclipticLatitude;
    public double TrueGeocentricRA;
    public double TrueGeocentricDeclination;
    public double TrueGeocentricDistance;
    public double TrueGeocentricLightTime;
    public double AstrometricGeocentricRA;
    public double AstrometricGeocentricDeclination;
    public double AstrometricGeocentricDistance;
    public double AstrometricGeocentricLightTime;
    public double Elongation;
    public double PhaseAngle;
}
