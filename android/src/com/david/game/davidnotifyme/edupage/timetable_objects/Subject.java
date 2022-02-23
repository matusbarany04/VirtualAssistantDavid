package com.david.game.davidnotifyme.edupage.timetable_objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Subject {
    private String startTime;
    private String endTime;
    private String classroomNumber;
    public String shortName;
    public String subjectName;

    public Subject(String startTime, String endTime, String classroomNumber, String shortName, String subjectName){
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomNumber = classroomNumber;
        this.shortName = shortName;
        this.subjectName = subjectName;
    }


    public Subject(String startTime, String endTime, String classroomNumber, SemiSubject semiSubject){
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomNumber = classroomNumber;
        this.subjectName = semiSubject.getName();
        this.shortName = semiSubject.getNameShort();

    }


    public Subject setTimes(String startTime, String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("classnum", classroomNumber);
        json.put("name", shortName);
        json.put("startTime" ,startTime);
        json.put("endTime" ,endTime);

        return json;
    }


}
