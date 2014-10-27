package com.example.appsplorationdev.publisherapp;

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

public class adapter_Lazy extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    private int stepNumber;
    private int startCount;
    private int count;
    private int type;
    private int width, height;

    private static final int TYPE_FEATURED_NEWS = 0;
    private static final int TYPE_SUB_HEADER = 1;
    private static final int TYPE_ARTICLE_NEWS = 2;

    /*
    * @param startCount the initial number of views to show
    * @param stepNumber the number of additional views to show with each expansion
    * */
    public adapter_Lazy(Activity a, ArrayList<HashMap<String, String>> d, int startC, int stepNo) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
        startCount = Math.min(startC, d.size());
        count = startCount;
        stepNumber = stepNo;

        Display display = a.getWindowManager().getDefaultDisplay();
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
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                type = TYPE_FEATURED_NEWS;
                break;
            case 1:
                type = TYPE_SUB_HEADER;
                break;
            default:
                type = TYPE_ARTICLE_NEWS;
                break;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        int type = getItemViewType(position);
        HashMap<String, String> song = data.get(position);

        String hasThumb = song.get(Key.KEY_ARTICLE_HAS_THUMB);

        if (convertView == null)

            Log.v("Has thumb : ", String.valueOf(hasThumb));    /**/

            if (hasThumb.equals("1")) {
                switch (type) {
                    case 0:
                        vi = inflater.inflate(R.layout.row_featured, null);
                        break;
                    case 1:
                        vi = inflater.inflate(R.layout.row_subheader, null);
                        break;
                    default :
                        vi = inflater.inflate(R.layout.row_normal, null);
                        break;
                }
            }
            if (hasThumb.equals("0")){         //without thumbnail
                switch (type){
                    case 0:
                        vi = inflater.inflate(R.layout.row_featured_no_image, null);
                        vi.setMinimumHeight((int) (height * 0.25));
                        break;
                    case 1:
                        vi = inflater.inflate(R.layout.row_subheader_no_image, null);
                        break;
                    default:
                        vi = inflater.inflate(R.layout.row_normal_no_image, null);
                        break;
                }
            }
            TextView title = (TextView) vi.findViewById(R.id.title); // title
            TextView pubDate = (TextView) vi.findViewById(R.id.duration); // duration
            TextView hidURL = (TextView) vi.findViewById(R.id.hiditem); // hidden url
            TextView hidthumbURL = (TextView) vi.findViewById(R.id.hidthumburl); // hidden thumbnail url
            TextView hidIsThumb = (TextView) vi.findViewById(R.id.hidisThumb);
            ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb image

            // Setting all values in listview
            title.setText(song.get(Key.KEY_ARTICLE_TITLE));
            pubDate.setText(song.get(Key.KEY_ARTICLE_PUBDATE));
            hidURL.setText(song.get(Key.KEY_ARTICLE_URL));
            hidthumbURL.setText(song.get(Key.KEY_SMALL_ARTICLE_THUMB_URL));
            hidIsThumb.setText(song.get(Key.KEY_ARTICLE_HAS_THUMB));

            if (hasThumb.equals("1")) {
                imageLoader.DisplayImage(song.get(Key.KEY_SMALL_ARTICLE_THUMB_URL), thumb_image);

                if (type == 0) {                            // featured cell
                } else {
                    int width_ratio = (int) (width * 0.4);  // 40% of device width

                    ViewGroup.LayoutParams layoutParams = thumb_image.getLayoutParams();
                    layoutParams.width = (width_ratio);
                    layoutParams.height = (int) (width_ratio / 1.56);
                    thumb_image.setLayoutParams(layoutParams);

                    thumb_image.requestLayout();
                }
            }
        return vi;

    }
}