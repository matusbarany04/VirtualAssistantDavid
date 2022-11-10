package com.david.notify.debug;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.david.notify.R;
import com.david.notify.davidnotifyme.david.David;
import com.david.notify.davidnotifyme.david.Timetable;
import com.david.notify.davidnotifyme.edupage.TimetableParser;
import com.david.notify.davidnotifyme.edupage.timetable_objects.Groups;
import com.david.notify.davidnotifyme.edupage.timetable_objects.Subject;
import com.google.android.material.slider.Slider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DebugActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    int LOCATION_REFRESH_TIME = 5000; // 5 seconds to update
    int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    Slider slider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        linearLayout = findViewById(R.id.debugLinearLayout);

        David david = new David(this, null);

        Timetable timetable = david.ziskajRozvrh();
        slider =  findViewById(R.id.slider);
        timetable.addOnLoadListener(new Timetable.OnLoadListener() {
            @Override
            public void onLoadTimetable(Timetable timetable) {

                addText(" ");
                addText("ziskaj dalsiu hodinu z edupage:");
                addText("SECOND: " + david.ziskajDalsiuHodinuEdupage(true).second);
                addText("FIRST: " + david.ziskajDalsiuHodinuEdupage(true).first);
                addText(" ");
                addText("Práve prebieha: ");
                addText(timetable.getCurrentLessonName());

                addText(" ");
                addText("End of current lesson");
                addText(timetable.getEndOfCurrentLesson());

                addText("rozvrh bez skupin");
                StringBuilder timetableText = new StringBuilder();
                timetable.getFullTimetable()
                        .forEach(day -> {
                            day.getSubjectsArray().forEach((Subject s)-> timetableText.append(s.shortName).append(" "));
                            timetableText.append("\n");
                        });

                addText(timetableText.toString());

                String[] groups = Groups.getSavedGroups(DebugActivity.this);
                ArrayList<TimetableParser.Day> filtered  =  TimetableParser.filterGroups(timetable.getFullTimetable(), groups);
                addText("rozvrh so skupinami");
                addText("skupiny> " + String.join(" ", groups));
                StringBuilder filteredTimetableText = new StringBuilder();
                filtered
                        .forEach(day -> {
                            day.getSubjectsArray().forEach((Subject s)-> filteredTimetableText.append(s.shortName).append(" "));
                            filteredTimetableText.append("\n");
                        });

                addText(filteredTimetableText.toString());

                getCurrentLocation();


            }
        });

        for (String lesson : timetable.getLessonsToday(true)) {
            addText(lesson);
        }
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                readLogs();

//                Toast.makeText(DebugActivity.this, "This method is run every 0.5 seconds", Toast.LENGTH_SHORT).show();
            }
        }, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

    }

    public void readLogs(){
       // new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("logcat -t " +(int) slider.getValue());
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                StringBuilder log=new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    log.append(line);
                }

//                runOnUiThread(()->{

                    TextView tv = (TextView) findViewById(R.id.logs);
                    tv.setText(log.toString());

//                });

            }
            catch (IOException e) {
                Log.e("Debugerror", e.getMessage().toString());
            }
       // }).start();
    }
    public void addText(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        linearLayout.addView(textView);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //getCurrentLocation();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "false");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
            return;
        }
        TextView textView = new TextView(DebugActivity.this);
        linearLayout.addView(textView);
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
       // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

//private LocationListener locationListener = new LocationListener() {
//
//    @Override
//    public void onProviderEnabled(@NonNull String provider) {
//        TextView textView = new TextView(DebugActivity.this);
//        textView.setText("Provider: " + provider);
//        linearLayout.addView(textView);
//    }
//
//    @Override
//    public void onProviderDisabled(@NonNull String provider) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        TextView textView = new TextView(DebugActivity.this);
//        textView.setText("Status: " + provider + status);
//        linearLayout.addView(textView);
//    }
//
//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//
//        TextView textView = new TextView(DebugActivity.this);
//        TextView textView2 = new TextView(DebugActivity.this);
//        Log.d("gps", "Altitude: " + location.getAltitude() + "\nLatitude" + location.getLatitude() + "\nLongtitude: " + location.getLongitude());
//        textView.setText("Latitude" + location.getLatitude() + "\nLongtitude: " + location.getLongitude());
//        textView2.setText("Altitude: " + location.getAltitude());
//        textView2.setTextColor(Color.RED);
//        linearLayout.addView(textView);
//        linearLayout.addView(textView2);
//    }
//};

}