package com.david.game.davidnotifyme.edupage;

import androidx.annotation.NonNull;

public class StudentsClass implements EdupageSerializable{
    private String id;
    private String label;

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

    @NonNull
    @Override
    public String toString() {
        return "StudentsClass{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    public static StudentsClass mutate(String serialized){
        String[] data = serialized.split(":");
        String label = data[0];
        String id = data[1];
        return new StudentsClass(id, label);
    }

    @Override
    public String serialize(){
        return id + ":" + label;
    }
}
