package com.example.appsplorationdev.ver20;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by appsplorationdev on 8/12/14.
 */
public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);

        TextView title = (TextView)vi.findViewById(R.id.itm1); // title
        TextView artist = (TextView)vi.findViewById(R.id.itm2); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.itm3); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.imageView); // thumb image

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        // Setting all values in listview
   /*     title.setText(song.get(MainActivity.));
        artist.setText(song.get(MainActivity.KEY_ARTIST));
        duration.setText(song.get(MainActivity.KEY_DURATION));
        imageLoader.DisplayImage(song.get(MainActivity.KEY_THUMB_URL), thumb_image);*/
        return vi;
    }
}
