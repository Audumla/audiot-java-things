package net.audumla.astronomy;

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

import net.audumla.Time;
import net.audumla.astronomy.algorithims.EquinoxesAndSolstices;
import net.audumla.astronomy.algorithims.JulianDate;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class SeasonalEvent implements AstronomicEvent {
    private static final Logger logger = LoggerFactory.getLogger(SeasonalEvent.class);
    private Date time;

    interface SeasonalFunction {
        double time(int year,double latitude);
    }

    public static final SeasonalFunction WINTER_SOLSTICE = EquinoxesAndSolstices::winterSolstice;
    public static final SeasonalFunction SUMMER_SOLSTICE = EquinoxesAndSolstices::summerSolstice;
    public static final SeasonalFunction JUNE_SOLSTICE = EquinoxesAndSolstices::juneSolstice;
    public static final SeasonalFunction DECEMBER_SOLSTICE = EquinoxesAndSolstices::decemberSolstice;
    public static final SeasonalFunction AUTUMN_EQUINOX = EquinoxesAndSolstices::autumnEquinox;
    public static final SeasonalFunction SPRING_EQUINOX = EquinoxesAndSolstices::springEquinox;
    public static final SeasonalFunction SEPTEMBER_EQUINOX = EquinoxesAndSolstices::septemberEquinox;
    public static final SeasonalFunction MARCH_EQUINOX = EquinoxesAndSolstices::marchEquinox;
    public static final SeasonalFunction SUMMER_START = SeasonalEvent::summerStart;
    public static final SeasonalFunction WINTER_START = SeasonalEvent::winterStart;
    public static final SeasonalFunction SPRING_START = SeasonalEvent::springStart;
    public static final SeasonalFunction AUTUMN_START = SeasonalEvent::autumnStart;

    protected SeasonalFunction event;
    protected Geolocation location;

    public SeasonalEvent(SeasonalFunction event, Geolocation location) {
        this.event = event;
        this.location = location;
    }

    @Override
    public Date getCalculatedEventTime() {
        return time;
    }

    @Override
    public Date calculateEventFrom(Date date) {
        time = new JulianDate(event.time(DateUtils.toCalendar(date).get(Calendar.YEAR),location.getLatitude(Geolocation.Direction.NORTH)),true).toDate();
        return time;
    }

    @Override
    public AstronomicEvent getNextEvent() {
        SeasonalEvent nevent = new SeasonalEvent(event, location);
        nevent.calculateEventFrom(DateUtils.addYears(time,1));
        return nevent;
    }

    @Override
    public AstronomicEvent getPreviousEvent() {
        SeasonalEvent nevent = new SeasonalEvent(event, location);
        nevent.calculateEventFrom(DateUtils.addYears(time,-1));
        return nevent;
    }

    protected static double summerStart(int year, double latitude) {
        Date t = Time.getZeroDate();
        t = DateUtils.setYears(t,year);
        if (latitude >= 0) {
            t = DateUtils.setMonths(t, Calendar.JUNE);
        }
        else {
            t = DateUtils.setMonths(t, Calendar.DECEMBER);
        }
        return new JulianDate(t).julian();
    }

    protected static double winterStart(int year, double latitude) {
        Date t = Time.getZeroDate();
        t = DateUtils.setYears(t,year);
        if (latitude >= 0) {
            t = DateUtils.setMonths(t, Calendar.DECEMBER);
        }
        else {
            t = DateUtils.setMonths(t, Calendar.JUNE);
        }
        return new JulianDate(t).julian();
    }

    protected static double autumnStart(int year, double latitude) {
        Date t = Time.getZeroDate();
        t = DateUtils.setYears(t,year);
        if (latitude >= 0) {
            t = DateUtils.setMonths(t, Calendar.SEPTEMBER);
        }
        else {
            t = DateUtils.setMonths(t, Calendar.MARCH);
        }
        return new JulianDate(t).julian();
    }

    protected static double springStart(int year, double latitude) {
        Date t = Time.getZeroDate();
        t = DateUtils.setYears(t,year);
        if (latitude >= 0) {
            t = DateUtils.setMonths(t, Calendar.MARCH);
        }
        else {
            t = DateUtils.setMonths(t, Calendar.SEPTEMBER);
        }
        return new JulianDate(t).julian();
    }
}
