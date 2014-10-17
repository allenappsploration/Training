package com.example.appsplorationdev.ver30;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by appsplorationdev on 10/13/14.
 */
public class SearchAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader image_Loader;
    private int width, height;

    public SearchAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
        this.activity = activity;
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        image_Loader = new ImageLoader(activity.getApplicationContext());

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        HashMap<String, String> song = data.get(position);

        String hasThumb = song.get(SearchActivity.KEY_ARTICLE_HAS_THUMB);

        if (convertView == null)

            Log.v("Has thumb : ", String.valueOf(hasThumb));                // remove this may cause crash happens

            if (hasThumb.equals("1")) {
                vi = inflater.inflate(R.layout.row_normal, null);
            }else {         //without thumbnail
                vi = inflater.inflate(R.layout.row_normal_no_image, null);
            }

            TextView title = (TextView) vi.findViewById(R.id.title); // title
            TextView pubDate = (TextView) vi.findViewById(R.id.duration); // duration
            TextView hidURL = (TextView) vi.findViewById(R.id.hiditem); // hidden url
            TextView hidthumbURL = (TextView) vi.findViewById(R.id.hidthumburl); // hidden thumbnail url
            TextView hidIsThumb = (TextView) vi.findViewById(R.id.hidisThumb);
            ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb image

            // Setting all values in listview
            title.setText(song.get(SearchActivity.KEY_ARTICLE_TITLE));
            pubDate.setText(song.get(SearchActivity.KEY_ARTICLE_PUBDATE));
            hidURL.setText(song.get(SearchActivity.KEY_ARTICLE_URL));
            hidthumbURL.setText(song.get(SearchActivity.KEY_SMALL_ARTICLE_THUMB_URL));
            hidIsThumb.setText(song.get(SearchActivity.KEY_ARTICLE_HAS_THUMB));

            if (hasThumb.equals("1")) {
                image_Loader.DisplayImage(song.get(SearchActivity.KEY_SMALL_ARTICLE_THUMB_URL), thumb_image);

                int width_ratio = (int) (width * 0.4);  // 40% of device width

                android.view.ViewGroup.LayoutParams layoutParams = thumb_image.getLayoutParams();
                layoutParams.width = (width_ratio);
                layoutParams.height = (int) (width_ratio / 1.56);
                thumb_image.setLayoutParams(layoutParams);

                thumb_image.requestLayout();
            }

        return vi;

    }
}
