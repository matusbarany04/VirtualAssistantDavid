package com.david.game.davidnotifyme.david;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DavidClockUtils {

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static String currentTimeInStringWithSeconds() {
        Date date = new Date();
        return format.format(date);
    }

    public static int timeToMinutes(String time) {
        String[] list = time.split(":");
        return Integer.parseInt(list[0]) * 60 + Integer.parseInt(list[1]);
    }

    public static String minutesToTime(int fullMinutes) {
        int hours = fullMinutes / 60;
        int minutes = fullMinutes - (hours * 60);
        return hours + ":" + minutes;
    }

    public static int timeToMillis(String time) {
        String[] list = time.split(":");
        return (Integer.parseInt(list[0]) * 60 + Integer.parseInt(list[1])) * 60 * 100;
    }

    public static boolean afterEleven() {
        Date date = new Date();
        return date.getHours() >= 23;
    }

    public static boolean beforeThree() {
        Date date = new Date();
        return date.getHours() < 3;
    }

    public static int millisFromNowTill(String tillTime) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        try {

            Date tillDate = formatter.parse(tillTime);
            assert tillDate != null;
            long tillDateMillis = (((((tillDate.getHours() * 60L) + tillDate.getMinutes()) * 60) + tillDate.getSeconds()) * 1000);

            Date date2 = Calendar.getInstance().getTime();
            long timeMillisNow = (((((date2.getHours() * 60L) + date2.getMinutes()) * 60) + date2.getSeconds()) * 1000);

            return (int) (tillDateMillis - timeMillisNow);
        } catch (ParseException e) {
            e.printStackTrace();
            return (5 * 60 * 1000); // if time is wrong try five minutes later
        }
    }

    public static String currentTimeInString() {
        Date date = new Date();
        Log.d("date.getHours()", new SimpleDateFormat("HH").format(date) + " " );
        return  Integer.parseInt(new SimpleDateFormat("HH").format(date)) + ":"+ date.getMinutes();
    }

    public static int currentTimeInMinutes() {
        Date date = new Date();
        return Integer.parseInt(new SimpleDateFormat("HH").format(date)) * 60 + date.getMinutes();
    }

    public static int zistiDen() {
        int day = new Date().getDay();

        return day == 0 ? 6 : day;
    }

    public static boolean jeVikend() {
        Date date = new Date();
        return date.getDay() == 0 || date.getDay() == 6;
    }

    public static String zistiCas() {
        return format.format(new Date());
    }


    public static String getLastWeek(Calendar mCalendar) {
        // Monday
        mCalendar.add(Calendar.DAY_OF_YEAR, -13);
        Date mDateMonday = mCalendar.getTime();

        // Sunday
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        Date mDateSunday = mCalendar.getTime();

        // Date format
        String strDateFormat = "dd MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String MONDAY = sdf.format(mDateMonday);
        String SUNDAY = sdf.format(mDateSunday);

        // Substring
        if ((MONDAY.substring(3, 6)).equals(SUNDAY.substring(3, 6))) {
            MONDAY = MONDAY.substring(0, 2);
        }

        return MONDAY + " - " + SUNDAY;
    }

    public static String getCurrentWeek(Calendar mCalendar) {
        Date date = new Date();
        mCalendar.setTime(date);

        // 1 = Sunday, 2 = Monday, etc.
        int day_of_week = mCalendar.get(Calendar.DAY_OF_WEEK);

        int monday_offset;
        if (day_of_week == 1) {
            monday_offset = -6;
        } else
            monday_offset = (2 - day_of_week); // need to minus back
        mCalendar.add(Calendar.DAY_OF_YEAR, monday_offset);

        Date mDateMonday = mCalendar.getTime();

        // return 6 the next days of current day (object cal save current day)
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        Date mDateSunday = mCalendar.getTime();

        //Get format date
        String strDateFormat = "dd MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String MONDAY = sdf.format(mDateMonday);
        String SUNDAY = sdf.format(mDateSunday);

        // Sub String
        if ((MONDAY.substring(3, 6)).equals(SUNDAY.substring(3, 6))) {
            MONDAY = MONDAY.substring(0, 2);
        }

        return MONDAY + " - " + SUNDAY;
    }

    public static String getNextWeek(Calendar mCalendar) {
        // Monday
        mCalendar.add(Calendar.DAY_OF_YEAR, 1);
        Date mDateMonday = mCalendar.getTime();

        // Sunday
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        Date Week_Sunday_Date = mCalendar.getTime();

        // Date format
        String strDateFormat = "dd MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String MONDAY = sdf.format(mDateMonday);
        String SUNDAY = sdf.format(Week_Sunday_Date);

        // Sub string
        if ((MONDAY.substring(3, 6)).equals(SUNDAY.substring(3, 6))) {
            MONDAY = MONDAY.substring(0, 2);
        }

        return MONDAY + " - " + SUNDAY;
    }

}
