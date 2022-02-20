package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.security.keystore.StrongBoxUnavailableException;
import android.util.Log;

import com.badlogic.gdx.utils.UBJsonReader;
import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ConcurrentModificationException;

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

            Subject[] subjectsArray = parseSubjects(rawJSON);
            saveParsedData(subjectsArray, InternalFiles.SUBJECTS);

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

    private Subject[] parseSubjects(String rawJSON) {
        try {
            JSONObject json = new JSONObject(rawJSON);
            JSONArray j = json.getJSONObject("r").getJSONArray("tables");

            JSONArray classesArray = null;
            for (int i = 0; i < j.length(); i++) {
                JSONObject obj = ((JSONObject) j.get(i));

                if (obj.get("id").equals("subjects")) {
                    classesArray = obj.getJSONArray("data_rows");
                    break;
                }
            }
            if (classesArray == null) return null;

            Subject[] output = new Subject[classesArray.length()];
            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject jsonClassObject = (JSONObject) classesArray.get(i);

                output[i] = new Subject(jsonClassObject.getString("name"), jsonClassObject.getString("id"), jsonClassObject.getString("short"));
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

    public StudentsClass[] parseClasses(String rawJSON) {
        try {
            JSONObject json = new JSONObject(rawJSON);
            JSONArray j = json.getJSONObject("r").getJSONArray("tables");

            JSONArray classesArray = null;
            for (int i = 0; i < j.length(); i++) {
                JSONObject obj = ((JSONObject) j.get(i));

                if (obj.get("id").equals("classes")) {
                    classesArray = obj.getJSONArray("data_rows");
                    break;
                }
            }

            StudentsClass[] output = new StudentsClass[classesArray.length()];
            for (int i = 0; i < classesArray.length(); i++) {
                JSONObject jsonClassObject = (JSONObject) classesArray.get(i);

                output[i] = new StudentsClass(jsonClassObject.getString("id"), jsonClassObject.getString("name"));
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
