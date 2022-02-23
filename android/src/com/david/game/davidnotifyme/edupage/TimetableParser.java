package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.util.Log;

import androidx.activity.result.ActivityResultRegistry;
import androidx.appcompat.widget.AppCompatRadioButton$InspectionCompanion;

import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.edupage.readers.EdupageSerializableReader;
import com.david.game.davidnotifyme.edupage.timetable_objects.Classroom;
import com.david.game.davidnotifyme.edupage.timetable_objects.SemiSubject;
import com.david.game.davidnotifyme.edupage.timetable_objects.StudentsClass;
import com.david.game.davidnotifyme.edupage.timetable_objects.Subject;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

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

    ArrayList<Day> timetable;

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


    public ArrayList<Day> parse(JSONArray arrayOfSubjects) throws JSONException {
//      Log.d("arrayOfSubjects", arrayOfSubjects.toString());

        // pridať filter pre skupiny

        for (int i = 0; i < arrayOfSubjects.length(); i++) {
            JSONObject obj = (JSONObject) arrayOfSubjects.get(i);
            try {
                SemiSubject semiSubject = subjectHashMap.get(Integer.valueOf(obj.get("subjectid").toString()));

                Subject subject;
                JSONArray classroomIds = ((JSONArray) obj.get("classroomids"));
                String id = classroomIds.length() > 0 ? classroomIds.getString(0) : "nemá";
                EdupageSerializable s = classroomHashMap.get(Integer.parseInt(id));
                subject = new Subject(
                        obj.getString("starttime"),
                        obj.getString("endtime"),
                        s != null ? s.getName() : "iné",
                        semiSubject);


                timetable.get(findIndexOfDay(obj.getString("date"))).append(subject);


            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return timetable;
    }

    private ArrayList<Day> fillDays() {
        ArrayList<Day> out = new ArrayList<>();
        for (String date : DavidClockUtils.getCurrentWeekDates()) { // dávať pozor pri zmene current week na last week  v edupage classe
            out.add(new Day(date));
        }
        return out;
    }

    private int findIndexOfDay(String date) {
        for (int i = 0; i < timetable.size(); i++) {
            Day day = timetable.get(i);
            if (day.getDate().equals(date)) {
                return i;
            }
        }
        return -1;
    }

    public int save() {
        InternalStorageFile saver = new InternalStorageFile(context, InternalFiles.TIMETABLE);

        JSONArray timetableArray = new JSONArray();
        for (Day day : timetable) {
            timetableArray.put(day.toJsonArray());
        }

        JSONObject finalJsonTimetable = new JSONObject();
        try {
            finalJsonTimetable.put("timetable", timetableArray);
            saver.clear();
            saver.append(finalJsonTimetable.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return 0;
    }


    class Day {
        ArrayList<Subject> subjectsArray;
        String date;

        public Day(String date) {
            subjectsArray = new ArrayList<>();
            this.date = date;
        }

        public void append(Subject subject) {
            subjectsArray.add(subject);
        }

        public Subject get(int index) {
            return subjectsArray.get(index);
        }

        public String getDate() {
            return date;
        }

        public JSONArray toJsonArray() {
            JSONArray array = new JSONArray();
            try {

                for (Subject sub : subjectsArray) {
                    array.put(sub.toJsonObject());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return array;
        }
    }


}
