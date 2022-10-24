package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.preference.PreferenceManager;

import com.david.game.R;
import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.edupage.internet.AsyncEdupageFetcher;
import com.david.game.davidnotifyme.edupage.internet.EdupageCallback;
import com.david.game.davidnotifyme.edupage.internet.Result;
import com.david.game.davidnotifyme.edupage.readers.EdupageSerializableReader;
import com.david.game.davidnotifyme.edupage.timetable_objects.Classroom;
import com.david.game.davidnotifyme.edupage.timetable_objects.Groups;
import com.david.game.davidnotifyme.edupage.timetable_objects.SemiSubject;
import com.david.game.davidnotifyme.edupage.timetable_objects.StudentsClass;
import com.david.game.davidnotifyme.utils.EdupageRoutes;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;
import com.david.game.davidnotifyme.utils.PreferencesReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Edupage {
    private final String TAG = "Edupage-scraper";

    AsyncEdupageFetcher asyncEdupageFetcher;
    Context context;
    String startDate;
    String endDate;
    private OnCompletionListener onCompletionListener;

    // file pre skupiny

    public Edupage(Context context) {
        this.context = context;
        Pair<String, String> dates = DavidClockUtils.getCurrentWeek(); // nezabudnut zmeniť na current week
        startDate = dates.first;
        endDate = dates.second;

        // init musí byť posledný
        init();
    }


    public void init() {
        asyncEdupageFetcher = new AsyncEdupageFetcher(result -> {
            String rawJSON = result.data;

            StudentsClass[] classArray = parseClasses(rawJSON);
            saveParsedData(classArray, InternalFiles.CLASSES);

            SemiSubject[] subjectsArray = parseSubjects(rawJSON);
            saveParsedData(subjectsArray, InternalFiles.SUBJECTS);

            Classroom[] classroomArray = parseClassrooms(rawJSON);
            saveParsedData(classroomArray, InternalFiles.CLASSROOM);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            String classname = preferences.getString("trieda", "II.A");

            StudentsClass classroom;
            //niekde v kode je hardconduta II.A (nie ta hore) namiesto idecka, preto tie ify
            if (classname.chars().allMatch( Character::isDigit )){
                classroom = findClassroomById(Integer.valueOf(classname));
            }else{
                classroom = findClassroomByName(classname);
            }

            Log.d("trieda", classroom.getName());

            timetableFetch(classroom.getId()); // change to dynamic class  chosen by user
            return null;
        });

        try {
            System.out.println("dates  + " + startDate + " " + endDate);
            String content = InternalStorageFile.readFromResource(context, R.raw.subject_id_payload);
            JSONObject payload = new JSONObject(content);
            payload.getJSONArray("__args").put(1, DavidClockUtils.getSchoolYear());

            JSONObject vt_filter = payload.getJSONArray("__args").getJSONObject(2).getJSONObject("vt_filter");
            vt_filter.put("datefrom", startDate);
            vt_filter.put("dateto", endDate);

            Log.d("payload", payload.toString());

            asyncEdupageFetcher.execute(EdupageRoutes.SUBJECT_ID_URL.getEdupageRoute(), payload.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveParsedData(EdupageSerializable[] array, InternalFiles filename) {
        InternalStorageFile fileManager = new InternalStorageFile(this.context, filename);
        fileManager.clear();
        for (EdupageSerializable subject : array) {
            fileManager.append(subject.serialize()).append("/");
        }

        if (InternalFiles.SUBJECTS.equals(filename)) {
            fileManager.readDeserializableSubjects();
        }
    }

    public StudentsClass findClassroomByName(String label) {

        EdupageSerializableReader<StudentsClass> reader = new EdupageSerializableReader<>(context, InternalFiles.CLASSES, StudentsClass::new);

        HashMap<String, StudentsClass> classes = reader.getsAsHashMapNameObject();

        Log.d("label", label);
        Log.d("all classes", classes + "");

        return classes.get(label);
    }

    public StudentsClass findClassroomById(Integer id) {

        EdupageSerializableReader<StudentsClass> reader = new EdupageSerializableReader<>(context, InternalFiles.CLASSES, StudentsClass::new);

        HashMap<Integer, StudentsClass> classes = reader.getsAsHashMapIdObject();

        Log.d("id", id.toString());
        Log.d("all classes", classes + "");

        return classes.get(id);
    }

    private SemiSubject[] parseSubjects(String rawJSON) {
        try {
            JSONArray classesArray = getRow(rawJSON, "subjects");

            if (classesArray == null) return null;

            SemiSubject[] output = new SemiSubject[classesArray.length()];
            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject jsonClassObject = (JSONObject) classesArray.get(i);

                output[i] = new SemiSubject(
                        jsonClassObject.getString("name"),
                        jsonClassObject.getString("id"),
                        jsonClassObject.getString("short")
                );
//                Log.d("SemiSubject", output[i].toString());
            }

            return output;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void timetableFetch(String classId) {
        try {
            String content = InternalStorageFile.readFromResource(context, R.raw.timetable_payload);
            JSONObject payload = new JSONObject(content);
            JSONObject params = payload.getJSONArray("__args").getJSONObject(1);
            params.put("datefrom", startDate);
            params.put("dateto", endDate);
            params.put("year", DavidClockUtils.getSchoolYear());
            params.put("id", classId);

            new AsyncEdupageFetcher(new EdupageCallback<String>() {
                @Override
                public String onComplete(Result.Success<String> result) {

                    ArrayList<TimetableParser.Day> timetable = parseTimetable(result.data);

                    Log.d("result", result.data);

                    if (onCompletionListener != null) {
                        onCompletionListener.onComplete(timetable);
                    }
                    return null;
                }

            }).execute(EdupageRoutes.TIMETABLE_URL.getEdupageRoute(), payload.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnCompletionListener {
        void onComplete(ArrayList<TimetableParser.Day> timetable);
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    private ArrayList<TimetableParser.Day> parseTimetable(String rawJSON) {
        try {
            JSONObject json = new JSONObject(rawJSON);
            JSONArray j = json.getJSONObject("r").getJSONArray("ttitems");
            TimetableParser.resetTimetable();

            TimetableParser parser = new TimetableParser(context);
            ArrayList<TimetableParser.Day> parsed = parser.parse(j);

            parser.save();
           // parser.getGroupOfGroupNames();
            return parsed;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Classroom[] parseClassrooms(String rawJSON) {
        try {

            JSONArray classesArray = getRow(rawJSON, "classrooms");

            Classroom[] output = new Classroom[classesArray.length()];
            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject jsonClassObject = (JSONObject) classesArray.get(i);

                output[i] = new Classroom(jsonClassObject.getString("short"), jsonClassObject.getString("id"));
            }

            return output;
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("error error!" ,"hey  you've got a problem man");
        return null;
    }

    public JSONArray getRow(String rawJSON, String rowName) throws JSONException {
        System.out.println(rawJSON);
        JSONObject json = new JSONObject(rawJSON);
        JSONArray j = json.getJSONObject("r").getJSONArray("tables");

        JSONArray classesArray = null;
        for (int i = 0; i < j.length(); i++) {
            JSONObject obj = ((JSONObject) j.get(i));

            if (obj.get("id").equals(rowName)) {
                classesArray = obj.getJSONArray("data_rows");
                break;
            }
        }

        return classesArray;
    }

    public StudentsClass[] parseClasses(String rawJSON) {
        try {
            JSONArray classesArray = getRow(rawJSON, "classes");

            StudentsClass[] output = new StudentsClass[classesArray.length()];
            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject jsonClassObject = (JSONObject) classesArray.get(i);

                output[i] = new StudentsClass(jsonClassObject.getString("name"), jsonClassObject.getString("id"));
            }

            return output;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isCached() { //to do
        return false;
    }


}
