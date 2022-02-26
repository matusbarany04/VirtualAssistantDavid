package com.david.game.davidnotifyme.utils;

public enum InternalFiles {
    CLASSES("classes"),
    TIMETABLE("timetable"),
    LUNCH("lunch"),
    SUBJECTS("subjects"),
    CLASSROOM("classroom");

    String fileName;

    InternalFiles(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }
}