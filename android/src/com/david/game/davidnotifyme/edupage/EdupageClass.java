package com.david.game.davidnotifyme.edupage;

public class EdupageClass {
    public String annotation;
    public int id;
    public EdupageClass(int id, String annotation){
        this.annotation = annotation;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAnnotation() {
        return annotation;
    }

}
