package com.david.game.davidnotifyme.edupage.timetable_objects;

import com.david.game.davidnotifyme.edupage.EdupageSerializable;

public class Classroom extends EdupageSerializable {
    private String classroomId;
    private String classroomAnnotation;

    public Classroom(String classroomAnnotation, String id) {
        this.classroomAnnotation = classroomAnnotation;
        this.classroomId = id;
    }

    public Classroom() {

    }

    public String getClassroomId() {
        return classroomId;
    }

    @Override
    public String serialize() {
        return  classroomAnnotation + ":" + classroomId;
    }

    @Override
    public EdupageSerializable init(String[] data) {
        this.classroomAnnotation = data[0];
        this.classroomId =  data[1];
        return this;
    }

    @Override
    public String getId() {
        return classroomId;
    }

    @Override
    public String getName() {
        return classroomAnnotation;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }
}
