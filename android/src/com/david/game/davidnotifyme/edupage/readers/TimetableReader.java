package com.david.game.davidnotifyme.edupage.readers;

import android.content.Context;
import android.util.Log;

import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.david.Timetable;
import com.david.game.davidnotifyme.edupage.TimetableParser;
import com.david.game.davidnotifyme.edupage.timetable_objects.Subject;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;
import com.david.game.davidnotifyme.utils.JSONparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TimetableReader {

    private final Context context;
    private String jsonOutput;

    public TimetableReader(Context context) {
        this.context = context;
    }

    public TimetableReader read() {
        InternalStorageFile internalStorageFile = new InternalStorageFile(context, InternalFiles.TIMETABLE);

        jsonOutput = internalStorageFile.isEmpty() ? "" : internalStorageFile.read();
        Log.d("rawJSON", jsonOutput);
        return this;
    }

    public boolean isEmpty() {
        return jsonOutput.equals("");
    }

    public ArrayList<TimetableParser.Day> getTimetableArray() {

        try {
            JSONObject jsonObject = new JSONObject(jsonOutput);
            JSONArray jsonArray = jsonObject.getJSONArray("timetable");

            ArrayList<TimetableParser.Day> output = new ArrayList<>();
            String[] days = DavidClockUtils.getCurrentWeekDates();

            ArrayList<Subject> subjectArray;
            for (int i = 0; i < jsonArray.length(); i++) {
                subjectArray = jsonToSubjectArray(jsonArray.getJSONArray(i));

                TimetableParser.Day day = new TimetableParser.Day(days[i], subjectArray);
                output.add(day);
            }

            return output;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Subject> jsonToSubjectArray(JSONArray array) {
        Subject subject;
        ArrayList<Subject> subjectArray = new ArrayList<>();

        for(int i = 0; i < array.length(); i++) {
            try {
                subject = Subject.fromJsonObject(array.getJSONObject(i));
                subjectArray.add(subject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return subjectArray;
    }



    public String getJsonString() {
        return jsonOutput;
    }
}
