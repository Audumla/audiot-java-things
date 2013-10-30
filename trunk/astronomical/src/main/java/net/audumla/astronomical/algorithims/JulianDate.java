package net.audumla.astronomical.algorithims;

//~--- JDK imports ------------------------------------------------------------

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
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Class description
 *
 * @author         Marius Gleeson    
 */
public class JulianDate {

    // Member variables
    protected double m_dblJulian;              // Julian day number for this date
    protected boolean m_bGregorianCalendar;    // Is this date in the Gregorian calendar

    /**
     * Constructs ...
     *
     */
    public JulianDate() {
        this.m_dblJulian = 0;
        this.m_bGregorianCalendar = false;
    }

    /**
     * Constructs ...
     *
     *
     * @param date
     */
    public JulianDate(java.util.Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTime(date);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        set(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR), c.get(Calendar.MINUTE),
            c.get(Calendar.SECOND), true);
    }

    /**
     * Constructs ...
     *
     *
     * @param JD
     * @param bGregorianCalendar
     */
    public JulianDate(double JD, boolean bGregorianCalendar) {
        set(JD, bGregorianCalendar);
    }

    /**
     * Constructs ...
     *
     *
     * @param Year
     * @param Month
     * @param Day
     * @param bGregorianCalendar
     */
    public JulianDate(int Year, int Month, double Day, boolean bGregorianCalendar) {
        set(Year, Month, Day, 0, 0, 0, bGregorianCalendar);
    }

    /**
     * Constructs ...
     *
     *
     * @param Year
     * @param Month
     * @param Day
     * @param Hour
     * @param Minute
     * @param Second
     * @param bGregorianCalendar
     */
    public JulianDate(int Year, int Month, double Day, double Hour, double Minute, double Second, boolean bGregorianCalendar) {
        set(Year, Month, Day, Hour, Minute, Second, bGregorianCalendar);
    }

    // Enums

    /**
     * Enum description
     *
     */
    public enum DAY_OF_WEEK {
        sUNDAY(0), mONDAY(1), tUESDAY(2), wEDNESDAY(3), tHURSDAY(4), fRIDAY(5), sATURDAY(6);

        private static java.util.HashMap<Integer, DAY_OF_WEEK> map = new java.util.HashMap<>();
        private int value;

        private DAY_OF_WEEK(int intValue) {
            value = intValue;
            getMap().put(intValue, this);
        }

        private static java.util.HashMap<Integer, DAY_OF_WEEK> getMap() {
            return map;
        }

        /**
         * Method description
         *
         *
         * @param intValue
         *
         * @return
         */
        public static DAY_OF_WEEK getEnum(int intValue) {
            return getMap().get(intValue);
        }

        /**
         * Method description
         *
         *
         * @return
         */
        public int getValue() {
            return value;
        }
    }

    /**
     * Method description
     *
     *
     * @param Year
     * @param Month
     * @param Day
     *
     * @return
     */
    public static boolean afterPapalReform(int Year, int Month, double Day) {
        return ((Year > 1582) || ((Year == 1582) && (Month > 10)) || ((Year == 1582) && (Month == 10) && (Day >= 15)));
    }

    /**
     * Method description
     *
     *
     * @param JD
     *
     * @return
     */
    public static boolean afterPapalReform(double JD) {
        return (JD >= 2299160.5);
    }

    /**
     * Method description
     *
     *
     * @param Year
     * @param Month
     * @param Day
     * @param bGregorianCalendar
     *
     * @return
     */
    public static double dateToJD(int Year, int Month, double Day, boolean bGregorianCalendar) {
        int Y = Year;
        int M = Month;

        if (M < 3) {
            Y = Y - 1;
            M = M + 12;
        }

        int B = 0;

        if (bGregorianCalendar) {
            int A = (int) (Y / 100.0);

            B = 2 - A + (int) (A / 4.0);
        }

        return (int) (365.25 * (Y + 4716)) + (int) (30.6001 * (M + 1)) + Day + B - 1524.5;
    }

    /**
     * Method description
     *
     *
     * @param Year
     * @param bGregorianCalendar
     *
     * @return
     */
    public static boolean isLeap(int Year, boolean bGregorianCalendar) {
        if (bGregorianCalendar) {
            if ((Year % 100) == 0) {
                return ((Year % 400) == 0);
            } else {
                return ((Year % 4) == 0);
            }
        } else {
            return ((Year % 4) == 0);
        }
    }

    /**
     * Method description
     *
     *
     * @param Year
     * @param Month
     * @param Day
     *
     * @return
     */
    public static CalendarDate julianToGregorian(int Year, int Month, int Day) {
        JulianDate date = new JulianDate(Year, Month, Day, false);

        date.setInGregorianCalendar(true);

        return date.get();
    }

    /**
     * Method description
     *
     *
     * @param Year
     * @param Month
     * @param Day
     *
     * @return
     */
    public static CalendarDate gregorianToJulian(int Year, int Month, int Day) {
        JulianDate date = new JulianDate(Year, Month, Day, true);

        date.setInGregorianCalendar(false);

        return date.get();
    }

    /**
     * Method description
     *
     *
     * @param Year
     * @param Month
     * @param Day
     * @param Hour
     * @param Minute
     * @param Second
     * @param bGregorianCalendar
     */
    public void set(int Year, int Month, double Day, double Hour, double Minute, double Second, boolean bGregorianCalendar) {
        double dblDay = Day + (Hour / 24) + (Minute / 1440) + (Second / 86400);

        set(dateToJD(Year, Month, dblDay, bGregorianCalendar), bGregorianCalendar);
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public CalendarDate get() {
        CalendarDate dt = new CalendarDate();
        double JD = m_dblJulian + 0.5;
        double tempZ = Math.floor(JD);
        double F = JD - tempZ;
        int Z = (int) (tempZ);
        int A;

        if (m_bGregorianCalendar)    // There is a difference here between the Meeus implementation and this one

        // if (Z >= 2299161)       //The Meeus implementation automatically assumes the Gregorian Calendar
        // came into effect on 15 October 1582 (JD: 2299161), while the JulianDate
        // implementation has a "m_bGregorianCalendar" value to decide if the date
        // was specified in the Gregorian or Julian Calendars. This difference
        // means in effect that JulianDate fully supports a propalactive version of the
        // Julian calendar. This allows you to construct Julian dates after the Papal
        // reform in 1582. This is useful if you want to construct dates in countries
        // which did not immediately adapt the Gregorian calendar
        {
            int alpha = (int) ((Z - 1867216.25) / 36524.25);

            A = Z + 1 + alpha - (int) ((int) alpha / 4.0);
        } else {
            A = Z;
        }

        int B = A + 1524;
        int C = (int) ((B - 122.1) / 365.25);
        int D = (int) (365.25 * C);
        int E = (int) ((B - D) / 30.6001);
        double dblDay = B - D - (int) (30.6001 * E) + F;

        dt.day = (int) (dblDay);

        if (E < 14) {
            dt.month = E - 1;
        } else {
            dt.month = E - 13;
        }

        if (dt.month > 2) {
            dt.year = C - 4716;
        } else {
            dt.year = C - 4715;
        }

        tempZ = Math.floor(dblDay);
        F = dblDay - tempZ;
        dt.hour = (int) (F * 24);
        dt.minute = (int) ((F - (dt.hour) / 24.0) * 1440.0);
        dt.second = (F - (dt.hour / 24.0) - (dt.minute / 1440.0)) * 86400.0;

        return dt;
    }

    /**
     * Method description
     *
     *
     * @param JD
     * @param bGregorianCalendar
     */
    public void set(double JD, boolean bGregorianCalendar) {
        m_dblJulian = JD;
        setInGregorianCalendar(bGregorianCalendar);
    }

    /**
     * Method description
     *
     *
     * @param bGregorianCalendar
     */
    public void setInGregorianCalendar(boolean bGregorianCalendar) {
        boolean bAfterPapalReform = (m_dblJulian >= 2299160.5);

        m_bGregorianCalendar = bGregorianCalendar && bAfterPapalReform;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public int day() {
        return get().day;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public int month() {
        return get().month;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public int year() {
        return get().year;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public int hour() {
        return get().hour;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public int minute() {
        return get().minute;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public double second() {
        return get().second;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public JulianDate.DAY_OF_WEEK dayOfWeek() {
        return DAY_OF_WEEK.getEnum((int) ((m_dblJulian + 1.5) % 7));
    }

    /**
     * Method description
     *
     *
     * @param Month
     * @param bLeap
     *
     * @return
     */
    public int daysInMonth(int Month, boolean bLeap) {
        assert (Month >= 1) && (Month <= 12);

        int[] MonthLength = {
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        };

        if (bLeap) {
            MonthLength[1]++;
        }

        return MonthLength[Month - 1];
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public int daysInMonth() {
        CalendarDate dt = get();

        return daysInMonth(dt.month, isLeap(dt.year, m_bGregorianCalendar));
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public int daysInYear() {
        CalendarDate dt = get();

        if (isLeap(dt.year, m_bGregorianCalendar)) {
            return 366;
        } else {
            return 365;
        }
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public double dayOfYear() {
        CalendarDate dt = get();

        return dayOfYear(m_dblJulian, dt.year, afterPapalReform(dt.year, 1, 1));
    }

    /**
     * Method description
     *
     *
     * @param JD
     * @param Year
     * @param bGregorianCalendar
     *
     * @return
     */
    public double dayOfYear(double JD, int Year, boolean bGregorianCalendar) {
        return JD - dateToJD(Year, 1, 1, bGregorianCalendar) + 1;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public double fractionalYear() {
        CalendarDate dt = get();
        int DaysInYear;

        if (isLeap(dt.year, m_bGregorianCalendar)) {
            DaysInYear = 366;
        } else {
            DaysInYear = 365;
        }

        return dt.year + ((m_dblJulian - dateToJD(dt.year, 1, 1, afterPapalReform(dt.year, 1, 1))) / DaysInYear);
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public boolean leap() {
        return isLeap(year(), m_bGregorianCalendar);
    }

    /**
     * Method description
     *
     *
     * @param DayOfYear
     * @param bLeap
     */
    public void dayOfYearToDayAndMonth(int DayOfYear, boolean bLeap) {
        CalendarDate dt = new CalendarDate();
        int K = bLeap
                ? 1
                : 2;

        dt.month = (int) (9 * (K + DayOfYear) / 275.0 + 0.98);

        if (DayOfYear < 32) {
            dt.month = 1;
        }

        dt.day = DayOfYear - (int) ((275 * dt.month) / 9.0) + (K * (int) ((dt.month + 9) / 12.0)) + 30;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public double julian() {
        return m_dblJulian;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public boolean inGregorianCalendar() {
        return m_bGregorianCalendar;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public java.util.Date toDate() {
        return get().toDate();
    }


}



