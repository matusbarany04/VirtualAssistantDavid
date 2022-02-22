package com.david.game.davidnotifyme.edupage.readers;

import android.content.Context;
import android.util.Log;

import com.david.game.davidnotifyme.edupage.EdupageSerializable;

import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

public class EdupageSerializableReader<T extends EdupageSerializable> {

    private final Supplier<? extends T> ctor;
    Context context;
    ArrayList<T> edu_objects;
    HashMap<Integer, T> subjectsHashMap;

    public EdupageSerializableReader(Context context, InternalFiles type, Supplier<? extends T> ctor) {
        this.context = context;
        this.ctor = Objects.requireNonNull(ctor);

        InternalStorageFile subjectsFileManager = new InternalStorageFile(context, type);
        final String[] subjectsData = subjectsFileManager.read("/");

        edu_objects = new ArrayList<>();

        int error_count = 0;
        for (int i = 0; i < subjectsData.length - error_count; i++) {
            String[] vals = subjectsData[i].split(":");
            if (vals.length < ctor.get().getParameterCount()) {
                error_count++;
                continue;
            }

            this.edu_objects.add((T) ctor.get().init(vals));
        }
        Log.d("finished", "yes");
    }

    public T[] getEdu_objects() {
        return (T[]) edu_objects.toArray();
    }

    public String[] getIds() {
        String[] output = new String[edu_objects.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = String.valueOf(edu_objects.get(i).getId());
        }
        return output;
    }


    public String[] getNames() {
        String[] output = new String[edu_objects.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = edu_objects.get(i).getName();
        }
        return output;
    }


    public HashMap<Integer, T> getsAsHashMap() {

        if (subjectsHashMap == null) {
            HashMap<Integer, T> output = new HashMap<>();

            for (T sub : this.edu_objects) {
                output.put(Integer.parseInt(sub.getId()), sub);
            }

            subjectsHashMap = output;
            return output;
        } else
            return subjectsHashMap;
    }
}
