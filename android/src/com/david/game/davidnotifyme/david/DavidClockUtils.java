package com.david.game.davidnotifyme.david;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;

import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
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
        Log.d("date.getHours()", new SimpleDateFormat("HH").format(date) + " ");
        return Integer.parseInt(new SimpleDateFormat("HH").format(date)) + ":" + date.getMinutes();
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


    public static Pair<String,String> getLastWeek() {
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Print dates of the current week starting on Sunday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = "", endDate = "";
        calendar.add(Calendar.DATE, -7);
        startDate = df.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        endDate = df.format(calendar.getTime());

        return new Pair<>(startDate, endDate);
    }

    public static Pair<String,String> getNextWeek() {
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Print dates of the current week starting on Sunday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = "", endDate = "";
        calendar.add(Calendar.DATE, 7 * 2);
        startDate = df.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        endDate = df.format(calendar.getTime());

        return new Pair<>(startDate, endDate);
    }

    public static Pair<String,String> getCurrentWeek() {
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Print dates of the current week starting on Sunday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = "", endDate = "";
        calendar.add(Calendar.DATE, 7);
        startDate = df.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        endDate = df.format(calendar.getTime());

        return new Pair<>(startDate, endDate);
    }

    public static String[] getCurrentWeekDates() {
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Print dates of the current week starting on Sunday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = "", endDate = "";
        calendar.add(Calendar.DATE, 7);   // nezabudnut to odkomentovať
         // calendar.add(Calendar.DATE, -7);   // nezabudnut to odkomentovať

        String[] dates = new String[7];
        for (int i = 0; i < 7; i++){
            dates[i] =  df.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }

       return dates;
    }

}
