package com.david.notify.debug;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.david.notify.R;
import com.david.notify.davidnotifyme.utils.InternalFiles;
import com.david.notify.davidnotifyme.utils.InternalStorageFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Debugger extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debugger);
        getSupportActionBar().setTitle("Logs");
       readLogs();

    }

    public static void startLogging() {
        new Thread(()->{


        });
    }

    public void readLogs(){
        // new Thread(() -> {
        try {
            Process process = Runtime.getRuntime().exec("logcat -t 1000");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            TextView textView = findViewById(R.id.logsView);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            StringBuilder log =new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                int offset = 0;
                while(!Character.isAlphabetic(line.charAt(offset))) offset++;
                //line = line.substring(offset);

                SpannableStringBuilder logLine = new SpannableStringBuilder(line);
                int color = Color.WHITE;
                switch(line.substring(offset).charAt(0)){
                    case 'W':
                        color = Color.YELLOW;
                        break;
                    case 'E':
                        color = Color.RED;
                        break;
                }

                logLine.setSpan(new ForegroundColorSpan(color), 0, logLine.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(logLine);


                //textView.append(line).append("\n");
                //textView.append(line, TextView.BufferType.SPANNABLE);
                builder.append("\n");
            }

//                runOnUiThread(()->{

            textView.setText( builder, TextView.BufferType.SPANNABLE);

            //textView.setText(log);
            ScrollView scrollView = findViewById(R.id.logsScroll);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
//                });

        }
        catch (IOException e) {
            Log.e("Debugerror", e.getMessage().toString());
        }
        // }).start();
    }
}