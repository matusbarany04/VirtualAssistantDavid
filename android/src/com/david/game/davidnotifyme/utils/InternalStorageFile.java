package com.david.game.davidnotifyme.utils;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.david.game.davidnotifyme.edupage.EdupageSerializable;
import com.david.game.davidnotifyme.edupage.StudentsClass;
import com.david.game.davidnotifyme.edupage.Subject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Objects;

public class InternalStorageFile {
    Context context;
    String fileName;
    File internalFile;


    public InternalStorageFile(Context context, String fileName) {
        this.context = Objects.requireNonNull(context);
        this.fileName = Objects.requireNonNull(fileName);
        init();
    }

    public InternalStorageFile(Context context, InternalFiles fileName) {
        this.context = Objects.requireNonNull(context);
        this.fileName = resolveEnum(fileName);
        init();
    }

    private String resolveEnum(InternalFiles fileName) {
        String outputFileName = "";
        String suffix = ".txt";
        switch (fileName) {
            case LUNCH:
                outputFileName += "lunch";
                break;
            case CLASSES:
                outputFileName += "classes";
                break;
            case TIMETABLE:
                outputFileName += "timetable";
                break;
            case SUBJECTS:
                outputFileName += "subjects";
                break;
            default:
                try {
                    throw new CannotResolveFileEnumException("cannot resolve enum");
                } catch (CannotResolveFileEnumException e) {
                    e.printStackTrace();
                }
        }
        return outputFileName + suffix;
    }

    public InternalStorageFile init() {
        internalFile = new File(context.getFilesDir(), fileName);
        Log.d("FILES DIR", context.getFilesDir().toString());
        if (!internalFile.exists()) {
            try {
                internalFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }


    public boolean isEmpty() { // possible heavy operation
        return read().isEmpty();
    }

    public InternalStorageFile append(String data) {
        FileWriter fr = null;
        try {
            fr = new FileWriter(internalFile, true);
            fr.write(data);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void clear() {
        try {
            if(internalFile.delete())
                internalFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {

        int length = (int) internalFile.length();

        char[] char_array = new char[length];
        StringBuilder output = new StringBuilder();
        try {
            FileReader reader = new FileReader(internalFile);

            BufferedReader bReader = new BufferedReader(reader);
            String line;
            while ((line = bReader.readLine()) != null) {
                output.append(line);
            }
            FileInputStream fin = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fin);

            isr.read(char_array);
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        for (char a:char_array) {
//            System.out.println(a);
//        }
        return output.toString();//new String(char_array);
    }

    public String[] read(String lineEnder) {
        return read().split(lineEnder);
    }



    public EdupageSerializable[] readDeserializableSubjects() {
        String[] lines = read().split("/");
        Subject[] array = new Subject[lines.length];

        int errors = 0;
        for (int i = 0; i < lines.length - errors; i++) {
            String line = lines[i];
            Subject s = Subject.mutate(line);

            if (s != null)
                array[i - errors] = s;
            else
                errors++;
        }

        return array;
    }

    public String getFileName() {
        return fileName;
    }


    public class CannotResolveFileEnumException extends Exception {
        public CannotResolveFileEnumException(String errorMessage) {
            super(errorMessage);
        }
    }
}
