package net.audumla.spacetime.astrological;

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

import net.audumla.spacetime.Geolocation;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Public interface for getting the various types of sunrise/sunset.
 */
public class SunriseSunsetCalculator {

    private Geolocation location;

    private SolarEventCalculator calculator;

    /**
     * Constructs a new <code>SunriseSunsetCalculator</code> with the given <code>Location</code>
     *
     * @param location           <code>Location</code> object containing the Latitude/Longitude of the location to compute
     *                           the sunrise/sunset for.
     * @param timeZoneIdentifier String identifier for the timezone to compute the sunrise/sunset times in. In the form
     *                           "America/New_York". Please see the zi directory under the JDK installation for supported
     *                           time zones.
     */
    public SunriseSunsetCalculator(Geolocation location, String timeZoneIdentifier) {
        this.calculator = new SolarEventCalculator(location, timeZoneIdentifier);
    }

    /**
     * Constructs a new <code>SunriseSunsetCalculator</code> with the given <code>Location</code>
     *
     * @param location <code>Location</code> object containing the Latitude/Longitude of the location to compute
     *                 the sunrise/sunset for.
     * @param timeZone timezone to compute the sunrise/sunset times in.
     */
    public SunriseSunsetCalculator(Geolocation location, TimeZone timeZone) {
        this.calculator = new SolarEventCalculator(location, timeZone);
    }

    /**
     * Returns the astronomical (108deg) sunrise for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the astronomical sunrise for.
     * @return the astronomical sunrise time in HH:MM (24-hour clock) form.
     */
    public String getAstronomicalSunriseForDate(Calendar date) {
        return calculator.computeSunriseTime(net.audumla.spacetime.astrological.Zenith.ASTRONOMICAL, date);
    }

    /**
     * Returns the astronomical (108deg) sunrise for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the astronomical sunrise for.
     * @return the astronomical sunrise time as a Calendar
     */
    public Calendar getAstronomicalSunriseCalendarForDate(Calendar date) {
        return calculator.computeSunriseCalendar(net.audumla.spacetime.astrological.Zenith.ASTRONOMICAL, date);
    }

    /**
     * Returns the astronomical (108deg) sunset for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the astronomical sunset for.
     * @return the astronomical sunset time in HH:MM (24-hour clock) form.
     */
    public String getAstronomicalSunsetForDate(Calendar date) {
        return calculator.computeSunsetTime(net.audumla.spacetime.astrological.Zenith.ASTRONOMICAL, date);
    }

    /**
     * Returns the astronomical (108deg) sunset for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the astronomical sunset for.
     * @return the astronomical sunset time as a Calendar
     */
    public Calendar getAstronomicalSunsetCalendarForDate(Calendar date) {
        return calculator.computeSunsetCalendar(net.audumla.spacetime.astrological.Zenith.ASTRONOMICAL, date);
    }

    /**
     * Returns the nautical (102deg) sunrise for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the nautical sunrise for.
     * @return the nautical sunrise time in HH:MM (24-hour clock) form.
     */
    public String getNauticalSunriseForDate(Calendar date) {
        return calculator.computeSunriseTime(net.audumla.spacetime.astrological.Zenith.NAUTICAL, date);
    }

    /**
     * Returns the nautical (102deg) sunrise for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the nautical sunrise for.
     * @return the nautical sunrise time as a Calendar
     */
    public Calendar getNauticalSunriseCalendarForDate(Calendar date) {
        return calculator.computeSunriseCalendar(net.audumla.spacetime.astrological.Zenith.NAUTICAL, date);
    }

    /**
     * Returns the nautical (102deg) sunset for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the nautical sunset for.
     * @return the nautical sunset time in HH:MM (24-hour clock) form.
     */
    public String getNauticalSunsetForDate(Calendar date) {
        return calculator.computeSunsetTime(net.audumla.spacetime.astrological.Zenith.NAUTICAL, date);
    }

    /**
     * Returns the nautical (102deg) sunset for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the nautical sunset for.
     * @return the nautical sunset time as a Calendar
     */
    public Calendar getNauticalSunsetCalendarForDate(Calendar date) {
        return calculator.computeSunsetCalendar(net.audumla.spacetime.astrological.Zenith.NAUTICAL, date);
    }

    /**
     * Returns the civil sunrise (twilight, 96deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the civil sunrise for.
     * @return the civil sunrise time in HH:MM (24-hour clock) form.
     */
    public String getCivilSunriseForDate(Calendar date) {
        return calculator.computeSunriseTime(net.audumla.spacetime.astrological.Zenith.CIVIL, date);
    }

