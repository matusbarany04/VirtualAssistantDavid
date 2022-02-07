package com.david.game.davidnotifyme;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.app.ActivityManager;
import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;

import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.david.game.AndroidLauncher;
import com.david.game.R;
import com.david.game.davidnotifyme.david.ClassroomLocation;
import com.david.game.davidnotifyme.david.David;
import com.david.game.davidnotifyme.david.DavidClockUtils;
import com.david.game.davidnotifyme.edupage.EdupageScraper;
import com.david.game.davidnotifyme.lunch.LunchActivity;
import com.david.game.davidnotifyme.notifications.BroadCastReceiver;
import com.david.game.davidnotifyme.notifications.DavidNotifications;
import com.david.game.davidnotifyme.opengl.OpenglRenderer;
import com.david.game.davidnotifyme.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements AndroidFragmentApplication.Callbacks {
    David david;
    Intent notificationIntent;
    private GLSurfaceView mGLSurfaceView;
    private ClassroomLocation classroomLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_with_david);

        // Create libgdx fragment
        AndroidLauncher libgdxFragment = new AndroidLauncher();

        // Put it inside the framelayout (which is defined in the layout.xml file).
        getSupportFragmentManager().beginTransaction().
                add(R.id.frame, libgdxFragment).
                commit();

        try {
            new EdupageScraper();//.scrape("smh");
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationIntent = new Intent(this, BroadCastReceiver.class);
        Executor executor = runnable -> new Thread(runnable).start();

        startAnimations();

        Bundle extras = new Bundle();
        extras.putString("notificationType", "update");
        notificationIntent.putExtras(extras);

        david = new David(this, executor);

        scheduleNotifications(this, notificationIntent);

        initUI();

        initDavidGLView();
    }

    public static void scheduleNotifications(Context context, Intent intent) {
        ArrayList<Long> times = David.ziskajCasyAktualizacie(context);

        Handler handler = new Handler(Looper.getMainLooper());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < times.size(); i++) {
            Log.d("cas", times.get(i) + "");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    i, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + times.get(i), pendingIntent);

            if(context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                handler.postDelayed(mainActivity::updateTimetableData, times.get(i));
            }
            Log.d("timeMillis", times.get(i) + "");
        }

        DavidNotifications.planMorningNotification(context, null);
    }

    public void initUI() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        updateTimetableData();

        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        LinearLayout update = findViewById(R.id.updateNotification);
        update.setOnClickListener((View view) -> {
            updateTimetableData();
        });
        LinearLayout ulohy = findViewById(R.id.ulohy);
        ulohy.setOnClickListener((View view) -> {
            coming_soon();
        });
        LinearLayout obedy = findViewById(R.id.obedy);
        obedy.setOnClickListener((View view) -> {
            Intent lunchIntent = new Intent(this, LunchActivity.class);
            startActivity(lunchIntent);
        });

        LinearLayout location = findViewById(R.id.location);
        location.setOnClickListener((View view) -> {
            showClasroomInput();
           /* String url = "http://www.github.com/mtu4554";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);*/
        });

        sendBroadcast(notificationIntent);
    }

    private void updateTimetableData() {
        try {
            david.zistiSkupiny(this);

            String header = david.prebiehaHodina() ? david.ziskajPrebiehajucuHodinu().first : "Prestávka";

            String description = "";
            if(david.prebiehaHodina() && !david.bliziSaKoniecHodiny()) {
                description = david.ziskajPrebiehajucuHodinu().second;

            } else {
                Pair<String, String> message = david.ziskajDalsiuHodinuEdupage(true);
                description = String.format("%s\n%s", message.first, message.second);
            }

            if (description.contains("Zisťujem čo je na obed...")) checkLunch(description);

            TextView timetableTextView = findViewById(R.id.timetable);
            timetableTextView.setText(header);

            TextView details = findViewById(R.id.details);
            details.setText(description);

            sendBroadcast(notificationIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLunch(String description) {
            david.zistiNovyObed().nastavObedoveNacuvadlo(new David.OnObedNajdenyNacuvadlo() {

                @Override
                public void onObedNajdeny(ArrayList<String> data) {
                    int dayIndex = DavidClockUtils.zistiDen() - 1;
                    String newDescription;
                    Log.d("index", dayIndex + " " + data.size());
                    if(dayIndex < data.size()) {
                        String todayLunch = LunchActivity.formatLunch(data.get(dayIndex), ", ");
                        String formatLunch = getString(R.string.na_obed) + todayLunch;
                        newDescription = description.replace("Zisťujem čo je na obed...", formatLunch);
                    }
                    else newDescription = "Dneska obed nie je";
                    TextView details = findViewById(R.id.details);
                    details.post(() -> details.setText(newDescription));
                }
            });
    }

    private void startAnimations() {
        LinearLayout layout = findViewById(R.id.linearLayout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setTranslationX(-2000);
            child.animate().translationX(0).setDuration(500).setStartDelay(i * 100L + 300);
        }

        LinearLayout davidWords = findViewById(R.id.davidWords);
        davidWords.setAlpha(0);
        davidWords.setTranslationY(150);
        davidWords.animate().alpha(1).translationY(0).setDuration(3000).setStartDelay(500).setInterpolator(new FastOutSlowInInterpolator());
    }

    public void initDavidGLView() {
        mGLSurfaceView = findViewById(R.id.glDavidView); //new GLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            // Set the renderer to our demo renderer, defined below.
            mGLSurfaceView.setRenderer(new OpenglRenderer());
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }
    }

    private void showClasroomInput() {
        LinearLayout layout = findViewById(R.id.davidWords);

        classroomLocation = new ClassroomLocation(this);
        classroomLocation.inflateView(layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("app closing", "app closing");

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTimetableData();
        mGLSurfaceView.onResume();
        sendBroadcast(notificationIntent);
    }

    @Override
    protected void onPause() {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public void onBackPressed() {
        if(classroomLocation != null && classroomLocation.isShowed()) {
            classroomLocation.hide();
        } else {
            finish();
        }
    }

    public void coming_soon() {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exit() {

    }
}