package com.example.saibot.test1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Saibot on 3/22/2015.
 */
public class MySimpleArrayAdapter extends ArrayAdapter<MyListObject> {
    private final Context context;
    private final List<MyListObject> values;
    private int resourceID;

    public MySimpleArrayAdapter(Context context, int resourceID, List<MyListObject> values) {
        super(context, resourceID, values);
        this.context = context;
        this.resourceID = resourceID;
        this.values = values;
    }

    private class ViewHolder {
        ImageView imageView;
        //TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceID, null);
            holder = new ViewHolder();
            //holder.textView = (TextView) convertView.findViewById(R.id.textID);
            holder.imageView = (ImageView) convertView.findViewById(R.id.PermissionItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyListObject rowItem = (MyListObject) values.get(position);
        //holder.textView.setText(rowItem.getCountry());
        holder.imageView.setImageResource(rowItem.getImage());

        return convertView;
    }

}
