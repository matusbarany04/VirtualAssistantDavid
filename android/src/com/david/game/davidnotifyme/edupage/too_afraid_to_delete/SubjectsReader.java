package com.david.game.davidnotifyme.edupage.too_afraid_to_delete;

import android.content.Context;
import android.util.Log;

import com.david.game.davidnotifyme.edupage.timetable_objects.SemiSubject;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectsReader {
    Context context;
    ArrayList<SemiSubject> subjects;
    HashMap<Integer, SemiSubject> subjectsHashMap;

    public SubjectsReader(Context context){
        this.context = context;

        InternalStorageFile subjectsFileManager = new InternalStorageFile(context, InternalFiles.SUBJECTS);
        final String[] subjectsData = subjectsFileManager.read("/");

        subjects = new ArrayList<>();//new SemiSubject[subjectsData.length];

        int error_count = 0;
        for (int i = 0; i < subjectsData.length - error_count; i++) {
            String[] vals = subjectsData[i].split(":");
            if(vals.length < 3){
                error_count++;
//                Log.d("SUBJECTtik", subjectsData[i]);
                continue;
            }

            SemiSubject subject = new SemiSubject(vals[0], vals[1],vals[2]);



            this.subjects.add(subject);
        }
        Log.d("finished" ,"yes");
    }

    public SemiSubject[] getSubjects() {
        return (SemiSubject[]) subjects.toArray();
    }

    public String[] getIDs(){
        String[] output = new String[subjects.size()];
        for (int i = 0; i < output.length;i++){
            output[i] = String.valueOf(subjects.get(i).getId());
        }
        return output;
    }


    public String[] getClassNames(){
        String[] output = new String[subjects.size()];
        for (int i = 0; i < output.length;i++){
            output[i] = subjects.get(i).getName();
        }
        return output;
    }


    public HashMap<Integer, SemiSubject> getSubjectsAsHashMap(){

        if (subjectsHashMap == null){
            HashMap<Integer, SemiSubject> output = new HashMap<>();

            for (SemiSubject sub: this.subjects) {
                output.put(Integer.parseInt(sub.getId()), sub);
            }

            subjectsHashMap = output;
            return output;
        }else
            return subjectsHashMap;
    }
}
