package com.david.game.davidnotifyme.lunch;


import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.david.game.R;
import com.david.game.davidnotifyme.david.David;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;

public class LunchActivity extends AppCompatActivity {

    David david;
    public AnimationDrawable animationDrawable;
    public ImageView mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);
        david = new David(this, null);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.setTitle("Obedy");
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Obedy");
        }

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setBackgroundResource(R.drawable.david_is_loading);
        animationDrawable =(AnimationDrawable)mProgressBar.getBackground();
        mProgressBar.setVisibility(View.VISIBLE);

        if(David.maPristupKInternetu(this)) {
            animationDrawable.start();
        } else {
            mProgressBar.setBackground(null);
            mProgressBar.setImageResource(R.drawable.david_bez_internetu);
            TextView textView = findViewById(R.id.zistovanieText);
            textView.setText(R.string.nejde_internet);
        }

        david.zistiNovyObed().nastavObedoveNacuvadlo(new David.OnObedNajdenyNacuvadlo() {
            @Override
            public void onObedNajdeny(ArrayList<String> data) {
                LinearLayout layout = findViewById(R.id.loadingLunches);
                layout.post(() -> layout.setVisibility(View.INVISIBLE));
                loadLaunchList(data);
                animationDrawable.stop();
            }
        });
    }

    private void loadLaunchList(ArrayList<String> data) {
        String[] days = {"Pondelok", "Utorok", "Streda", "Štvrtok", "Piatok"};
        LinearLayout layout = findViewById(R.id.lunchTable);

        int dayOfWeek = getDay();
        Log.d("day", dayOfWeek + "");

        for(int i = 0; i < data.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.lunch_item, null);

            TextView day = view.findViewById(R.id.day);
            day.setText(days[i]);

            TextView lunch = view.findViewById(R.id.lunch);
            lunch.setText(formatLunch(data.get(i), "\n"));

            int color = Color.parseColor("#50a1a1a1");
            if(dayOfWeek == i) view.setBackground(new ColorDrawable(color));

            runOnUiThread(() -> layout.addView(view));
        }
    }

    public static String formatLunch(String lunch, String divider) {
        StringBuilder lunchBuilder = new StringBuilder(lunch);
        int index = 0;
        for(char character : lunch.toCharArray()) {
            if(index > 0 && Character.isUpperCase(character)) {
                lunchBuilder.replace(index - 1, index, divider);
                index += divider.length() - 1;
            }
            index++;
        }
        return lunchBuilder.toString();
    }

    private int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK) - 2;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}