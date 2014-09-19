package com.example.appsplorationdev.ver30;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
    private int stepNumber;
    private int startCount;
    private int count;

    /*
    * @param startCount the initial number of views to show
    * @param stepNumber the number of additional views to show with each expansion
    * */
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d, int startC, int stepNo) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
        startCount = Math.min(startC, d.size());
        count = startCount;
        stepNumber = stepNo;

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

        if (convertView == null)
            Log.i("i", String.valueOf(position));
            if (position == 0) {
                vi = inflater.inflate(R.layout.featured_row, null);
            }
            if (position >= 1) {
                vi = inflater.inflate(R.layout.list_row, null);
            }

            TextView title = (TextView) vi.findViewById(R.id.title); // title
            TextView creator = (TextView) vi.findViewById(R.id.artist); // artist name
            TextView pubDate = (TextView) vi.findViewById(R.id.duration); // duration
            TextView hidURL = (TextView) vi.findViewById(R.id.hiditem); // hidden url
            ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb image

            HashMap<String, String> song = data.get(position);

            //Log.i("Position :", String.valueOf(position));
            // Setting all values in listview
            title.setText(song.get(MainActivity.KEY_ARTICLE_TITLE));
            creator.setText(song.get(MainActivity.KEY_CATEGORY_NAME));
            pubDate.setText(song.get(MainActivity.KEY_ARTICLE_PUBDATE));
            hidURL.setText(song.get(MainActivity.KEY_ARTICLE_URL));
            imageLoader.DisplayImage(song.get(MainActivity.KEY_ARTICLE_THUMB_URL), thumb_image);

        return vi;
    }

    /**
     * Show more views, or the bottom
     * @return true if the entire data set is being displayed, false otherwise
     */
    public boolean showMore(){
        if(count == data.size()) {
            return true;
        }else{
            count = Math.min(count + stepNumber, data.size()); //don't go past the end
            notifyDataSetChanged(); //the count size has changed, so notify the super of the change
            return endReached();
        }
    }

    /**
     * @return true if then entire data set is being displayed, false otherwise
     */
    public boolean endReached(){
        Log.i("i", "Count :" + String.valueOf(count) + " Data :" + String.valueOf(data.size()));
        return count == data.size();
    }

}