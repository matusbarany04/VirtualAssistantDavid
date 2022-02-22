package com.david.game.davidnotifyme.edupage.timetable_objects;

import com.david.game.davidnotifyme.edupage.EdupageSerializable;

import java.util.Objects;

public class SemiSubject extends EdupageSerializable {
    private String name;
    private String nameShort;
    private String id;

    public SemiSubject(String name, String id, String nameShort) {
        this.name = Objects.requireNonNull(name);
        this.id = Objects.requireNonNull(id);
        this.nameShort = nameShort;
    }

    public SemiSubject() {

    }

    public static SemiSubject mutate(String serialized) {
        if (serialized.endsWith(":")) serialized += "ERROR";
        String[] data = serialized.split(":");

        String name = data[0];
        String classroomNumber = data[1];
        String nameShort = data[2];
        return new SemiSubject(name, classroomNumber, nameShort);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNameShort() {
        return nameShort;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getParameterCount() {
        return 3;
    }

    @Override
    public String toString() {
        return "SemiSubject{" +
                "name='" + name + '\'' +
                ", nameShort='" + nameShort + '\'' +
                ", classroomNumber=" + id +
                '}';
    }

    @Override
    public String serialize() {
        return name + ":" + id + ":" + nameShort;
    }

    @Override
    public EdupageSerializable init(String[] data) {
        name = data[0];
        id = data[1];
        nameShort = data[2];
        return this;
    }

}
