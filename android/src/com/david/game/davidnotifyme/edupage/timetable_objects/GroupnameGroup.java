package com.david.game.davidnotifyme.edupage.timetable_objects;

public class GroupnameGroup {
    private String[] groupnames;
    private String label;
    public GroupnameGroup(String[] groupnames, String label)
    {
        this.groupnames = groupnames;
        this.label = label;
    }


    public String[] getGroupnames() {
        return groupnames;
    }

    public String getLabel() {
        return label;
    }
}