package com.david.notify.davidnotifyme.david;
import static com.david.notify.davidnotifyme.david.DavidClockUtils.timeToMinutes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.david.notify.R;
import com.david.notify.davidnotifyme.david.lunch.LunchCallback;
import com.david.notify.davidnotifyme.david.lunch.LunchFetcher;
import com.david.notify.davidnotifyme.david.lunch.Result;
import com.david.notify.davidnotifyme.edupage.TimetableParser;
import com.david.notify.davidnotifyme.edupage.timetable_objects.Subject;

//asi to uz ide
public class David {
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private Timetable timetable;
    private LunchFetcher lunch;
    private SchoolNavigator navigator;
    private OnObedNajdenyNacuvadlo obedoveNacuvadlo;

    //  new Lunch(new LunchFetcher(this,executor)).getNextClosestLunch();
    public David(Context context, @Nullable Executor executor) {
        if (executor == null) {
            executor = runnable -> new Thread(runnable).start();
        }

        this.lunch = new LunchFetcher(context, executor);

        timetable = new Timetable(context);

        navigator = new SchoolNavigator(context);
    }

    public static boolean maPristupKInternetu(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //unused
    public static int findNearestNotificationChange(@Nullable String startTime) {
        if (startTime == null) {
            Date date = new Date();
            startTime = date.getHours() + ":" + date.getMinutes();
        }

        //int[] breaks = {0, 5, 10, 20, 5, 10, 10, 5};
        String[] timeArray = {"7:55", "8:45", "9:35", "10:30", "11:35", "12:25", "13:20", "14:15", "15:05"};
        int[] timeArrayMinutes = Arrays.stream(timeArray).mapToInt(DavidClockUtils::timeToMinutes).toArray();
        int startTimeMinutes = timeToMinutes(startTime);

        if (startTimeMinutes > timeArrayMinutes[timeArrayMinutes.length - 1] ||
                startTimeMinutes < timeArrayMinutes[0]) {
            return timeArrayMinutes[0];
        }

        for (int i = 0; i < timeArray.length - 1; i++) {
            if (timeArrayMinutes[i] <= startTimeMinutes &&
                    timeArrayMinutes[i + 1] > startTimeMinutes) {
                return timeArrayMinutes[i + 1];
            }
        }
        return 427;
    }

    public boolean prebiehaHodina() {
        int index = timetable.getIndexOfCurrentLesson();
        return index >= 0;
    }

    public boolean bliziSaKoniecHodiny(){
        if(timetable.getIndexOfCurrentLesson() == -2) return false;
        int timeToEnd = DavidClockUtils.timeToMinutes(timetable.getEndOfCurrentLesson()) - DavidClockUtils.currentTimeInMinutes();
        return timeToEnd <= 5;
    }

    public static ArrayList<Long> ziskajCasyAktualizacie(Context context, Timetable timetable) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String startTime = prefs.getString("time_on", "7:30");                      //hardcodnute hodnoty !!!
        String endTime = prefs.getString("time_off", "15:30");
        int aheadTime = Integer.parseInt(prefs.getString("time_ahead", "5"));

        ArrayList<String> timeArray = new ArrayList<>();

        TimetableParser.Day day = timetable.getCurrentDay();
        ArrayList<Subject> subjects = day.getSubjectsArray();

        Log.d("subjects", subjects.toString());

        timeArray.add(startTime);

        for (int i = 0; i < subjects.size(); i++) {
            if (i == 0) { // checking for first day time margin
                timeArray.add(DavidClockUtils.minutesToTime( timeToMinutes(subjects.get(i).getStart()) - aheadTime));
            }
            timeArray.add(subjects.get(i).getStart());
            timeArray.add(DavidClockUtils.minutesToTime( timeToMinutes(subjects.get(i).getEnd()) - aheadTime));
        }
        timeArray.add(endTime);

        for (String time : timeArray) {
            Log.d("timeArray", time + " ");
        }
        ArrayList<Long> output = new ArrayList<>();
        String myDate =  DavidClockUtils.currentTimeInString();


        for (String s : timeArray) {
            if ( DavidClockUtils.timeToMillis(s) >  DavidClockUtils.timeToMillis(myDate)) { //mozno mozny mozno opraveny problemik
                Log.d("time", s + " -> " + DavidClockUtils.millisFromNowTill(s));
                output.add((long)  DavidClockUtils.millisFromNowTill(s));
            }
        }
        return output;
    }

