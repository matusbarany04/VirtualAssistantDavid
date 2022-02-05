package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.utils.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class EdupageScraper {

    public EdupageScraper()
    {
        init();
    }

    public void init() {

        try {
            new AsyncEdupageFetcher(result -> {
                String rawJSON =  ((Result.Success<String>) result).data;
                parseFetchedData(rawJSON);
                return null;
            }).execute(
            "https://spseke.edupage.org/rpr/server/maindbi.js?__func=mainDBIAccessor",
                    "{\"__args\":[null,2021,{\"vt_filter\":{"+
                            "\"datefrom\":\"2022-01-31\","+
                            "\"dateto\":\"2022-02-06\"}},{"+
                            "\"op\":\"fetch\",\"needed_part\":{"+
                            "\"classes\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"classroomid\"],"+
                            "\"classrooms\":[\"short\",\"name\",\"firstname\",\"lastname\",\"subname\",\"name\",\"short\"]},"+
                            "\"needed_combos\":{}}],\"__gsh\":\"00000000\"}"
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFetchedData(String rawJSON) {
        try {
            JSONObject json = new JSONObject(rawJSON);
            JSONObject j = (JSONObject) json.getJSONObject("r").getJSONArray("tables").get(1);
            Log.d("parsing JSON" ,( (JSONObject) j.getJSONArray("data_rows").get(4)).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isCached(){ //to do
        return false;
    }

    private boolean saveFetchedData(){
        return true;
    }


}
