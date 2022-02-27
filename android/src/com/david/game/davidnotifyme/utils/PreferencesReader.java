package com.david.game.davidnotifyme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferencesReader {

    private final Context context;
    private final SharedPreferences preferences;

    public PreferencesReader(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String[] getSavedGroups() {
        return new String[]{
                preferences.getString("nemcina", "1. sk"),
                preferences.getString("odp", "A1"),
                preferences.getString("etv_nbv", "ETV")
        };

    }
}
