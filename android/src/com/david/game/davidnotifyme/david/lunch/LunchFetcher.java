package com.david.game.davidnotifyme.david.lunch;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;


import androidx.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.Array;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Executor;



public class LunchFetcher {

    private final Executor executor;

    public LunchFetcher(Context context, Executor executor) {
        this.executor = executor;
    }

    public void makeLunchFetchRequest(final LunchCallback<ArrayList<String>> callback) {
//        int day = LocalDate.now().getDayOfWeek().getValue();
//        if(day < 6){
            executor.execute(new Runnable() {
                @Override
                public void run() {
//                    Result<String> result = synchronousFetch(day);
                    Result<ArrayList<String>> result = synchronousFetch();
                    callback.onComplete(result);
                }
            });
//        }else{
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    callback.onComplete(new Result.Success<String>("Je víkend"));
//                }
//            });
//        }

    }

    public Result<ArrayList<String>> synchronousFetch() { // int day
        try {
            ArrayList<String> lunchTable = new ArrayList<>();
            Document doc = Jsoup.connect("https://www.eskoly.sk/komenskeho44/jedalen").get();
            Elements table = doc.select("table.foodtable");
            Element tbody = table.get(0).select("tbody").first();
            assert tbody != null;
            Elements days = tbody.select("tr");

            for (int i = 0; i < days.size(); i++) {
                Elements foodsAtDay = days.get(i).select("td.foodCell");
                if (foodsAtDay.size() > 2) {
                    Element food = foodsAtDay.get(2);
                    lunchTable.add(food.text());
                    Log.d("lunch", food.text());
                }
            }

            for (String item : lunchTable){
                Log.d("lunch table", item);
            }
            Log.d("table", lunchTable.toString());

//            return new Result.Success<String>(lunchTable.get(day - 1));
            return new Result.Success<ArrayList<String>>(lunchTable);

        } catch (IOException e) {
            return new Result.Error<>(null);
        }
    }

}


