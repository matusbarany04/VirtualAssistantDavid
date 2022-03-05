package com.david.game.davidnotifyme.edupage.timetable_objects;

public class GroupnameGroup {
    private final String[] groupnames;
    private final String label;
    private String[] fullGroupNames;

    public GroupnameGroup(String[] groupnames, String label)
    {
        this.groupnames = groupnames;
        this.label = label;
        checkFullNames();
    }

    public String[] getGroupnames() {
        return groupnames;
    }

    public String[] getFullGroupNames() {
        return fullGroupNames;
    }

    public String getLabel() {
        return label;
    }

    private void checkFullNames() {
        fullGroupNames = new String[groupnames.length];
        for(int i = 0; i < groupnames.length; i++) {
            String group = groupnames[i];
            switch (group) {
                case "ETV":
                    fullGroupNames[i] = "Etická Výchova";
                    break;
                case "NBV":
                    fullGroupNames[i] = "Náboženská Výchova";
                    break;
                default:
                    fullGroupNames[i] = group.replace("sk", "skupina");
            }
        }
    }
}