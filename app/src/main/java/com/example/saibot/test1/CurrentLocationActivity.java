package com.example.saibot.test1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.view.View.*;


public class CurrentLocationActivity extends Activity {

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        bindImageButton();
    }

    public void bindImageButton() {
        imageButton = (ImageButton) findViewById(R.id.menuButton);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("menubutton", "Menu Button has been Clicked");

                toggleMenu();
            }
        });
    }

    public void toggleMenu() {
        // *** NOTE! The animation below is not working, only the onAnimationEnd function
        final FrameLayout menuLayout = (FrameLayout) findViewById(R.id.menuLayout);

        // Animations not working, put in the the below code into onAnimationEnd when it is
        Integer left = menuLayout.getLeft();
        Log.w("Debug", left.toString());
        int x = -menuLayout.getWidth();
        if ( left != 0 ) {
            x = 0;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(menuLayout.getWidth(), menuLayout.getHeight());
        lp.setMargins(x, 0, 0, 0);
        menuLayout.setLayoutParams(lp);
        Log.w("toggleMenu", left.toString());

//        TranslateAnimation anim = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF, 0,
//                Animation.RELATIVE_TO_PARENT, x,
//                Animation.RELATIVE_TO_SELF, 0,
//                Animation.RELATIVE_TO_SELF, 0);
//
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setFillAfter(true);
//        // ** Note: When the animation is working, turn this duration back up
//        anim.setDuration(100);
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                menuLayout.clearAnimation();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        menuLayout.startAnimation(anim);
    }

    public void cl_seelogs(View view) {
        Log.d("okey", "see logs from current location!");

        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }

    public void menu_toggle(View view) {
        Log.w("Menu Toggle Button", "Menu Toggle Button clicked from menu");

        toggleMenu();
    }

    public void go_dashboard(View view) {
        Log.d("go_dashboard", "Click from go_dashboard function");

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
