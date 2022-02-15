package com.david.game.davidnotifyme.edupage;

public class StudentsClass {
    public String id;
    public String label;

    // to add interface for position in school
    public StudentsClass(String id, String label){
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }
}
