package com.example.appsplorationdev.ver30;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by appsplorationdev on 8/18/14.
 */
public class CustomAppList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;

    public CustomAppList(Activity context, String[] web, Integer[] imageId) {
        super(context, R.layout.row, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    @Override
    public  View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row, null, true);
        TextView txtTitle = (TextView)rowView.findViewById(R.id.label);
        txtTitle.setText(web[position]);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.icon);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
