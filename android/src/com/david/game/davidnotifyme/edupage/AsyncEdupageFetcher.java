package com.david.game.davidnotifyme.edupage;

import android.content.Context;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.util.Log;

import com.david.game.davidnotifyme.david.lunch.LunchCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncEdupageFetcher extends AsyncTask<String, Integer, String> {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String TAG = "EDUPAGE_FETCHER";

    OkHttpClient client = new OkHttpClient();

    EdupageCallback<String> callback;

    public AsyncEdupageFetcher(EdupageCallback<String> callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return post(strings[0], strings[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.onComplete(new Result.Success<>(s));
    }

//    public void fetch() {
//        try {
////            post("https://spseke.edupage.org/timetable/server/currenttt.js?__func=curentttGetData",
////            "{\"__args\":[null,{\"year\":2021,\"datefrom\":\"2022-01-31\",\"dateto\":\"2022-02-06\",\"table\":\"classes\",\"id\":\"887799\",\"showColors\":true,\"showIgroupsInClasses\":false,\"showOrig\":true}],\"__gsh\":\"00000000\"}"
////            );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
