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
import net.audumla.astronomy.TransitDetails;

import java.util.Date;

/**
 * Class description
 *
 * @author Marius Gleeson
 */
public class JulianTransitDetails implements TransitDetails {

    // Member variables
    private JulianDate referenceTime;
    private boolean riseValid;
    private double rise;
    private boolean transitAboveHorizon;
    private double transit;
    private boolean setValid;
    private double set;

    // Constructors / Destructors

    /**
     * Constructs ...
     *
     * @param referenceTime
     */
    public JulianTransitDetails(JulianDate referenceTime) {
        this.setRiseValid(false);
        this.setRise(0);
        this.setTransitAboveHorizon(false);
        this.setTransit(0);
        this.setSetValid(false);
        this.setSet(0);
        this.referenceTime = referenceTime;
    }

    /**
     * Method description
     *
     * @return
     */
    public JulianDate getJulianRise() {
        double rtsJD = (referenceTime.julian() + ((getRise()) / 24.00));

        return new JulianDate(rtsJD, true);
    }

    /**
     * Method description
     *
     * @return
     */
    public JulianDate getJulianSet() {
        double rtsJD = (referenceTime.julian() + ((getSet()) / 24.00));

        return new JulianDate(rtsJD, true);
    }

    /**
     * Field description
     *
     * @return
     */

    /**
     * Method description
     *
     * @return
     */
    public boolean isRiseValid() {
        return riseValid;
    }

    /**
     * Field description
     *
     * @param riseValid
     */
    public void setRiseValid(boolean riseValid) {
        this.riseValid = riseValid;
    }

    /**
     * Method description
     *
     * @return
     */
    public boolean isTransitAboveHorizon() {
        return transitAboveHorizon;
    }

    /**
     * Field description
     *
     * @param transitAboveHorizon
     */
    public void setTransitAboveHorizon(boolean transitAboveHorizon) {
        this.transitAboveHorizon = transitAboveHorizon;
    }

    /**
     * Method description
     *
     * @return
     */
    public double getTransitHours() {
        return getTransitPeriod();
    }

    /**
     * Method description
     *
     * @return
     */
    public boolean isSetValid() {
        return setValid;
    }

    /**
     * Method description
     *
     * @param setValid
     */
    public void setSetValid(boolean setValid) {
        this.setValid = setValid;
    }

    /**
     * Field description
     *
     * @return
     */
    public double getRise() {
        return rise;
    }

    /**
     * Method description
     *
     * @param rise
     */
    public void setRise(double rise) {
        this.rise = rise;
    }

    /**
     * Field description
     *
     * @return
     */
    public double getTransitPeriod() {
        return transit;
    }

    /**
     * Method description
     *
     * @param transit
     */
    public void setTransit(double transit) {
        this.transit = transit;
    }

    /**
     * Field description
     *
     * @return
     */
    public double getSet() {
        return set;
    }

    /**
     * Method description
     *
     * @param set
     */
    public void setSet(double set) {
        this.set = set;
    }

    /**
     * Method description
     *
     * @return
     */
    @Override
    public Date getRiseTime() {
        return getJulianRise().toDate();
    }

    /**
     * Method description
     *
     * @return
     */
    @Override
    public Date getSetTime() {
        return getJulianSet().toDate();
    }

    public JulianDate getReferenceTime() {
        return referenceTime;
    }

    public void setReferenceTime(JulianDate referenceTime) {
        this.referenceTime = referenceTime;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
