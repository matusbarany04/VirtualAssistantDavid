package com.david.game.debug;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.david.game.R;
import com.david.game.davidnotifyme.david.David;
import com.david.game.davidnotifyme.david.Timetable;


public class DebugActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        linearLayout = findViewById(R.id.debugLinearLayout);

        David david = new David(this, null);

        Timetable timetable = new Timetable(this);

        for (String lesson : timetable.getLessonsToday()) {
            addText(lesson);
        }

        addText(" ");
        addText("ziskaj dalsiu hodinu z edupage:");
        addText("SECOND: " +david.ziskajDalsiuHodinuEdupage(true).second);
        addText("FIRST: " + david.ziskajDalsiuHodinuEdupage(true).first);
        addText(" ");
        addText("Práve prebieha: ");
        addText(timetable.getCurrentLesson());

        addText(" ");
        addText("End of current lesson");
        addText(timetable.getEndOfCurrentLesson());

    }
    public void addText(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
        linearLayout.addView(textView);

    }
}