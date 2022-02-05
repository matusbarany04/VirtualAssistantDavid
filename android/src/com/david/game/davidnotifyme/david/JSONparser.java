package com.david.game.davidnotifyme.david;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONparser {

    public static String getFileData(Context context, int id) {
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            InputStream is = context.getResources().openRawResource(id);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + id + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + id + "'");
        }

        return builder.toString();
    }

}
