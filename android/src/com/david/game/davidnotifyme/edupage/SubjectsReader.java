package com.david.game.davidnotifyme.edupage;

import android.accessibilityservice.FingerprintGestureController;
import android.content.Context;
import android.util.Log;

import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectsReader {
    Context context;
    ArrayList<Subject> subjects;
    HashMap<Integer, Subject> subjectsHashMap;

    public SubjectsReader(Context context){
        this.context = context;

        InternalStorageFile subjectsFileManager = new InternalStorageFile(context, InternalFiles.SUBJECTS);
        final String[] subjectsData = subjectsFileManager.read("/");

        subjects = new ArrayList<>();//new Subject[subjectsData.length];

        int error_count = 0;
        for (int i = 0; i < subjectsData.length - error_count; i++) {
            String[] vals = subjectsData[i].split(":");
            if(vals.length < 3){
                error_count++;
                Log.d("SUBJECTtik", subjectsData[i]);
                continue;
            }

            Subject subject = new Subject(vals[0], vals[1],vals[2]);



            this.subjects.add(subject);
        }
        Log.d("finished" ,"yes");
    }

    public Subject[] getSubjects() {
        return (Subject[]) subjects.toArray();
    }

    public String[] getIDs(){
        String[] output = new String[subjects.size()];
        for (int i = 0; i < output.length;i++){
            output[i] = String.valueOf(subjects.get(i).getClassroomNumber());
        }
        return output;
    }


    public String[] getClassNames(){
        String[] output = new String[subjects.size()];
        for (int i = 0; i < output.length;i++){
            output[i] = subjects.get(i).getSubjectName();
        }
        return output;
    }


    public HashMap<Integer, Subject > getSubjectsAsHashMap(){

        if (subjectsHashMap == null){
            HashMap<Integer, Subject> output = new HashMap<>();

            for (Subject sub: this.subjects) {
                output.put(sub.getClassroomNumber(), sub);
            }

            subjectsHashMap = output;
            return output;
        }else
            return subjectsHashMap;
    }
}
