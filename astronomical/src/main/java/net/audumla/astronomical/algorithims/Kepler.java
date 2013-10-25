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

public class Kepler
{
    public static double calculate(double M, double e) {
        return calculate(M,e,53);
    }

    public static double calculate(double M, double e, int nIterations)
    {
        //Convert from degrees to radians
        M = CoordinateTransformation.degreesToRadians(M);
        double PI = CoordinateTransformation.pI();

        double F = 1;
        if (M < 0)
        {
            F = -1;
        }
        M = Math.abs(M) / (2 * PI);
        M = (M - (int)(M)) * 2 * PI * F;
        if (M < 0)
        {
            M += 2 * PI;
        }
        F = 1;
        if (M > PI)
        {
            F = -1;
        }
        if (M > PI)
        {
            M = 2 * PI - M;
        }

        double E = PI / 2;
        double scale = PI / 4;
        for (int i = 0; i < nIterations; i++)
        {
            double R = E - e * Math.sin(E);
            if (M > R)
            {
                E += scale;
            }
            else
            {
                E -= scale;
            }
            scale /= 2;
        }

        //Convert the result back to degrees
        return CoordinateTransformation.radiansToDegrees(E) * F;
    }
}