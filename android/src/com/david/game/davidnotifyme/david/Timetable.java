package com.david.game.davidnotifyme.david;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.david.game.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Timetable {
    public ArrayList<String> times;
    int skupinaNem;
    int skupinaOdp;
    int skupinaEtvNbv;
    int[] breaks = {5, 10, 20, 5, 10, 10, 5, 0};
    ArrayList<ArrayList<String>> timetable;


    public Timetable(Context context) {
        times = new ArrayList<>();
        timetable = new ArrayList<>();
        initGroups(context);
        parseTimetableJson(context);
    }

    public static int stringTimeToMinutes(String stringTime) {
        int[] s = Arrays.stream(stringTime.split(":")).mapToInt(Integer::parseInt).toArray();
        return s[0] * 60 + s[1];
    }

    public void initGroups(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("nemcina", pref.getString("nemcina", "Prvá"));

        skupinaNem = pref.getString("nemcina", "Prvá").equals("Prvá") ? 1 : 2;

        String prax = pref.getString("odp", "Prvá");

        if (prax.equals("Prvá")) skupinaOdp = 1;
        else if (prax.equals("Druhá")) skupinaOdp = 2;
        else skupinaOdp = 3;

        skupinaEtvNbv = pref.getString("etv_nbv", "Etika").equals("Etika") ? 1 : 2;
    }

    private void parseTimetableJson(Context context) {
        String rawJsonData = JSONparser.getFileData(context, R.raw.timetable);
        JSONObject obj = null;
        try {
            obj = new JSONObject(rawJsonData);
            JSONArray array = obj.getJSONArray("timetable");

            for (int i = 0; i < array.length(); i++) {
                JSONArray innerArray = array.getJSONArray(i);

                ArrayList<String> dayArray = new ArrayList<>();
                for (int j = 0; j < innerArray.length(); j++) {
                    dayArray.add(innerArray.getString(j));
                }
                timetable.add(dayArray);
            }
            JSONArray timesJSON = obj.getJSONArray("times");
            for (int i = 0; i < timesJSON.length(); i++) {
                times.add(timesJSON.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("timetable", timetable.toString());
    }

    public Pair<String, String> getNextClass() {
        int day = new Date().getDay();
        Log.d("getNextClass: " , day + " ");
        if (day == 0 || day == 6) return new Pair<>(null, "víkend");

        int index = getClassIndexBasedOnCurrentTime();
        //Log.d("index", index + " ");
        if (index != -1) {

            ArrayList<String> lesson = new ArrayList<>(
                    Arrays.asList(timetable.get(day - 1).get(index).split("/")));

            if (lesson.size() == 1) { //normálka
                lesson = new ArrayList<>(Arrays.asList(lesson.get(0).split(":")));
            } else { // delené hodiny

                if (lesson.get(0).contains("ETV"))
                    lesson = new ArrayList<>(Arrays.asList(lesson.get(skupinaEtvNbv - 1).split(":")));

                else if (lesson.size() == 3)
                    lesson = new ArrayList<>(Arrays.asList(lesson.get(skupinaOdp - 1).split(":")));

                else
                    lesson = new ArrayList<>(Arrays.asList(lesson.get(skupinaNem - 1).split(":")));

            }

            return new Pair<>(lesson.get(0), lesson.get(1));
        }

        return new Pair<>(null, "voľno");
    }

    public int getClassIndexBasedOnCurrentTime() {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        int currentTime = stringTimeToMinutes(format.format(new Date()));
        Log.d("debugging:::", currentTime + " " + format.format(new Date()));
        for (int i = 0; i < times.size(); i++) {
            String time = times.get(i);
            String[] timeSpan = time.split("-");

            int timeInMinutesStart = stringTimeToMinutes(timeSpan[0]);
            int timeInMinutesEnd = stringTimeToMinutes(timeSpan[1]);

            if (currentTime < 8 * 60) {
                return 0;
            }
            if (timeInMinutesStart <= currentTime) {
                try {
                    if (timeInMinutesEnd >= currentTime || timeInMinutesEnd + breaks[i] >= currentTime) {
                        return i + 1;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
        return -1;
    }

    public int getIndexOfCurrentLesson() { // -1 nenajdené , -2 prestávka
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        int currentTime = stringTimeToMinutes(format.format(new Date()));

        for (int i = 0; i < times.size(); i++) {
            String time = times.get(i);
            String[] timeSpan = time.split("-");

            int timeInMinutesStart = stringTimeToMinutes(timeSpan[0]);
            int timeInMinutesEnd = stringTimeToMinutes(timeSpan[1]);

            if (currentTime < 8 * 60) {
                return 0;
            }
            if (timeInMinutesStart <= currentTime && timeInMinutesEnd >= currentTime) {
                return i;
            } else if (timeInMinutesStart <= currentTime && timeInMinutesEnd + breaks[i] >= currentTime) {
                return -2;
            }
        }
        return -1;
    }

    public String getEndOfCurrentLesson() {
        int index = getIndexOfCurrentLesson();
        if (index >= 0) {
            String time = times.get(index);
            return time.substring(time.indexOf("-") + 1);
        }
        return "00:00";
    }

    public int minutesTillEndOfLesson(@Nullable Integer lessonIndex) {
        if (lessonIndex == null)
            lessonIndex = getIndexOfCurrentLesson();
        else if (lessonIndex < 0) {
            Log.e("minutesTillEndOFlesson", "zly index!!");
            lessonIndex = getIndexOfCurrentLesson();
        }

        String time = times.get(lessonIndex);
        String current = DavidClockUtils.currentTimeInString();

        return Math.abs(DavidClockUtils.timeToMinutes(current) - DavidClockUtils.timeToMinutes(time));
    }


    public String getCurrentLesson() {
        String[] lessonsToday = getLessonsToday();

        if (lessonsToday.length > 0) { //nie je víkend
            int lessonIndex = getIndexOfCurrentLesson();
            if (lessonIndex >= 0) {
                return lessonsToday[lessonIndex];
            } else if (lessonIndex == -2) {
                return "prestávka";
            }
        }
        return "voľno";
    }


    public String[] getLessonsToday() {
        Date date = new Date();
        int day = date.getDay();
        if (day == 0 || day == 6) return new String[0];

        ArrayList<String> timetableToday = timetable.get(day - 1);
        String[] lessons = new String[timetableToday.size()];

        for (int i = 0; i < timetableToday.size(); i++) {

            String lesson = timetableToday.get(i);
            if (lesson.contains("/")) {
                String[] hodinaSkupiny = lesson.split("/");
                if (hodinaSkupiny.length == 2) lesson = hodinaSkupiny[skupinaNem - 1];
                else if (hodinaSkupiny.length == 3) lesson = hodinaSkupiny[skupinaOdp - 1];
            }

            lessons[i] = lesson.substring(lesson.indexOf(":") + 1);
        }
        return lessons;
    }
}
