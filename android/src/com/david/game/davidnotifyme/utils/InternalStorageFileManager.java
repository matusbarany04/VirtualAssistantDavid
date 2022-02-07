package com.david.game.davidnotifyme.utils;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InternalStorageFileManager {
    public InternalStorageFileManager(Context context){
        File internalStoragePath = context.getFilesDir();

        File file = new File(context.getFilesDir(), "mynewfile.txt");

        String filename = "myfile";
        String fileContents = "Hello world!";
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
