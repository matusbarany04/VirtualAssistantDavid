package com.david.game.davidnotifyme.edupage.timetable_objects;

import android.util.Pair;

import com.david.game.davidnotifyme.david.Timetable;
import com.david.game.davidnotifyme.edupage.TimetableParser;

import java.util.ArrayList;
import java.util.Set;

public class Groups {

    ArrayList<Pair<String, String>> groups;

    public static void loadFromTimetable(Timetable timetable) { //TODO make a hell lot of faster

        for(TimetableParser.Day day : timetable.timetable){
            ArrayList<Subject> subjectsWithGroup = new ArrayList<>();
            for (Subject subject : day.getSubjectsArray()) {
                if(subject.subjectGroups.length > 0) {
                    subjectsWithGroup.add(subject);
                }
            }
/*
            for(Subject subjectWithGroup: subjectsWithGroup){
                subjectWithGroup.subjectGroups
                for(Subject subjectWithGroup: subjectsWithGroup){

                }
            }
*/
        }

    }

}
