package com.david.game.davidnotifyme.edupage.timetable_objects;

import androidx.annotation.NonNull;

import com.david.game.davidnotifyme.edupage.EdupageSerializable;

public class StudentsClass extends EdupageSerializable {
    private String id;
    private String label;

    // to add interface for position in school
    public StudentsClass(String label, String id){
        Integer.parseInt(id);

        this.id = id;
        this.label = label;
    }

    public StudentsClass() {

    }

    public String getName() {
        return label;
    }

    @Override
    public int getParameterCount() {
        return 2;
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
        return new StudentsClass( label,id);
    }

    @Override
    public String serialize(){
        return label + ":" + id;
    }

    @Override
    public EdupageSerializable init(String[] data) {
        label = data[0];
        id = data[1];
        return this;
    }
}