    /**
     * Returns the civil sunrise (twilight, 96deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the civil sunrise for.
     * @return the civil sunrise time as a Calendar
     */
    public Calendar getCivilSunriseCalendarForDate(Calendar date) {
        return calculator.computeSunriseCalendar(net.audumla.spacetime.astrological.Zenith.CIVIL, date);
    }

    /**
     * Returns the civil sunset (twilight, 96deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the civil sunset for.
     * @return the civil sunset time in HH:MM (24-hour clock) form.
     */
    public String getCivilSunsetForDate(Calendar date) {
        return calculator.computeSunsetTime(net.audumla.spacetime.astrological.Zenith.CIVIL, date);
    }

    /**
     * Returns the civil sunset (twilight, 96deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the civil sunset for.
     * @return the civil sunset time as a Calendar
     */
    public Calendar getCivilSunsetCalendarForDate(Calendar date) {
        return calculator.computeSunsetCalendar(net.audumla.spacetime.astrological.Zenith.CIVIL, date);
    }

    /**
     * Returns the official sunrise (90deg 50', 90.8333deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the official sunrise for.
     * @return the official sunrise time in HH:MM (24-hour clock) form.
     */
    public String getOfficialSunriseForDate(Calendar date) {
        return calculator.computeSunriseTime(net.audumla.spacetime.astrological.Zenith.OFFICIAL, date);
    }

    /**
     * Returns the official sunrise (90deg 50', 90.8333deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the official sunrise for.
     * @return the official sunrise time as a Calendar
     */
    public Calendar getOfficialSunriseCalendarForDate(Calendar date) {
        return calculator.computeSunriseCalendar(net.audumla.spacetime.astrological.Zenith.OFFICIAL, date);
    }

    /**
     * Returns the official sunrise (90deg 50', 90.8333deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the official sunset for.
     * @return the official sunset time in HH:MM (24-hour clock) form.
     */
    public String getOfficialSunsetForDate(Calendar date) {
        return calculator.computeSunsetTime(net.audumla.spacetime.astrological.Zenith.OFFICIAL, date);
    }

    /**
     * Returns the official sunrise (90deg 50', 90.8333deg) for the given date.
     *
     * @param date <code>Calendar</code> object containing the date to compute the official sunset for.
     * @return the official sunset time as a Calendar
     */
    public Calendar getOfficialSunsetCalendarForDate(Calendar date) {
        return calculator.computeSunsetCalendar(net.audumla.spacetime.astrological.Zenith.OFFICIAL, date);
    }

    /**
     * Computes the sunrise for an arbitrary declination.
     *
     * @param location Coordinates for the location to compute the sunrise/sunset for.
     * @param timeZone timezone to compute the sunrise/sunset times in.
     * @param date     <code>Calendar</code> object containing the date to compute the official sunset for.
     * @param degrees  Angle under the horizon for which to compute sunrise. For example, "civil sunrise"
     *                 corresponds to 6 degrees.
     * @return the requested sunset time as a <code>Calendar</code> object.
     */

    public static Calendar getSunrise(Geolocation location, TimeZone timeZone, Calendar date, double degrees) {
        SolarEventCalculator solarEventCalculator = new SolarEventCalculator(location, timeZone);
        return solarEventCalculator.computeSunriseCalendar(new net.audumla.spacetime.astrological.Zenith(90 - degrees), date);
    }

    /**
     * Computes the sunset for an arbitrary declination.
     *
     * @param location Coordinates for the location to compute the sunrise/sunset for.
     * @param timeZone timezone to compute the sunrise/sunset times in.
     * @param date     <code>Calendar</code> object containing the date to compute the official sunset for.
     * @param degrees  Angle under the horizon for which to compute sunrise. For example, "civil sunset"
     *                 corresponds to 6 degrees.
     * @return the requested sunset time as a <code>Calendar</code> object.
     */

    public static Calendar getSunset(Geolocation location, TimeZone timeZone, Calendar date, double degrees) {
        SolarEventCalculator solarEventCalculator = new SolarEventCalculator(location, timeZone);
        return solarEventCalculator.computeSunsetCalendar(new net.audumla.spacetime.astrological.Zenith(90 - degrees), date);
    }

    /**
     * Returns the location where the sunrise/sunset is calculated for.
     *
     * @return <code>Location</code> object representing the location of the computed sunrise/sunset.
     */
    public Geolocation getLocation() {
        return location;
    }
}
