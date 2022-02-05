package com.david.game.davidnotifyme.david;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.david.game.R;
import com.david.game.davidnotifyme.utils.JSONparser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class SchoolNavigator {
    ArrayList<String> poschodiaKeys;
    HashMap<String, Pair<Integer, Integer>> classRanges;

    public SchoolNavigator(Context context) {
        poschodiaKeys = new ArrayList<>();
        classRanges = new HashMap<>();
        parsePoschodoch(context);
    }

    private <T> Iterable<T> iterate(final Iterator<T> i) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return i;
            }
        };
    }

    private void parsePoschodoch(Context context) {
        String rawPoschodiaData = JSONparser.getFileData(context, R.raw.poschodia);
        JSONObject obj = null;
        try {
            obj = new JSONObject(rawPoschodiaData);

            for (String key : iterate(obj.keys())) {
                poschodiaKeys.add(key);
                int[] numbers = Arrays.stream(
                        obj.getString(key)
                                .split("-"))
                        .mapToInt(Integer::parseInt)
                        .toArray();

                Pair<Integer, Integer> range = new Pair<>(numbers[0], numbers[1]);
                classRanges.put(key, range);

                Log.d("arayPoschodia ", key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSekcieSkoly() {
        return poschodiaKeys;
    }

    public String whereIs(String classNumber) {

        Log.d("classNumber", classNumber + " ");
        String almostInt= classNumber.replaceAll("[^\\d.]", "");
        Log.d("whereis", almostInt + " almostint ");

        if(almostInt.length() == 0){
            Log.d("whereis", "sa nenašla 0");
            return "sa nenašla";
        }
        int classNumberInt = Integer.parseInt(almostInt);
        for (String key : poschodiaKeys) {
            Pair<Integer, Integer> range = classRanges.get(key);

            assert range != null;
            if (range.first <= classNumberInt && classNumberInt <= range.second) {
                return key;
            }
        }


        return "sa nenašla";
    }
}

