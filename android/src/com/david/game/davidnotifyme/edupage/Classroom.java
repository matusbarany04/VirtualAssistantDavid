package com.david.game.davidnotifyme.edupage;

public class Classroom {
    public int id;
    public String label;

    // to add interface for position in school
    public Classroom(int id, String label){
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
