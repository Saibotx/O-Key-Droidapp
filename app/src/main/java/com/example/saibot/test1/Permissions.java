package com.example.saibot.test1;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListFragment;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Permissions extends Activity {

    public static final String[] countries = new String[] {"1", "2", "3", "4", "5"};
    public static final int[] images = new int[]{R.drawable.fb_logo_name, R.drawable.familly_logo_and_name, R.drawable.homejoy_logo_name, R.drawable.aliriaz, R.drawable.asad};
    ListView listView;
    List<MyListObject> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_permissions);
        //TextView textView = (TextView) findViewById(R.id.textID);

        values = new ArrayList<MyListObject>();
        for (int i = 0; i < countries.length; i++) {
            MyListObject item = new MyListObject();
            //item.setCountry(countries[i]);
            item.setImage(images[i]);
            values.add(item);
        }

        listView = (ListView) findViewById(R.id.listView);
        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, R.layout.rowlayout, values);
        listView.setAdapter(adapter);

    }



    @Override
    protected void onResume() {
        super.onResume();
        /* make list adapter here.
    */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_permissions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*
    private class PermissionsListAdapter extends BaseAdapter {
        private ArrayList<> mPermissionItems;
        private LayoutInflater mInflator;

    }
*/
}
