package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.util.Log;

import androidx.activity.result.ActivityResultRegistry;

import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.edupage.readers.EdupageSerializableReader;
import com.david.game.davidnotifyme.edupage.timetable_objects.Classroom;
import com.david.game.davidnotifyme.edupage.timetable_objects.SemiSubject;
import com.david.game.davidnotifyme.edupage.timetable_objects.StudentsClass;
import com.david.game.davidnotifyme.edupage.timetable_objects.Subject;
import com.david.game.davidnotifyme.utils.InternalFiles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TimetableParser {
    //    DavidClockUtils
    Context context;
    HashMap<Integer, SemiSubject> subjectHashMap;
    HashMap<Integer, Classroom> classroomHashMap;
    HashMap<Integer, StudentsClass> classHashMap;

    HashMap<String, Day> timetable;

    public TimetableParser(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        EdupageSerializableReader<SemiSubject> semiSubjectReader = new EdupageSerializableReader<>(context, InternalFiles.SUBJECTS, SemiSubject::new);
        EdupageSerializableReader<Classroom> classroomReader = new EdupageSerializableReader<>(context, InternalFiles.CLASSROOM, Classroom::new);
        EdupageSerializableReader<StudentsClass> studentClassReader = new EdupageSerializableReader<>(context, InternalFiles.CLASSES, StudentsClass::new);
        this.subjectHashMap = semiSubjectReader.getsAsHashMap();
        this.classroomHashMap = classroomReader.getsAsHashMap();
        this.classHashMap = studentClassReader.getsAsHashMap();
        timetable = fillDays();
    }

    public  ArrayList<Day> parse(JSONArray arrayOfSubjects) throws JSONException {
        DavidClockUtils.getCurrentWeek();

        Log.d("arrayOfSubjects", arrayOfSubjects.toString());
        for (int i = 0; i < arrayOfSubjects.length(); i++) {
            JSONObject obj = (JSONObject) arrayOfSubjects.get(i);
            Log.d("subject", obj.toString());
            try {
                SemiSubject semiSubject = subjectHashMap.get(Integer.valueOf(obj.get("subjectid").toString()));

                Subject subject;
                String id2 = ((JSONArray) obj.get("classroomids")).getString(0);
                EdupageSerializable s = classroomHashMap.get(id2);
                subject = new Subject(
                        obj.getString("starttime"),
                        obj.getString("endtime"),
                        s != null ? s.getName() : "iné",
                        semiSubject);


                timetable.get(obj.getString("date")).append(subject);


            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Day> output = new ArrayList<>(timetable.values());
        return output;
    }

    private HashMap<String,Day> fillDays() {
        HashMap<String, Day> out = new HashMap<>();
        for (String date : DavidClockUtils.getCurrentWeekDates()) {
            out.put(date, new Day(date));
        }
        return out;
    }


    class Day {
        ArrayList<Subject> subjectsArray;
        String date;

        public Day(String date) {
            this.date = date;
        }

        public void append(Subject subject) {
            subjectsArray.add(subject);
        }

        public Subject get(int index) {
            return subjectsArray.get(index);
        }
    }

}