    public static boolean skontrolujCas(String time) {
        //        Log.d("minutes skontrolujCas",  time.length() + "length" + time.indexOf(':') + "index");
        String[] list = time.split(":");

        if (list[0].length() > 2 || list[1].length() != 2) return false;

        try {
            int h = Integer.parseInt(list[0]);
            int m = Integer.parseInt(list[1]);

            if (h < 0 || h > 23 || m < 0 || m > 59) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static String ziskajRannuSpravu(Context context, Timetable timetable) {
        StringBuilder text = new StringBuilder(context.getString(R.string.zbalene_veci));

        String[] lessons = timetable.getLessonsToday(true);
        for (String lesson : lessons) {
            if (trebaPomocky(lesson) && !lesson.equals("-") && !text.toString().contains(lesson + ","))
                text.append(lesson).append(", ");
        }
        text.replace(text.length() - 2, text.length(), ".");
        return text.toString();
    }

    private static boolean trebaPomocky(String hodina) {
        String[] hodinyBezPomocok = {"OBED", "TSV", "NBV"};
        for (String hodinaBezPomocok : hodinyBezPomocok) {
            if (hodina.equalsIgnoreCase(hodinaBezPomocok)) return false;
        }
        return true;
    }

    public Timetable ziskajRozvrh() {
        return timetable;
    }

    public Timetable ziskajNovyRozvrh(Context context) {
        timetable = new Timetable(context);
        return timetable;
    }

    public David zistiNovyObed() {
        lunch.makeLunchFetchRequest(new LunchCallback<ArrayList<String>>() {

            @Override
            public String onComplete(Result<ArrayList<String>> result) {
                if (result instanceof Result.Success) {
                    ArrayList<String> data = ((Result.Success<ArrayList<String>>) result).data;

                    if (obedoveNacuvadlo != null) obedoveNacuvadlo.onObedNajdeny(data);
                }
                return null;
            }
        });
        return this;
    }

    public void nastavObedoveNacuvadlo(OnObedNajdenyNacuvadlo nacuvadlo) {
        obedoveNacuvadlo = nacuvadlo;
    }

    public Pair<String, String> ziskajDalsiuHodinuEdupage(@Nullable Boolean verbose) {

        Pair<String, String> input = timetable.getNextClass();  // first : učebňa, second - názov hodiny
        String header = "";
        String text = "";
        if (input.first != null){

            if(timetable.getIndexOfCurrentLesson() == -3) {
                header = "Prvá hodina je " + input.first;
                text = "Vyučovanie začína " + timetable.getBeginOfFirstLesson();

            } else {

                if(input.first.equalsIgnoreCase("OBED")) {
                    header = "Nasleduje obed. Dobrú chuť !";
                    text = "Zisťujem čo je na obed...";
                    verbose = null;

                } else if (input.second != null) {
                    header = "Ďalšia hodina je " + input.first;
                    text = "Učebňa " + input.second;

                } else if (input.second.equals("víkend")) {
                    header = "Je víkend";

                } else header = "Máš voľno :)";

                if (verbose != null && Boolean.TRUE && input.first != null) {
                    text += " (" + navigator.whereIs(input.second) + ")";
                }
            }

        }else
        {
            header = "došlo k chybe :/ ";
            text = "niečo sa pokazilo";
        }


        return new Pair<>(header, text);
    }

    public Pair<String, String> ziskajPrebiehajucuHodinu() {

        String lesson = timetable.getCurrentLessonName();

        if(lesson.equals("voľno")) return new Pair<>("Pravdepobne voľno", "");

        String header = "Aktuálne prebieha " + lesson;

        if(lesson.equals("-")) header = "Voľná hodina";

        String text = "hodina končí " + timetable.getEndOfCurrentLesson();

        if(lesson.equals("OBED")) {
            header = "Prebieha obed. Dobrú chuť !";
            text = "Zisťujem čo je na obed...";
        }

        return new Pair<>(header, text);
    }

    public String zistiSuplovanie() {
        return "Prepáč, toto ma Matúš ešte nenaučil";
    }

    public String zistiPisomky() {
        return "Prepáč, toto ma Matúš ešte nenaučil";
    }

    public interface OnObedNajdenyNacuvadlo {
        void onObedNajdeny(ArrayList<String> data);
    }
}
