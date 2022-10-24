package com.david.game.davidnotifyme.settings;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.david.game.R;
import com.david.game.davidnotifyme.MainActivity;
import com.david.game.davidnotifyme.david.David;

import com.david.game.davidnotifyme.david.Timetable;
import com.david.game.davidnotifyme.edupage.TimetableParser;
import com.david.game.davidnotifyme.edupage.readers.EdupageSerializableReader;
import com.david.game.davidnotifyme.edupage.timetable_objects.Classroom;
import com.david.game.davidnotifyme.edupage.timetable_objects.GroupnameGroup;
import com.david.game.davidnotifyme.edupage.timetable_objects.Groups;
import com.david.game.davidnotifyme.edupage.timetable_objects.StudentsClass;
import com.david.game.davidnotifyme.notifications.BroadCastReceiver;
import com.david.game.davidnotifyme.notifications.DavidNotifications;
import com.david.game.davidnotifyme.utils.InternalFiles;
import com.david.game.debug.DebugActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;


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

            ListPreference usersClass = findPreference("trieda");
            EdupageSerializableReader<StudentsClass> cr = new EdupageSerializableReader<>(getContext(), InternalFiles.CLASSES, StudentsClass::new);
            usersClass.setEntries(cr.getNames());
            usersClass.setEntryValues(cr.getIds());

            Timetable timetable = MainActivity.getDavid().ziskajRozvrh();
            createGroupPreferences(timetable);

            usersClass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    showRestartDialog();
                    return true;
                }
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

            Preference version = findPreference("about_version");

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
             });
        }

        private void createGroupPreferences(Timetable timetable) {
            PreferenceCategory category = findPreference("groups");
            category.removeAll();

            Set<String> groups = Groups.loadFromTimetable(timetable);

            for (String division : groups) {
                String[] divisionArray = division.split("/");
                ListPreference groupPreference = new ListPreference(getContext());
                groupPreference.setTitle(division);
                groupPreference.setValue(divisionArray[0]);
                groupPreference.setDialogTitle(groupPreference.getTitle());
                groupPreference.setKey("group-" + division); // zatiaľ dávam label možno zmeníme neskor
                groupPreference.setEntries(divisionArray);
                groupPreference.setEntryValues(divisionArray);
                setAutoSummaryProvider(groupPreference);
                category.addPreference(groupPreference);
            }

        }

        private void setAutoSummaryProvider(ListPreference preference) {
            preference.setSummaryProvider(preference1 -> {
                ListPreference listPreference = (ListPreference) preference1;
                return listPreference.getValue();
            });
        }

        private void showRestartDialog() {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage("Zmenil si triedu. Aplikácia sa teraz reštartuje.");
            dialog.setPositiveButton("OK", (dialogInterface, i) -> {
                Groups.deleteSavedData(getContext());
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }

                Runtime.getRuntime().exit(0);
            });
            dialog.show();
        }
    }
}