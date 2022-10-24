package com.david.game.davidnotifyme.edupage.timetable_objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;

import com.david.game.davidnotifyme.david.David;
import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.david.Timetable;
import com.david.game.davidnotifyme.edupage.TimetableParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Groups {

    public static Set<String> loadFromTimetable(Timetable timetable) { //TODO make a hell lot of faster

        Set<String> groups = new HashSet<>();
        ArrayList<String> found = new ArrayList<>();

        for(TimetableParser.Day day : timetable.getFullTimetable()) {

            for (Subject subject : day.getSubjectsArray()) {
                if (subject.subjectGroups.length > 0 && !found.contains(subject.subjectGroups[0])) {
                    String relatedSubjects = findRelated(day, subject, found);
                    if(relatedSubjects.contains("/")) groups.add(relatedSubjects);
                    found.add(subject.subjectGroups[0]);
                }
            }
        }

        System.out.println(groups);

        return groups;
    }

    private static String findRelated(TimetableParser.Day day, Subject subject, ArrayList<String> found) {
        Set<String> groups = new HashSet<>();

        for (Subject sub : day.getSubjectsArray()) {
            if(sub.subjectGroups.length > 0 && DavidClockUtils.timeToMinutes(subject.getStart()) < DavidClockUtils.timeToMinutes(sub.getEnd())
                && DavidClockUtils.timeToMinutes(subject.getEnd()) > DavidClockUtils.timeToMinutes(sub.getStart())) {
                if(!found.contains(sub.subjectGroups[0])) {
                    groups.add(sub.subjectGroups[0]);
                    found.add(sub.subjectGroups[0]);
                }
            }
        }

        String[] out = new String[groups.size()];
        groups.toArray(out);

        return Arrays.toString(out).replace(", ", "/").replace(",", "/")
                .replace("[", "").replace("]", "");
    }

    public static String[] getSavedGroups(Context context) {
        ArrayList<String> groups = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> allPrefs = prefs.getAll();
        for(String key : allPrefs.keySet()) {
            if(key.startsWith("group-")) groups.add(allPrefs.get(key).toString());
        }

        return groups.toArray(new String[0]);
    }
}
