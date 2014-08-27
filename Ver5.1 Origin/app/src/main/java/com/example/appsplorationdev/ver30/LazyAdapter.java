package com.example.appsplorationdev.ver30;

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

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
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

        View vi = convertView;

        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView creator = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView pubDate = (TextView)vi.findViewById(R.id.duration); // duration
        TextView hidURL = (TextView)vi.findViewById(R.id.hiditem); // hidden url
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        // Setting all values in listview
        title.setText(song.get(MainActivity.KEY_ARTICLE_TITLE));
        creator.setText(song.get(MainActivity.KEY_CATEGORY_NAME));
        pubDate.setText(song.get(MainActivity.KEY_ARTICLE_PUBDATE));
        hidURL.setText(song.get(MainActivity.KEY_ARTICLE_URL));
        imageLoader.DisplayImage(song.get(MainActivity.KEY_ARTICLE_THUMB_URL), thumb_image);

        return vi;
    }
}