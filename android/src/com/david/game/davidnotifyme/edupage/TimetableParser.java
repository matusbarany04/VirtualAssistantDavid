package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.badlogic.gdx.utils.Null;
import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;

public class TimetableParser {
    //    DavidClockUtils
    Context context;
    Subject[] allSubjects;
    HashMap<Integer, Subject> subjectHashMap;

    public TimetableParser(Context context) {
        this.context = context;
        loadAllSubjects();
    }

    private void loadAllSubjects() {
        SubjectsReader reader = new SubjectsReader(context);
        this.subjectHashMap = reader.getSubjectsAsHashMap();
    }

    public String parse(JSONArray arrayOfSubjects) throws JSONException {
        Log.d("arrayOfSubjects", arrayOfSubjects.toString());
        for (int i = 0; i < arrayOfSubjects.length(); i++) {
            JSONObject obj = (JSONObject) arrayOfSubjects.get(i);
            Log.d("subject", obj.toString());
            try {
                Subject s = subjectHashMap.get(Integer.valueOf(obj.get("subjectid").toString()));

                Log.d("SUBJECTS", obj.get("starttime") + "\n" + obj.get("uniperiod") + "\n" +
                        s.getNameShort()); // groupnames  endtime
            } catch (NumberFormatException e) {
            }


        }
        return null;
    }


    class Day {
        Subject[] subjectsArray;

        public Day() {

        }


    }

}
