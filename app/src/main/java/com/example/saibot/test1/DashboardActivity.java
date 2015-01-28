package com.example.saibot.test1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


public class DashboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void toggleMenu() {
        // *** NOTE! The animation below is not working, only the onAnimationEnd function
        final FrameLayout menuLayout = (FrameLayout) findViewById(R.id.menuLayout);

        // Animations not working, put in the the below code into onAnimationEnd when it is
        Integer left = menuLayout.getLeft();
        Log.w("Debug", left.toString());
        int x = -menuLayout.getWidth();
        if (left != 0) {
            x = 0;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(menuLayout.getWidth(), menuLayout.getHeight());
        lp.setMargins(x, 0, 0, 0);
        menuLayout.setLayoutParams(lp);
        Log.w("toggleMenu", left.toString());
    }

    public void menu_toggle(View view) {
        Log.w("Menu Toggle Button", "Menu Toggle Button clicked from menu");

        toggleMenu();
    }

    public void menu_clicked(View view) {
        Log.d("dashboard", "Menu button clicked from dashboard");

        toggleMenu();
    }

    public void go_home(View view) {
        Log.w("dashboard", "Home Button Clicked");

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
