package com.david.game.debug;


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
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.david.game.R;
import com.david.game.davidnotifyme.david.David;
import com.david.game.davidnotifyme.david.Timetable;
import com.david.game.davidnotifyme.edupage.TimetableParser;
import com.david.game.davidnotifyme.edupage.timetable_objects.Groups;
import com.david.game.davidnotifyme.edupage.timetable_objects.Subject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class DebugActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    int LOCATION_REFRESH_TIME = 5000; // 5 seconds to update
    int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        linearLayout = findViewById(R.id.debugLinearLayout);

        David david = new David(this, null);

        Timetable timetable = david.ziskajRozvrh();

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
    }

    public void addText(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        linearLayout.addView(textView);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        getCurrentLocation();
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

private LocationListener locationListener = new LocationListener() {

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        TextView textView = new TextView(DebugActivity.this);
        textView.setText("Provider: " + provider);
        linearLayout.addView(textView);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        TextView textView = new TextView(DebugActivity.this);
        textView.setText("Status: " + provider + status);
        linearLayout.addView(textView);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        TextView textView = new TextView(DebugActivity.this);
        TextView textView2 = new TextView(DebugActivity.this);
        Log.d("gps", "Altitude: " + location.getAltitude() + "\nLatitude" + location.getLatitude() + "\nLongtitude: " + location.getLongitude());
        textView.setText("Latitude" + location.getLatitude() + "\nLongtitude: " + location.getLongitude());
        textView2.setText("Altitude: " + location.getAltitude());
        textView2.setTextColor(Color.RED);
        linearLayout.addView(textView);
        linearLayout.addView(textView2);
    }
};
}