package com.example.appsplorationdev.publisherapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by appsplorationdev on 9/19/14.
 */
public class adapter_NaviDrawer extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public adapter_NaviDrawer(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null)
            vi = inflater.inflate(R.layout.drawer_items, null);

            TextView id = (TextView) vi.findViewById(R.id.hidID);
            TextView name = (TextView) vi.findViewById(R.id.tvDrawer);

            HashMap<String, String> itemList = data.get(position);

            id.setText(itemList.get(Key.KEY_ID));
            name.setText(itemList.get(Key.KEY_NAME));

        return vi;
    }
}
