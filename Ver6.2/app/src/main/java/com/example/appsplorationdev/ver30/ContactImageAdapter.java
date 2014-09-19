package com.example.appsplorationdev.ver30;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by appsplorationdev on 8/18/14.
 */
public class ContactImageAdapter extends ArrayAdapter<Contact> {
    Context context;
    int layoutResourceId;
    ArrayList<Contact> data = new ArrayList<Contact>();

    public ContactImageAdapter(Context context, int layoutResourceId, ArrayList<Contact> data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ImageHolder();
            holder.label = (TextView)row.findViewById(R.id.label);
            holder.icon = (ImageView)row.findViewById(R.id.icon);
            row.setTag(holder);
        }
        else
        {
            holder = (ImageHolder)row.getTag();
        }

        Contact myImage = data.get(position);
        holder.label.setText(myImage.name);
        int outImage = myImage.image;
        holder.icon.setImageResource(outImage);

        return row;
    }

    static class ImageHolder
    {
        ImageView icon;
        TextView label;
    }
}
