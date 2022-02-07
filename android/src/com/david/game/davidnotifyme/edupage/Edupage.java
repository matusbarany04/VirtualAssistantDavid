package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.utils.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Edupage {
    private final String TAG = "Edupage-scraper";
    AsyncEdupageFetcher asyncEdupageFetcher;
    private String path = "smh";



    public Edupage() {
        init();
    }

    public Edupage(String path) {
        this.path = path;
        init();
    }

    public void init() {


        asyncEdupageFetcher = new AsyncEdupageFetcher(result -> {
            String rawJSON = result.data;
            String classId = parseFetchedData(rawJSON);
            timetableFetch(classId);
            return null;
        });

        try {
            asyncEdupageFetcher.execute(
                    "https://spseke.edupage.org/rpr/server/maindbi.js?__func=mainDBIAccessor",
                    "{\"__args\":[null,2021,{\"vt_filter\":{" +
                            "\"datefrom\":\"2022-01-31\"," +
                            "\"dateto\":\"2022-02-06\"}},{" +
                            "\"op\":\"fetch\",\"needed_part\":{" +
                            "\"classes\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"classroomid\"]," +
                            "\"classrooms\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"name\",\"short\"]}," +
                            "\"needed_combos\":{}}],\"__gsh\":\"00000000\"}"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timetableFetch(String classId) {
        new AsyncEdupageFetcher(new EdupageCallback<String>() {
            @Override
            public String onComplete(Result.Success<String> result) {
                Log.d("unparsed timetable", result.data);
                return null;
            }
        }).execute("https://spseke.edupage.org/timetable/server/currenttt.js?__func=curentttGetData",
                "{\"__args\":[null,{\"year\":2021,\"datefrom\":\"2022-01-31\",\"dateto\":\"2022-02-06\",\"table\":\"classes\",\"id\":\"" + classId + "\",\"showColors\":true,\"showIgroupsInClasses\":false,\"showOrig\":true}],\"__gsh\":\"00000000\"}"
        );

    }

    private String parseFetchedData(String rawJSON) {
        try {
            JSONObject json = new JSONObject(rawJSON);
            JSONArray j = json.getJSONObject("r").getJSONArray("tables");
            Log.d(TAG, j.toString());
            String id = ((JSONObject) ((JSONObject) j.get(1)).getJSONArray("data_rows").get(8)).getString("id");
            Log.d("parsing JSON", id);
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
            return "000";
        }
    }

    private boolean isCached() { //to do
        return false;
    }

    private boolean saveFetchedData() {
        return true;
    }


}
