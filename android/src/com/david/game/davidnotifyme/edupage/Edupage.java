package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.util.Log;

import com.david.game.davidnotifyme.edupage.internet.AsyncEdupageFetcher;
import com.david.game.davidnotifyme.edupage.internet.EdupageCallback;
import com.david.game.davidnotifyme.edupage.internet.Result;
import com.david.game.davidnotifyme.edupage.timetable_objects.Classroom;
import com.david.game.davidnotifyme.edupage.timetable_objects.SemiSubject;
import com.david.game.davidnotifyme.edupage.timetable_objects.StudentsClass;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Edupage {
    private final String TAG = "Edupage-scraper";
    AsyncEdupageFetcher asyncEdupageFetcher;
    Context context;


    public Edupage(Context context) {
        this.context = context;
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

            timetableFetch(String.valueOf(classArray[0].getId())); // change to dynamic class  chosen by user
            return null;
        });

//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-dd-MM");
//        LocalDateTime now = LocalDateTime.now(TimeZone.getTimeZone("UTC"));
//        Log.d(TAG, DavidClockUtils.getCurrentWeek(Calendar.getInstance()));
        try {
            asyncEdupageFetcher.execute(
                    "https://spseke.edupage.org/rpr/server/maindbi.js?__func=mainDBIAccessor",
                    "{\"__args\":[null,2021,{\"vt_filter\":{\"datefrom\":\"2022-02-07\",\"dateto\":\"2022-02-13\"}},{\"op\":\"fetch\"," +
                            "\"needed_part\":{" +
                            //  "\"teachers\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"cb_hidden\",\"expired\",\"firstname\",\"lastname\",\"short\"],"+
                            "\"classes\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"classroomid\"]," +
                            "\"classrooms\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"name\",\"short\"]," +
                            "\"students\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"classid\"]," + // nemazať
                            "\"subjects\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"name\",\"short\"]," + // nemazať
                            // "\"events\":[\"typ\",\"name\"],\"event_types\":[\"name\",\"icon\"],"+ // zatiaľ zakomentované ale písomky sú tu
                            "\"periods\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"period\",\"starttime\",\"endtime\"],\"dayparts\":[\"starttime\",\"endtime\"]" +
                            "},\"needed_combos\":{}}],\"__gsh\":\"00000000\"}"
            );
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

    private SemiSubject[] parseSubjects(String rawJSON) {
        try {
            JSONArray classesArray = getRow(rawJSON, "subjects");

            if (classesArray == null) return null;

            SemiSubject[] output = new SemiSubject[classesArray.length()];
            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject jsonClassObject = (JSONObject) classesArray.get(i);

                output[i] = new SemiSubject(jsonClassObject.getString("name"), jsonClassObject.getString("id"), jsonClassObject.getString("short"));
//                Log.d("SemiSubject", output[i].toString());
            }

            return output;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void timetableFetch(String classId) {
        new AsyncEdupageFetcher(new EdupageCallback<String>() {
            @Override
            public String onComplete(Result.Success<String> result) {
                parseTimetable(result.data);

                return null;
            }
        }).execute("https://spseke.edupage.org/timetable/server/currenttt.js?__func=curentttGetData",
                "{\"__args\":[null,{\"year\":2021,\"datefrom\":\"2022-02-07\",\"dateto\":\"2022-02-13\",\"table\":\"classes\",\"id\":\"" + classId + "\",\"showColors\":true,\"showIgroupsInClasses\":false,\"showOrig\":true}],\"__gsh\":\"00000000\"}"
        );

    }

    private void parseTimetable(String rawJSON) {
        try {
            JSONObject json = new JSONObject(rawJSON);
            JSONArray j = json.getJSONObject("r").getJSONArray("ttitems");
            String parsed = new TimetableParser(context).parse(j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Classroom[] parseClassrooms(String rawJSON){
        try {

            JSONArray classesArray = getRow(rawJSON, "classrooms");

            Classroom[] output = new Classroom[classesArray.length()];
            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject jsonClassObject = (JSONObject) classesArray.get(i);

                output[i] = new Classroom( jsonClassObject.getString("short"),jsonClassObject.getString("id"));
            }

            return output;
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("error error!" ,"hey  you've got a problem man");
        return null;
    }

    public JSONArray getRow(String rawJSON, String rowName) throws JSONException {
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

                output[i] = new StudentsClass(jsonClassObject.getString("name"),jsonClassObject.getString("id"));
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

    private boolean saveFetchedData() {
        return true;
    }


}
