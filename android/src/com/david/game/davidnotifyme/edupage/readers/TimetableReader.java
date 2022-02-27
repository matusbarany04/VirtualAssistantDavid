package com.david.game.davidnotifyme.edupage.readers;

import android.content.Context;
import android.util.Log;

import com.david.game.davidnotifyme.edupage.timetable_objects.Subject;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;
import com.david.game.davidnotifyme.utils.JSONparser;

import org.json.JSONException;
import org.json.JSONObject;

public class TimetableReader {

    public TimetableReader(Context context){
        InternalStorageFile internalStorageFile = new InternalStorageFile(context, InternalFiles.TIMETABLE);

        String rawJson = internalStorageFile.read();

        Log.d("rawJSON", rawJson);

        try {
            JSONObject jsonObject = new JSONObject(rawJson);
            Subject subject = Subject.fromJsonObject(jsonObject);

            Log.d("subject", subject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // JSONObject a čítať treba ...
        // JSONobject.getString(key)

        // finalny object by mal byť Subject
    }



}
