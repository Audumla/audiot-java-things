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

public class CAASidereal
{
    public static double meanGreenwichSiderealTime(final double JD)
    {
        CAADate date = new CAADate();
        date.Set(JD, CAADate.AfterPapalReform(JD));
        CAACalendarDate dt = date.Get();
        date.Set(dt.year, dt.month, dt.day, 0, 0, 0, date.InGregorianCalendar());
        double JDMidnight = date.Julian();

        //Calculate the sidereal time at midnight
        double T = (JDMidnight - 2451545) / 36525;
        double TSquared = T * T;
        double TCubed = TSquared * T;
        double Value = 100.46061837 + (36000.770053608 * T) + (0.000387933 * TSquared) - (TCubed / 38710000);

        //Adjust by the time of day
        Value += (((dt.hour * 15) + (dt.minute * 0.25) + (dt.second * 0.0041666666666666666666666666666667)) * 1.00273790935);

        Value = CAACoordinateTransformation.DegreesToHours(Value);

        return CAACoordinateTransformation.MapTo0To24Range(Value);
    }
    public static double apparentGreenwichSiderealTime(final double JD)
    {
        double MeanObliquity = CAANutation.MeanObliquityOfEcliptic(JD);
        double TrueObliquity = MeanObliquity + CAANutation.NutationInObliquity(JD) / 3600;
        double NutationInLongitude = CAANutation.NutationInLongitude(JD);

        double Value = meanGreenwichSiderealTime(JD) + (NutationInLongitude * Math.cos(CAACoordinateTransformation.DegreesToRadians(TrueObliquity)) / 54000);
        return CAACoordinateTransformation.MapTo0To24Range(Value);
    }
}