package com.david.game.davidnotifyme.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    private String resolveEnum(InternalFiles fileName){
        String outputFileName = "";
        String suffix = ".txt";
        switch (fileName){
            case LUNCH:
                outputFileName+= "lunch";
                break;
            case CLASSES:
                outputFileName+= "classes";
                break;
            case TIMETABLE:
                outputFileName+= "timetable";
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

    public InternalStorageFile init()  {
        internalFile = new File(context.getFilesDir(), fileName);
        if (!internalFile.exists()) {
            try {
                internalFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }


    public void append() {
        FileWriter fr = null;
        try {
            fr = new FileWriter(internalFile, true);
            fr.write("data");
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {

        int length = (int) internalFile.length();

        char[] char_array = new char[length];

        try {

            FileInputStream fin = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fin);

            isr.read(char_array);
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(char_array);
    }

    public String[] readFile(String lineEnder) {
        return read().split(lineEnder);
    }

    public String getFileName() {
        return fileName;
    }


    public class CannotResolveFileEnumException extends Exception{
        public CannotResolveFileEnumException(String errorMessage){
            super(errorMessage);
        }
    }
}
