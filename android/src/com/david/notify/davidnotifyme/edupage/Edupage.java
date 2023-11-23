package com.david.notify.davidnotifyme.edupage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.preference.PreferenceManager;

import com.david.notify.R;
import com.david.notify.davidnotifyme.david.David;
import com.david.notify.davidnotifyme.david.DavidClockUtils;
import com.david.notify.davidnotifyme.edupage.internet.AsyncEdupageFetcher;
import com.david.notify.davidnotifyme.edupage.internet.EdupageCallback;
import com.david.notify.davidnotifyme.edupage.internet.Result;
import com.david.notify.davidnotifyme.edupage.readers.EdupageSerializableReader;
import com.david.notify.davidnotifyme.edupage.readers.TimetableReader;
import com.david.notify.davidnotifyme.edupage.timetable_objects.Classroom;
import com.david.notify.davidnotifyme.edupage.timetable_objects.SemiSubject;
import com.david.notify.davidnotifyme.edupage.timetable_objects.StudentsClass;
import com.david.notify.davidnotifyme.utils.EdupageRoutes;
import com.david.notify.davidnotifyme.utils.InternalFiles;
import com.david.notify.davidnotifyme.utils.InternalStorageFile;

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
        asyncEdupageFetcher = new AsyncEdupageFetcher(new EdupageCallback<String>() {
            @Override
            public String onComplete(Result.Success<String> result) {
                Log.d("data fetched ", result.data);
                String rawJSON = result.data;

                StudentsClass[] classArray = Edupage.this.parseClasses(rawJSON);
                Edupage.this.saveParsedData(classArray, InternalFiles.CLASSES);

                SemiSubject[] subjectsArray = Edupage.this.parseSubjects(rawJSON);
                Edupage.this.saveParsedData(subjectsArray, InternalFiles.SUBJECTS);

                Classroom[] classroomArray = Edupage.this.parseClassrooms(rawJSON);
                Edupage.this.saveParsedData(classroomArray, InternalFiles.CLASSROOM);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                String classname = preferences.getString("trieda", "II.A");

                StudentsClass classroom;
                //niekde v kode je hardconduta II.A (nie ta hore) namiesto idecka, preto tie ify
                if (classname.chars().allMatch(Character::isDigit)) {
                    classroom = Edupage.this.findClassroomById(Integer.valueOf(classname));
                } else {
                    classroom = Edupage.this.findClassroomByName(classname);
                }

//                Log.d("trieda", classroom.getName());
                if(classroom != null){
                    Log.e("trieda je null", "pomoc nieco je zleee aaa");
                    Edupage.this.timetableFetch(classroom.getId());
                }
                return null;
            }
        });

        try {
            System.out.println("dates  + " + startDate + " " + endDate);
            String content = InternalStorageFile.readFromResource(context, R.raw.subject_id_payload);
            assert content != null;
            JSONObject payload = new JSONObject(content);
            payload.getJSONArray("__args").put(1, DavidClockUtils.getSchoolYear());

            JSONObject vt_filter = payload.getJSONArray("__args").getJSONObject(2).getJSONObject("vt_filter");
            vt_filter.put("datefrom", startDate);
            vt_filter.put("dateto", endDate);

            Log.d("payload", payload.toString());
            if(David.maPristupKInternetu(context))
                asyncEdupageFetcher.execute(EdupageRoutes.SUBJECT_ID_URL.getEdupageRoute(), payload.toString());
            else {
                //TODO zase citaj zo suboru ?????
            }
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
                    if(!result.data.equals("fallback")){

                        ArrayList<TimetableParser.Day> timetable = parseTimetable(result.data);

                        Log.d("result", result.data);

                        if (onCompletionListener != null) {
                            onCompletionListener.onComplete(timetable, 0);
                        }
                    }else{
                        //TODO mozno tiez skontrolovat ci mame aj nieco ulozene
                        onCompletionListener.onComplete(new TimetableReader(context).read().getTimetableArray(),0); //making a fallback to saved timetable
                    }
                    return null;
                }

            }).execute(EdupageRoutes.TIMETABLE_URL.getEdupageRoute(), payload.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnCompletionListener {
        void onComplete(ArrayList<TimetableParser.Day> timetable, int status);
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
