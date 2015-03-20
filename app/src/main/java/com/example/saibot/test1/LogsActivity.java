package com.example.saibot.test1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class LogsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);


        //TextView text_view = (TextView) findViewById(R.id.textView);
        //text_view.setBackgroundColor(@android:color/white);
    }


    protected void onResume() {
        super.onResume();
        // Scan for all BTLE devices.
        // The first one with the UART service will be chosen--see the code in the scanCallback.
        //writeLine("Scanning for devices...");
        //adapter.startLeScan(scanCallback);
        new getLogs().execute();
        Log.w("Debug", "Getting logs");
        TextView t;
        t = (TextView) findViewById(R.id.logView);
        t.setText("Loading Logs... Please wait");
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

    public void ClearLogs(View view) {
        new clearLogs().execute();
        new getLogs().execute();
        Log.w("Debug", "Getting logs");
        TextView t;
        t = (TextView) findViewById(R.id.logView);
        t.setText("Loading New Logs... Please wait");
    }
    private class clearLogs extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                cleerLogs();
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void cleerLogs() throws Exception {

        String device = "lock";
        String action = "logclear";
        String id = "ANDROID";

        URL ourServer = new URL("https://warm-sierra-7577.herokuapp.com/" +
                device + "?id=" + id + "&" + action + "=1");

        Log.d("mymsg", "https://warm-sierra-7577.herokuapp.com/" +
                device + "?id=" + id + "&" + action + "=1");

        URLConnection yc = ourServer.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(yc.getInputStream()));
        Log.d("mymsg", "~~~~~~~~~~~~~~~~~~Authorized Buttom Pressed ~~~~~~~~~~~~~~~~~~~~");
//      THIS IS FOR VERTICAL READING
//      int inputLine;
        String inputLine;
        final StringBuffer sb = new StringBuffer("Result:");
        Log.d("mymsg", "derp");

        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine + "\n");
        in.close();
// THIS FOR VERTICAL READING
/*        while ((inputLine = in.read()) != -1) {
            if (inputLine = " ") {
                sb.append((char) inputLine + "\n");
            } else {
                sb.append((char) inputLine);
            }
        }
        in.close();
*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView t;

                t = (TextView) findViewById(R.id.tv);
                t.setText(sb);
            }
        });
    }

    public void menu_clicked(View view) {
        Log.d("Home", "Menu button clicked from dashboard");

        toggleMenu();
    }

    public void logs_clicked(View view) {
        Log.w("Home", "logs button clicked");

        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }
    private class getLogs extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                getStuff();
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void getStuff() throws Exception {

        String device = "lock";
        String action = "viewlog";
        String id = "android0123456";

        URL ourServer = new URL("https://warm-sierra-7577.herokuapp.com/" +
                device + "?id=" + id + "&" + action + "=1");

        Log.d("mymsg", "https://warm-sierra-7577.herokuapp.com/" +
                device + "?id=" + id + "&" + action + "=1");

        URLConnection yc = ourServer.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(yc.getInputStream()));

//      THIS IS FOR VERTICAL READING
//      int inputLine;
        String inputLine;
        final StringBuffer sb = new StringBuffer("");
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine + "\n");
        in.close();
// THIS FOR VERTICAL READING
/*        while ((inputLine = in.read()) != -1) {
            if (inputLine = " ") {
                sb.append((char) inputLine + "\n");
            } else {
                sb.append((char) inputLine);
            }
        }
        in.close();
*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView t;

                t = (TextView) findViewById(R.id.logView);
                t.setText(sb);
            }
        });
    }
}
