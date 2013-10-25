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


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CAADate {

    //Member variables
    protected double m_dblJulian; //Julian day number for this date
    protected boolean m_bGregorianCalendar; //Is this date in the Gregorian calendar

    public CAADate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        Set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR),c.get(Calendar.MINUTE),c.get(Calendar.SECOND),true);
    }

    public CAADate() {
        this.m_dblJulian = 0;
        this.m_bGregorianCalendar = false;
    }

    public CAADate(int Year, int Month, double Day, boolean bGregorianCalendar) {
        Set(Year, Month, Day, 0, 0, 0, bGregorianCalendar);
    }

    public CAADate(int Year, int Month, double Day, double Hour, double Minute, double Second, boolean bGregorianCalendar) {
        Set(Year, Month, Day, Hour, Minute, Second, bGregorianCalendar);
    }

    public CAADate(double JD, boolean bGregorianCalendar) {
        Set(JD, bGregorianCalendar);
    }

    public static boolean AfterPapalReform(int Year, int Month, double Day) {
        return ((Year > 1582) || ((Year == 1582) && (Month > 10)) || ((Year == 1582) && (Month == 10) && (Day >= 15)));
    }

    public static boolean AfterPapalReform(double JD) {
        return (JD >= 2299160.5);
    }

    public static double DateToJD(int Year, int Month, double Day, boolean bGregorianCalendar) {
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

    public static boolean IsLeap(int Year, boolean bGregorianCalendar) {
        if (bGregorianCalendar) {
            if ((Year % 100) == 0)
                return ((Year % 400) == 0);
            else
                return ((Year % 4) == 0);
        } else
            return ((Year % 4) == 0);
    }

    public static net.audumla.astronomical.algorithims.CAACalendarDate JulianToGregorian(int Year, int Month, int Day) {
        CAADate date = new CAADate(Year, Month, Day, false);
        date.SetInGregorianCalendar(true);

        return date.Get();
    }

    public static net.audumla.astronomical.algorithims.CAACalendarDate GregorianToJulian(int Year, int Month, int Day) {
        CAADate date = new CAADate(Year, Month, Day, true);
        date.SetInGregorianCalendar(false);
        return date.Get();
    }

    public void Set(int Year, int Month, double Day, double Hour, double Minute, double Second, boolean bGregorianCalendar) {
        double dblDay = Day + (Hour / 24) + (Minute / 1440) + (Second / 86400);
        Set(DateToJD(Year, Month, dblDay, bGregorianCalendar), bGregorianCalendar);
    }

    public CAACalendarDate Get() {
        CAACalendarDate dt = new CAACalendarDate();
        double JD = m_dblJulian + 0.5;
        double tempZ = Math.floor(JD);
        double F = JD - tempZ;
        int Z = (int) (tempZ);
        int A;

        if (m_bGregorianCalendar) //There is a difference here between the Meeus implementation and this one
        //if (Z >= 2299161)       //The Meeus implementation automatically assumes the Gregorian Calendar
        //came into effect on 15 October 1582 (JD: 2299161), while the CAADate
        //implementation has a "m_bGregorianCalendar" value to decide if the date
        //was specified in the Gregorian or Julian Calendars. This difference
        //means in effect that CAADate fully supports a propalactive version of the
        //Julian calendar. This allows you to construct Julian dates after the Papal
        //reform in 1582. This is useful if you want to construct dates in countries
        //which did not immediately adapt the Gregorian calendar
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

    public void Set(double JD, boolean bGregorianCalendar) {
        m_dblJulian = JD;
        SetInGregorianCalendar(bGregorianCalendar);
    }

    public void SetInGregorianCalendar(boolean bGregorianCalendar) {
        boolean bAfterPapalReform = (m_dblJulian >= 2299160.5);
        m_bGregorianCalendar = bGregorianCalendar && bAfterPapalReform;
    }

    public int Day() {
        return Get().day;
    }

    public int Month() {
        return Get().month;
    }

    public int Year() {
        return Get().year;
    }

    public int Hour() {
        return Get().hour;
    }

    public int Minute() {
        return Get().minute;
    }

    public double Second() {
        return Get().second;
    }

    public CAADate.DAY_OF_WEEK DayOfWeek() {
        return DAY_OF_WEEK.getEnum((int) ((m_dblJulian + 1.5) % 7));
    }

    public int DaysInMonth(int Month, boolean bLeap) {
        assert Month >= 1 && Month <= 12;

        int[] MonthLength = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (bLeap) {
            MonthLength[1]++;
        }

        return MonthLength[Month - 1];
    }

    public int DaysInMonth() {
        CAACalendarDate dt = Get();
        return DaysInMonth(dt.month, IsLeap(dt.year, m_bGregorianCalendar));
    }

    public int DaysInYear() {
        CAACalendarDate dt = Get();
        if (IsLeap(dt.year, m_bGregorianCalendar)) {
            return 366;
        } else {
            return 365;
        }
    }

    public double DayOfYear() {
        CAACalendarDate dt = Get();
        return DayOfYear(m_dblJulian, dt.year, AfterPapalReform(dt.year, 1, 1));
    }

    public double DayOfYear(double JD, int Year, boolean bGregorianCalendar) {
        return JD - DateToJD(Year, 1, 1, bGregorianCalendar) + 1;
    }

    public double FractionalYear() {
        CAACalendarDate dt = Get();
        int DaysInYear;
        if (IsLeap(dt.year, m_bGregorianCalendar)) {
            DaysInYear = 366;
        } else {
            DaysInYear = 365;
        }

        return dt.year + ((m_dblJulian - DateToJD(dt.year, 1, 1, AfterPapalReform(dt.year, 1, 1))) / DaysInYear);
    }

    public boolean Leap() {
        return IsLeap(Year(), m_bGregorianCalendar);
    }

    public void DayOfYearToDayAndMonth(int DayOfYear, boolean bLeap) {
        CAACalendarDate dt = new CAACalendarDate();
        int K = bLeap ? 1 : 2;

        dt.month = (int) (9 * (K + DayOfYear) / 275.0 + 0.98);
        if (DayOfYear < 32) {
            dt.month = 1;
        }

        dt.day = DayOfYear - (int) ((275 * dt.month) / 9.0) + (K * (int) ((dt.month + 9) / 12.0)) + 30;
    }

    public double Julian() {
        return m_dblJulian;
    }

    public boolean InGregorianCalendar() {
        return m_bGregorianCalendar;
    }

    //Enums
    public enum DAY_OF_WEEK {
        SUNDAY(0),
        MONDAY(1),
        TUESDAY(2),
        WEDNESDAY(3),
        THURSDAY(4),
        FRIDAY(5),
        SATURDAY(6);
        private static java.util.HashMap<Integer, DAY_OF_WEEK> map = new java.util.HashMap<>();
        private int value;

        private DAY_OF_WEEK(int intValue) {
            value = intValue;
            getMap().put(intValue, this);
        }

        private static java.util.HashMap<Integer, DAY_OF_WEEK> getMap() {
            return map;
        }

        public static DAY_OF_WEEK getEnum(int intValue) {
            return getMap().get(intValue);
        }

        public int getValue() {
            return value;
        }
    }

    public Date toDate() {
        return Get().toDate();
    }
}