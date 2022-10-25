package com.david.game.davidnotifyme.edupage.internet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

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
        Log.d("fetchresponse", s + " ");
        callback.onComplete(new Result.Success<>(s));
    }

    public void setCallback(EdupageCallback<String> callback) {
        this.callback = callback;
    }


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
