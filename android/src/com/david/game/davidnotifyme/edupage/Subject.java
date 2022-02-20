package com.david.game.davidnotifyme.edupage;

import java.util.Objects;

public class Subject implements EdupageSerializable {
    private String name;
    private String nameShort;
    private Integer classroomNumber;


    public Subject(String name, int classroomNumber, String nameShort) {
        this.name = Objects.requireNonNull(name);
        this.nameShort = nameShort;
        this.classroomNumber = classroomNumber;
    }

    public Subject(String name, String classroomNumber, String nameShort) {
        this.name = Objects.requireNonNull(name);
        this.classroomNumber = Integer.parseInt(Objects.requireNonNull(classroomNumber));
        this.nameShort = nameShort;
    }

    private String startTime;
    private String endTime;

    public Subject addTimes(String startTime, String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    public int getClassroomNumber() {
        return classroomNumber;
    }

    public String getNameShort() {
        return nameShort;
    }
    public String getSubjectName() {
        return name;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", nameShort='" + nameShort + '\'' +
                ", classroomNumber=" + classroomNumber +
                '}';
    }

    @Override
    public String serialize() {
        return name + ":" + classroomNumber + ":" + nameShort;
    }

    public static Subject mutate(String serialized) {
        if(serialized.endsWith(":")) serialized += "ERROR";
        String[] data = serialized.split(":");

        String name = data[0];
        Integer classroomNumber = Integer.valueOf(data[1]);
        String nameShort = data[2];
        return new Subject(name, classroomNumber, nameShort);
    }

}
