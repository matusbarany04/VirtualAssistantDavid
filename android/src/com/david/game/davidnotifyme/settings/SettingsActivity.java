package com.david.game.davidnotifyme.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.david.game.R;
import com.david.game.davidnotifyme.david.David;
import com.david.game.davidnotifyme.notifications.BroadCastReceiver;
import com.david.game.davidnotifyme.notifications.DavidNotifications;
import com.david.game.debug.DebugActivity;


public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        int versionClicks = 0;
        int clickToDeveloper = 10;

        private boolean checkTimeAndUpdateNotification(Object newValue) {
            boolean isValid = David.skontrolujCas(newValue.toString());

            Intent notificationIntent = new Intent(getContext(), BroadCastReceiver.class);

            if (!isValid)
                Toast.makeText(getContext(), "Zadal si nesprávny čas!", Toast.LENGTH_SHORT).show();

            else getContext().sendBroadcast(notificationIntent);

            return isValid;
        }

        private boolean checkTimeAndUpdateMorning(Object newValue) {
            boolean isValid = David.skontrolujCas(newValue.toString());

            if (isValid)
                DavidNotifications.planMorningNotification(getContext(), (String) newValue);

            else Toast.makeText(getContext(), "Zadal si nesprávny čas!", Toast.LENGTH_SHORT).show();

            return isValid;
        }


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference showTime = findPreference("time_on");
            showTime.setOnPreferenceChangeListener((Preference preference1, Object newValue) -> checkTimeAndUpdateNotification(newValue));

            Preference hideTime = findPreference("time_off");
            hideTime.setOnPreferenceChangeListener((Preference preference1, Object newValue) -> checkTimeAndUpdateNotification(newValue));

            Preference showTimeMorning = findPreference("time_notify_morning");
            showTimeMorning.setOnPreferenceChangeListener((Preference preference1, Object newValue) -> checkTimeAndUpdateMorning(newValue));

            Preference morningNotify = findPreference("show_notify_morning");
            morningNotify.setOnPreferenceChangeListener((Preference preference1, Object newValue) -> {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> DavidNotifications.planMorningNotification(getContext(), null));
                return true;
            });

            Preference showNotifications = findPreference("show_notify");
            showNotifications.setOnPreferenceChangeListener((preference, showValue) -> {
                        Context con = getContext();

                        boolean shouldShow = Boolean.parseBoolean(showValue.toString());

                        if (shouldShow) {
                            if (BroadCastReceiver.shouldShowNotification(con)) {
                                Intent notificationIntent = new Intent(con, BroadCastReceiver.class);
                                con.sendBroadcast(notificationIntent);
                            } else {
                                Toast.makeText(con, "Zapnuté, ale oznámenie sa ukáže až neskôr...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            DavidNotifications.stopNotification(con, DavidNotifications.LESSON_ONGOING_NOTIFICATION);
                        }
                        return true;
                    }
            );

            PreferenceCategory developerOptions = findPreference("developer_category");

            Preference version = (Preference) findPreference("about_version");

            version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 Toast toast = Toast.makeText(getContext(), clickToDeveloper - versionClicks + " to become a developer!", Toast.LENGTH_LONG);

                 @Override
                 public boolean onPreferenceClick(Preference preference) {
                     Intent intent = new Intent(getContext(), DebugActivity.class);
                     startActivity(intent);
                     Context con = getContext();
                     Log.d("clicks", versionClicks + " ");
//                     version.setTitle(versionClicks + "");
                     versionClicks = versionClicks + 1;
                     if (versionClicks >= clickToDeveloper - 5 && clickToDeveloper - versionClicks >= 0) {
                         toast.cancel();
                         toast = Toast.makeText(getContext(), clickToDeveloper - versionClicks + " to become a developer!", Toast.LENGTH_LONG);
                         toast.show();
                     }
                     if (versionClicks >= clickToDeveloper) {

                         developerOptions.setVisible(true);
                     }
                     return true;
                 }
             }


            );
        }
    }
}