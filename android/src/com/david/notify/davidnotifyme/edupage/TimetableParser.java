package com.david.notify.davidnotifyme.edupage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.david.notify.R;
import com.david.notify.davidnotifyme.david.DavidClockUtils;
import com.david.notify.davidnotifyme.edupage.readers.EdupageSerializableReader;
import com.david.notify.davidnotifyme.edupage.timetable_objects.Classroom;
import com.david.notify.davidnotifyme.edupage.timetable_objects.GroupnameGroup;
import com.david.notify.davidnotifyme.edupage.timetable_objects.SemiSubject;
import com.david.notify.davidnotifyme.edupage.timetable_objects.StudentsClass;
import com.david.notify.davidnotifyme.edupage.timetable_objects.Subject;
import com.david.notify.davidnotifyme.utils.InternalFiles;
import com.david.notify.davidnotifyme.utils.InternalStorageFile;
import com.david.notify.davidnotifyme.utils.JSONparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class TimetableParser {
    static ArrayList<Day> timetable;
    //    DavidClockUtils
    Context context;
    HashMap<Integer, SemiSubject> subjectHashMap;
    HashMap<Integer, Classroom> classroomHashMap;
    HashMap<Integer, StudentsClass> classHashMap;

    public TimetableParser(Context context) {
        this.context = context;
        init();
    }

    public static String[] toStringArray(JSONArray array) {
        if (array == null)
            return null;

        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }

    public static ArrayList<Day> filterGroups(ArrayList<Day> timetable, String[] groupNames) {
        ArrayList<Day> localArray = new ArrayList<>(timetable);

        for (int j = 0; j < localArray.size(); j++) {
            Day day = localArray.get(j);
            Day localDay = new Day(day.getDate());

            for (int i = 0; i < day.getSubjectsArray().size(); i++) {
                Subject subject = day.get(i);
                if (subject.containsSubjectGroups(groupNames)) {
                    localDay.append(subject);
                }
            }
            localArray.set(j, localDay);
        }

        return localArray;
    }

    private void init() {
        EdupageSerializableReader<SemiSubject> semiSubjectReader = new EdupageSerializableReader<>(context, InternalFiles.SUBJECTS, SemiSubject::new);
        EdupageSerializableReader<Classroom> classroomReader = new EdupageSerializableReader<>(context, InternalFiles.CLASSROOM, Classroom::new);
        EdupageSerializableReader<StudentsClass> studentClassReader = new EdupageSerializableReader<>(context, InternalFiles.CLASSES, StudentsClass::new);
        this.subjectHashMap = semiSubjectReader.getsAsHashMapIdObject();
        this.classroomHashMap = classroomReader.getsAsHashMapIdObject();
        this.classHashMap = studentClassReader.getsAsHashMapIdObject();
        timetable = fillDays();
    }

    public ArrayList<Day> parse(JSONArray arrayOfSubjects) throws JSONException {
        Log.d("arrayOfSubjects", arrayOfSubjects.toString());

        for (int i = 0; i < arrayOfSubjects.length(); i++) {
            JSONObject obj = (JSONObject) arrayOfSubjects.get(i);
            try {
                if(obj.get("subjectid") == null){
                    Log.e("SEMI SUBJECT", obj.get("subjectid").toString() + " doesnt exist!!");
                }
                SemiSubject semiSubject = subjectHashMap.get(Integer.valueOf(obj.get("subjectid").toString()));
                if(semiSubject == null) {
                    Log.e("SEMI SUBJECT", obj.get("subjectid").toString() + " doesnt exist!!");
                }

                Subject subject;
                JSONArray classroomIds = ((JSONArray) obj.get("classroomids"));
                String[] groupnames = toStringArray(obj.getJSONArray("groupnames"));
                if(groupnames == null) {
                    Log.e("SEMI SUBJECT", "null!!!");
                }
                String id = classroomIds.length() > 0 ? classroomIds.getString(0) : "-1";
                EdupageSerializable s = classroomHashMap.get(Integer.parseInt(id));
                subject = new Subject(
                        obj.getString("starttime"),
                        obj.getString("endtime"),
                        s != null ? s.getName() : "iné",
                        semiSubject,
                        groupnames);
                timetable.get(findIndexOfDay(obj.getString("date"))).append(subject);
            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return timetable;
    }

    private ArrayList<Day> fillDays() {
        ArrayList<Day> out = new ArrayList<>();

        if (timetable != null && timetable.size() > 0 && timetable.get(0).subjectsArray.size() > 0) {
            return timetable;
        } else {
            for (String date : DavidClockUtils.getCurrentWeekDates()) { // dávať pozor pri zmene current week na last week  v edupage classe
                out.add(new Day(date));
            }
        }
        return out;
    }

    public static void resetTimetable() {
        timetable = new ArrayList<>();
    }

    private int findIndexOfDay(String date) {
        for (int i = 0; i < timetable.size(); i++) {
            Day day = timetable.get(i);
            if (day.getDate().equals(date)) {
                return i;
            }
        }
        return -1;
    }

    public void save() {
        InternalStorageFile saver = new InternalStorageFile(context, InternalFiles.TIMETABLE);

        JSONArray timetableArray = new JSONArray();
        for (Day day : timetable) {
            timetableArray.put(day.toJsonArray());
        }

        JSONObject finalJsonTimetable = new JSONObject();
        try {
            finalJsonTimetable.put("timetable", timetableArray);
            saver.clear();
            saver.append(finalJsonTimetable.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String[] getAllGroupNames() {
        HashSet<String> groupNames = new HashSet<>();

        for (Day d : timetable) {
            for (Subject s : d.getSubjectsArray()) {
                groupNames.addAll(Arrays.asList(s.subjectGroups));
            }
        }

        String[] output = new String[groupNames.size()];
        groupNames.toArray(output);

        return output;
    }

    @Deprecated //humus2 toto urcite nepisal matus
    private HashSet<GroupnameGroup> parseJSONGroupData() {
        String data = JSONparser.getFileData(context.getApplicationContext(), R.raw.groups);
        try {
            JSONObject dataObject = new JSONObject(data);
            JSONArray container = dataObject.getJSONArray("groups");
            HashSet<GroupnameGroup> outputArray = new HashSet<>();

            for (int i = 0; i < container.length(); i++) {
                JSONObject item = container.getJSONObject(i);

                JSONArray groupsJSONArray = item.getJSONArray("groups_array");
                String[] groupsArray = new String[groupsJSONArray.length()];
                for (int j = 0; j < groupsJSONArray.length(); j++) {
                    groupsArray[j] = groupsJSONArray.getString(j);
                }

                String label = item.getString("label");

                outputArray.add(new GroupnameGroup(groupsArray, label));
            }

            return outputArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashSet<GroupnameGroup> errorOutput = new HashSet<>();
        errorOutput.add(new GroupnameGroup(new String[]{"ERROR"}, "ERROR"));
        return errorOutput;
    }

    @Deprecated //fujky humus
    public GroupnameGroup[] getGroupOfGroupNames() {
        String[] allGroupNames = getAllGroupNames();
        HashSet<GroupnameGroup> groupsOutput = new HashSet<>();
        HashSet<GroupnameGroup> fileGroupData = parseJSONGroupData();
        String numbers = "0123456789";

        for (String s : allGroupNames) {
            boolean found = false;
            for (GroupnameGroup groupHolder : fileGroupData) {
                for (String group : groupHolder.getGroupnames()) {
                    if (group.equals(s)) {
                        fileGroupData.remove(groupHolder);
                        groupsOutput.add(groupHolder);
                        found = true;
                    }
                    if (found) break;
                }
                if (found) break;
            }
//            groupsOutput.add(c.toString());
        }

        return groupsOutput.toArray(new GroupnameGroup[0]);
    }

    public static class Day {
        ArrayList<Subject> subjectsArray;
        String date;

        public Day(String date) {
            subjectsArray = new ArrayList<>();
            this.date = date;
        }

        public Day(String date, ArrayList<Subject> subjectsArray) {
            this.subjectsArray = subjectsArray;
            this.date = date;
        }

        public Day(Day day) {
            this.subjectsArray = day.getSubjectsArray();
            this.date = day.getDate();
        }

        public ArrayList<Subject> getSubjectsArray() {
            return subjectsArray;
        }

        public void append(Subject subject) {
            subjectsArray.add(subject);
        }

        public Subject get(int index) {
            return subjectsArray.get(index);
        }

        public String getDate() {
            return date;
        }

        public JSONArray toJsonArray() {
            JSONArray array = new JSONArray();
            try {
                for (Subject sub : subjectsArray) {
                    array.put(sub.toJsonObject());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return array;
        }

        @NonNull
        @Override
        public String toString() {
            return subjectsArray.toString();
        }
    }

}
