package com.david.game.davidnotifyme.david;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.david.game.R;
import com.david.game.davidnotifyme.MainActivity;

import java.util.ArrayList;
import java.util.logging.LogRecord;

public class ClassroomLocation {

    private boolean showed;
    private final Activity activity;
    private final ArrayList<View> previousViews = new ArrayList<>();
    private ViewGroup layout;
    private GradientDrawable activeSchoolPart;
    private View view;
    private View school;

    public ClassroomLocation(Activity activity) {
        this.activity = activity;
    }

    public void inflateView(ViewGroup layout) {
        for(int i = 0; i < layout.getChildCount(); i++) {
            previousViews.add(layout.getChildAt(i));
        }
        this.layout = layout;
        layout.removeAllViews();
        view = activity.getLayoutInflater().inflate(R.layout.school_navigator, layout);
        view.setAlpha(0);
        view.animate().alpha(1).setDuration(1000);
        handleButton();
        showed = true;
    }

    private void handleButton() {
        Button button = view.findViewById(R.id.showLocation);
        button.setOnClickListener(v -> showLocation());
    }

    private void showLocation(){
        EditText editText = view.findViewById(R.id.classroomInput);

        SchoolNavigator schoolNavigator = new SchoolNavigator(activity);
        String location = schoolNavigator.whereIs(editText.getText().toString());

        ViewGroup view = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

        if(school == null) {
            school = activity.getLayoutInflater().inflate(R.layout.classroom_location, null);
            view.addView(school);
        }

        school.setAlpha(0);
        school.animate().alpha(1).setDuration(500);

        if(location.equals("sa nenašla")) location = "Takáto učebňa neexistuje.";

        showInSchool(location);
        hideKeyboard(activity);

        TextView textView = school.findViewById(R.id.classroomLocation);

        textView.setText(location);
    }

    private void showInSchool(String location) {

        LayerDrawable layerList = (LayerDrawable) activity.findViewById(R.id.school).getBackground();

        int id = getSchoolPartId(location);

        if(id != 0) {
            GradientDrawable shape = (GradientDrawable) layerList.findDrawableByLayerId(id);
            shape.setColor(Color.RED);

            if(shape != activeSchoolPart) {
                activeSchoolPart = shape;
                animateSchoolPart(shape);
            }
        } else {
            activeSchoolPart = null;
        }
    }

    private void animateSchoolPart(GradientDrawable part) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            boolean showing;
            int alpha = 100;
            @Override
            public void run() {
                part.setAlpha(alpha);
                if(showing) alpha += 10;
                else alpha -= 10;

                if(alpha >= 255 || alpha <= 50) {
                    if(alpha >= 255) alpha = 255;
                    showing = !showing;
                }

                if(activeSchoolPart == part) {
                    handler.postDelayed(this, 30);
                } else {
                    part.setColor(getSchoolShapesColor(activity));
                    part.setAlpha(255);
                }
            }
        }, 30);
    }

    private int getSchoolPartId(String floor) {
        if(floor.contains("poschodie") || floor.equals("Prízemie")) return R.id.hlavna_budova;
        switch (floor) {
            case "Severný trakt 1":
                return R.id.severny_trakt_1;
            case "Severný trakt 2":
                return R.id.severny_trakt_2;
            case "Južný trakt 1":
                return R.id.juzny_trakt_1;
            case "Južný trakt 2":
                return R.id.juzny_trakt_2;
            case "Jedáleň":
                return R.id.jedalen;
            case "Telocvičňa":
                return R.id.telocvicna;
            default:
                return 0;
        }
    }

    public static int getSchoolShapesColor(Context context) {
        try {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.schoolShapeColor, typedValue, true);
            return typedValue.data;
        } catch (Exception ignored) {
            return Color.BLACK;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hide() {
        layout.removeAllViews();
        //layout.removeView();
        previousViews.forEach(previousView -> layout.addView(previousView));

        ViewGroup view = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

        if(school != null) {
            view.removeView(school);
        }

        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.startAnimations();
        }

        showed = false;
    }

    public boolean isShowed() {
        return showed;
    }
}
