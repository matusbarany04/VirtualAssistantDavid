package com.david.game.davidnotifyme.david;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.david.game.R;
import com.david.game.davidnotifyme.edupage.Edupage;
import com.david.game.davidnotifyme.edupage.TimetableParser;
import com.david.game.davidnotifyme.edupage.readers.TimetableReader;
import com.david.game.davidnotifyme.edupage.timetable_objects.Subject;
import com.david.game.davidnotifyme.utils.JSONparser;

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
    ArrayList<TimetableParser.Day> timetable;
    private OnLoadListener onLoadListener;
    private boolean loaded = false;


    public Timetable(Context context) {
        times = new ArrayList<>();
     //   timetable = new ArrayList<>();
        initGroups(context);

        TimetableReader timetableReader = new TimetableReader(context);

        if(David.maPristupKInternetu(context) || timetableReader.isEmpty()) {

            try {
                Edupage edupage = new Edupage(context);//.scrape("smh");
                edupage.setOnCompletionListener(new Edupage.OnCompletionListener() {

                    @Override
                    public void onComplete(ArrayList<TimetableParser.Day> timetable) {
                        Timetable.this.timetable = timetable;
                        if(onLoadListener != null) onLoadListener.onLoadTimetable(Timetable.this);
                        loaded = true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            timetable = timetableReader.read().getTimetableArray();
            loaded = true;
        }
        //parseTimetableJson(context);
    }

    public interface OnLoadListener {
        void onLoadTimetable(Timetable timetable);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        onLoadListener = listener;
        if(loaded) {
            onLoadListener.onLoadTimetable(this);
        }
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

    @Deprecated
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
               // timetable.add(dayArray);
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
        ArrayList<Subject> timeTableToday = getSubjectsToday();

        if (index != -1 && index < timeTableToday.size()) {

            Subject lesson = timeTableToday.get(index);

            return new Pair<>(lesson.subjectName, lesson.getClassroomNumber());

            /*ArrayList<String> lesson = new ArrayList<>(Arrays.asList(timeTableToday.get(index).split("/")));

            if (lesson.size() == 1) { //normálka
                lesson = new ArrayList<>(Arrays.asList(lesson.get(0).split(":")));
            } else { // delené hodiny

                if (lesson.get(0).contains("ETV"))
                    lesson = new ArrayList<>(Arrays.asList(lesson.get(skupinaEtvNbv - 1).split(":")));

                else if (lesson.size() == 3)
                    lesson = new ArrayList<>(Arrays.asList(lesson.get(skupinaOdp - 1).split(":")));

                else
                    lesson = new ArrayList<>(Arrays.asList(lesson.get(skupinaNem - 1).split(":")));

            }*/
        }

        return new Pair<>(null, "voľno");
    }

    public int getClassIndexBasedOnCurrentTime() {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        ArrayList<Subject> subjects = getSubjectsToday();
        int currentTime = stringTimeToMinutes(format.format(new Date()));
        Log.d("debugging:::", currentTime + " " + format.format(new Date()));
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);

            int timeInMinutesStart = stringTimeToMinutes(subject.getStart());
            int timeInMinutesEnd = stringTimeToMinutes(subject.getEnd());

            if (timeInMinutesStart <= currentTime || !getLessonsToday(false)[i].equals("-")) {
                try {
                    if (timeInMinutesEnd >= currentTime || timeInMinutesEnd + breaks[i] >= currentTime) {
                        int firstLesson = DavidClockUtils.timeToMinutes(getBeginOfFirstLesson());
                        return currentTime < firstLesson ? i : i + 1;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
        return -1;
    }

    public String getBeginOfFirstLesson() {
        ArrayList<Subject> lessons = getSubjectsToday();
        if(lessons.size() > 0) {

            for (Subject lesson : lessons) {
                if(!lesson.shortName.equals("-")) {
                    return lesson.getStart();
                }
            }
        }
        return "Dneska nie sú žiadne hodiny";
    }

    public String getEndOfAllLessons() {
        ArrayList<Subject> lessons = getSubjectsToday();

        if(lessons.size() > 0) {
            return lessons.get(lessons.size() - 1).getEnd();
        }
        else return "Dneska nie sú žiadne hodiny";
    }

    public boolean freeTime() {
        if(DavidClockUtils.jeVikend()) return true;

        try {
            Date date = new Date();
            if (date.getHours() >= 23) return false;

            int minutesEnd = stringTimeToMinutes(getEndOfAllLessons());
            return DavidClockUtils.currentTimeInMinutes() > minutesEnd;

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public int getIndexOfCurrentLesson() { // -1 nenajdené , -2 prestávka, -3 vyučovanie nezačalo
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        int currentTime = stringTimeToMinutes(format.format(new Date()));

        ArrayList<Subject> lessons = getSubjectsToday();

        for (int i = 0; i < lessons.size(); i++) {

            Subject subject = lessons.get(i);

            int timeInMinutesStart = stringTimeToMinutes(subject.getStart());
            int timeInMinutesEnd = stringTimeToMinutes(subject.getEnd());

            if (i == 0 && currentTime < timeInMinutesStart) {
                return -3;
            }

            if (timeInMinutesStart <= currentTime && timeInMinutesEnd >= currentTime) {
                return i;

            } else if (timeInMinutesStart < currentTime && timeInMinutesEnd + breaks[i] > currentTime) {
                return -2;
            }
        }
        return -1;
    }

    public ArrayList<Subject> getSubjectsToday() {
        int day = DavidClockUtils.zistiDen();
        return timetable.get(day).getSubjectsArray();
    }

    public String getEndOfCurrentLesson() {
        Subject subject = getCurrentLesson();
        if (subject != null) {
            return subject.getEnd();
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


    public String getCurrentLessonName() {
        String[] lessonsToday = getLessonsToday(false);

        if (lessonsToday.length > 0) { //nie je víkend
            int lessonIndex = getIndexOfCurrentLesson();

            Log.d("lessons", lessonIndex + "");
            if (lessonIndex >= 0) {
                return lessonsToday[lessonIndex];
            } else if (lessonIndex == -2) {
                return "prestávka";
            }
        }
        return "voľno";
    }

    public Subject getCurrentLesson() {
        ArrayList<Subject> lessonsToday = getSubjectsToday();
        int lessonIndex = getIndexOfCurrentLesson();
        if (lessonsToday.size() > 0 && lessonIndex >= 0) { //nie je víkend
            return lessonsToday.get(lessonIndex);
        }
        return null;
    }

    public String[] getLessonsToday(boolean shortNames) {
        Date date = new Date();
        int day = date.getDay();
        if (day == 0 || day == 6 || timetable == null) return new String[0];

        ArrayList<Subject> timetableToday = timetable.get(day - 1).getSubjectsArray();
        String[] lessons = new String[timetableToday.size()];

        for (int i = 0; i < timetableToday.size(); i++) {
            lessons[i] = shortNames ? timetableToday.get(i).shortName : timetableToday.get(i).subjectName;
        }
        return lessons;
    }
}
