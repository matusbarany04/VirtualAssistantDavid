package com.david.game.davidnotifyme.edupage.readers;

import android.content.Context;

import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.davidnotifyme.utils.InternalStorageFile;

import org.json.JSONObject;

public class TimetableReader {

    public TimetableReader(Context context){
        InternalStorageFile internalStorageFile = new InternalStorageFile(context, InternalFiles.TIMETABLE);

        String rawJson = internalStorageFile.read();

        // JSONObject a čítať treba ...
        // JSONobject.getString(key)

        // finalny object by mal byť Subject

    }
}
