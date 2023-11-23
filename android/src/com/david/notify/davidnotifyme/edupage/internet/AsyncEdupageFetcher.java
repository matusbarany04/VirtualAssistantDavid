package com.david.notify.davidnotifyme.edupage.internet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncEdupageFetcher extends AsyncTask<String, Integer, String> {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String TAG = "EDUPAGE_FETCHER";

    OkHttpClient client;

    EdupageCallback<String> callback;

    public AsyncEdupageFetcher(EdupageCallback<String> callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();//TODO change timeout dynamically
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();

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
            if(response.code() == 200){
                assert response.body() != null;
                return response.body().string();
            }

            System.out.println("fetch error " + response.body().string());
            return "fallback"; //WARNING make an interface

        } catch (Exception e) {
            System.out.println("there was an exceptiobn");
            e.printStackTrace();
            return "fallback";
        }
    }


}
