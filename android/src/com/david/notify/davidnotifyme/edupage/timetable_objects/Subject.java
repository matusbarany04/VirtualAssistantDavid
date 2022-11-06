package com.david.notify.davidnotifyme.edupage.timetable_objects;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Subject {
    private String startTime;
    private String endTime;
    private final String classroomNumber;
    public String shortName;
    public String subjectName;
    public String[] subjectGroups;

    public Subject(String startTime, String endTime, String classroomNumber, String shortName, String subjectName,String[] subjectGroup){
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomNumber = classroomNumber;
        this.shortName = shortName;
        this.subjectName = subjectName;
        this.subjectGroups = subjectGroup == null ? new String[0] : subjectGroup;
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
        json.put("subjectName", subjectName);
        json.put("startTime" ,startTime);
        json.put("endTime" ,endTime);
        JSONArray arr = new JSONArray();
        for (String group: subjectGroups) {
            arr.put(group);
        }
        json.put("groupNames",arr);

        return json;
    }

    public static Subject fromJsonObject(JSONObject jsonObject) throws JSONException{
        JSONArray jsonArray = jsonObject.getJSONArray("groupNames");
        String[] groupNames = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++ ){
            groupNames[i] = jsonArray.getString(i);
        }

     return new Subject(
             jsonObject.getString("startTime"),
             jsonObject.getString("endTime"),
             jsonObject.getString("classNum"),
             jsonObject.getString("name"),
             jsonObject.getString("subjectName"),
             groupNames); // dávať pozor lenivý Matúš tu bol, usilovný matúš tu bol tiež
    }

    public boolean containsSubjectGroups(String[] subjectGroups){
        if (this.subjectGroups.length == 0) return true;
        if (this.subjectGroups[0].equals("")) return true;
        for (String outer : subjectGroups){
            for (String inner : this.subjectGroups) {
                if (outer.equals(inner)) return true;
            }
        }
        return false;
    }

    public String getClassroomNumber() {
        return classroomNumber;
    }

    public String getStart() {
        return startTime;
    }

    public String getEnd() {
        return endTime;
    }

    @NonNull
    @Override
    public String toString() {
        return shortName;
    }
}
