package com.david.game.davidnotifyme.utils;

public enum EdupageRoutes {

    SUBJECT_ID_URL("https://spseke.edupage.org/rpr/server/maindbi.js?__func=mainDBIAccessor"),
    TIMETABLE_URL("https://spseke.edupage.org/timetable/server/currenttt.js?__func=curentttGetData");

    private final String url;

    EdupageRoutes(String url) {
           this.url = url;
    }

    public String getEdupageRoute() {
        return url;
    }
}
