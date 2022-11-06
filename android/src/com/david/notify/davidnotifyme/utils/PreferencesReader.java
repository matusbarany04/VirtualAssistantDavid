package com.david.notify.davidnotifyme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.david.notify.davidnotifyme.edupage.TimetableParser;
import com.david.notify.davidnotifyme.edupage.timetable_objects.GroupnameGroup;

public class PreferencesReader {

    private final Context context;
    private final SharedPreferences preferences;

    public PreferencesReader(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String[] getSavedGroups() {
        TimetableParser parser = new TimetableParser(context);
        GroupnameGroup[] allGroups = parser.getGroupOfGroupNames();
        String[] groups = new String[allGroups.length];

        for (int i = 0; i < allGroups.length; i++) {
            GroupnameGroup group = allGroups[i];
            String savedGroup = preferences.getString(group.getLabel(), group.getGroupnames()[0]);
            groups[i] = savedGroup;
        }

        return groups;
    }
}
