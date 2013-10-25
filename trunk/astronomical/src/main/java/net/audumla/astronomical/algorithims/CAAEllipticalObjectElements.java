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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CAAEllipticalObjectElements
{
    //Constructors / Destructors
    public CAAEllipticalObjectElements()
    {
        this.a = 0;
        this.e = 0;
        this.i = 0;
        this.w = 0;
        this.omega = 0;
        this.JDEquinox = 0;
        this.T = 0;
    }

    //member variables
    public double a;
    public double e;
    public double i;
    public double w;
    public double omega;
    public double JDEquinox;
    public double T;
}