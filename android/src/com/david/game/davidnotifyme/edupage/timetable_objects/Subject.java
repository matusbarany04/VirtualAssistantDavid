package com.david.game.davidnotifyme.edupage.timetable_objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Subject {
    private String startTime;
    private String endTime;
    private String classroomNumber;
    public String shortName;
    public String subjectName;
    public String[] subjectGroups;

    public Subject(String startTime, String endTime, String classroomNumber, String shortName, String subjectName){
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomNumber = classroomNumber;
        this.shortName = shortName;
        this.subjectName = subjectName;
        this.subjectGroups = subjectGroups == null ? new String[0] : subjectGroups;
    }


    public Subject(String startTime, String endTime, String classroomNumber, SemiSubject semiSubject, String[] subjectGroup){
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomNumber = classroomNumber;
        this.subjectName = semiSubject.getName();
        this.shortName = semiSubject.getNameShort();
        this.subjectGroups = subjectGroup;
    }


    public Subject setTimes(String startTime, String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("classNum", classroomNumber);
        json.put("name", shortName);
        json.put("startTime" ,startTime);
        json.put("endTime" ,endTime);
        JSONArray arr = new JSONArray();
        for (String group: subjectGroups) {
            arr.put(group);
        }
        json.put("groupNames",arr);

        return json;
    }

    public boolean containsSubjectGroups(String[] subjectGroups){
        if (this.subjectGroups.length == 0) return true;
        if (this.subjectGroups[0].equals("")) return true;
        for (String outer : subjectGroups){
            for (String inner: this.subjectGroups) {
                if (outer.equals(inner)) return true;
            }
        }
        return false;
    }


}