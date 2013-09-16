package net.audumla.spacetime;

import net.audumla.spacetime.astrological.SunriseSunsetCalculator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Time {
    public static DateFormat dateFormatter = new SimpleDateFormat("yyyy MMM dd");
    private static Date now;

    static public Date getToday() {
        Calendar c = newCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime(); // midnight, that's the first second of the day.
    }

    static public void unsetNow() {
        now = null;
    }

    static public Date getNow() {
        return now != null ? now : new Date();
    }

    static public void setNow(Date n) {
        now = n;
    }

    static public Date offset(Date date, int hour, int minute, int sec) {
        Calendar c1 = newCalendar();
        c1.setTime(date);
        c1.add(Calendar.SECOND, sec);
        c1.add(Calendar.HOUR, hour);
        c1.add(Calendar.MINUTE, minute);
        return c1.getTime();
    }

    static public Date offset(Date date, Date offset) {
        Calendar c1 = newCalendar();
        c1.setTime(date);
        Calendar c2 = newCalendar();
        c2.setTime(offset);
        c1.add(Calendar.SECOND, c2.get(Calendar.SECOND));
        c1.add(Calendar.HOUR, c2.get(Calendar.HOUR));
        c1.add(Calendar.MINUTE, c2.get(Calendar.MINUTE));
        return c1.getTime();
    }

    static public Date getDayAndYear(Date date) {
        Calendar c1 = newCalendar();
        c1.setTime(new Date(0));
        Calendar c2 = newCalendar();
        c2.setTime(date);
        c1.set(Calendar.YEAR, c2.get(Calendar.YEAR));
        c1.set(Calendar.DAY_OF_YEAR, c2.get(Calendar.DAY_OF_YEAR));
        c1.set(Calendar.HOUR, 0);
        return c1.getTime();
    }

    static public Date getMonthAndYear(Date date) {
        Calendar c1 = newCalendar();
        c1.setTime(new Date(0));
        Calendar c2 = newCalendar();
        c2.setTime(date);
        c1.set(Calendar.YEAR, c2.get(Calendar.YEAR));
        c1.set(Calendar.MONTH, c2.get(Calendar.MONTH));
        c1.set(Calendar.HOUR, 0);
        return c1.getTime();
    }

    static public Date getMonth(Date date) {
        Calendar c1 = newCalendar();
        c1.setTime(new Date(0));
        Calendar c2 = newCalendar();
        c2.setTime(date);
        c1.set(Calendar.MONTH, c2.get(Calendar.MONTH));
        return c1.getTime();
    }

    public static Date getZeroDate() {
        Calendar c1 = newCalendar();
        c1.set(Calendar.MILLISECOND, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.HOUR, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.DAY_OF_YEAR, 1);
//		c1.set(Calendar.YEAR, 0);
        return c1.getTime();
    }

    public static Date setNullYear(Date time) {
        Calendar c1 = newCalendar();
        c1.setTime(time);
        c1.set(Calendar.YEAR, 1970);
        return c1.getTime();
    }

    public static Date setNullDay(Date time) {
        Calendar c1 = newCalendar();
        c1.setTime(time);
        c1.set(Calendar.DAY_OF_YEAR, 1);
        return c1.getTime();
    }

    public static Date getSunrise(Date now, double llat, double llong) {
        Calendar c = newCalendar();
        c.setTime(now);
        TimeZone tz = TimeZone.getDefault();
        SunriseSunsetCalculator sc = new SunriseSunsetCalculator(new Location(llat, llong,0.0), tz);
        return sc.getOfficialSunriseCalendarForDate(c).getTime();
    }

    public static Date getSunset(Date now, double llat, double llong) {
        Calendar c = newCalendar();
        c.setTime(now);
        TimeZone tz = TimeZone.getDefault();
        SunriseSunsetCalculator sc = new SunriseSunsetCalculator(new Location(llat, llong,0.0), tz);
        return sc.getOfficialSunsetCalendarForDate(c).getTime();
    }

    public static Calendar newCalendar() {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getDefault());
        cal.setTime(getNow());
        return cal;
    }

    public static boolean hasYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return !((1970 == c.get(Calendar.YEAR)) && (0 == c.get(Calendar.MILLISECOND)));
    }

    public static boolean hasDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return !(((1970 == c.get(Calendar.YEAR)) && (0 == c.get(Calendar.MILLISECOND))) && (1 == c.get(Calendar.DAY_OF_YEAR)));
    }

}
