package com.david.notify.davidnotifyme.edupage.readers;

import android.content.Context;


import com.david.notify.davidnotifyme.edupage.EdupageSerializable;

import com.david.notify.davidnotifyme.utils.InternalFiles;
import com.david.notify.davidnotifyme.utils.InternalStorageFile;

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


    public HashMap<Integer, T> getsAsHashMapIdObject() {

        if (subjectsHashMap == null) {
            HashMap<Integer, T> output = new HashMap<>();

            for (T subject : this.edu_objects) {
                output.put(Integer.parseInt(subject.getId()), subject);
            }

            subjectsHashMap = output;
            return output;
        } else
            return subjectsHashMap;
    }

    public HashMap<String, T> getsAsHashMapNameObject() {

        HashMap<String, T> output = new HashMap<>();

        for (T sub : this.edu_objects) {
            output.put(sub.getName(), sub);
        }

        return output;

    }
}
