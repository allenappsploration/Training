package com.example.appsplorationdev.ver30;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by appsplorationdev on 8/26/14.
 */
public class InfiniteScrollAdapter<T> extends ArrayAdapter<T> {
    private Activity context;
    private ArrayList values;
    private int count;
    private int stepNumber;
    private int startCount;

    public InfiniteScrollAdapter(Activity context, ArrayList values, int startCount, int stepNumber) {
        super(context, R.layout.row_normal, values);
        this.context = context;
        this.values = values;
        this.startCount = Math.min(startCount, values.size()); //don't try to show more views than we have
        this.count = this.startCount;
        this.stepNumber = stepNumber;
    }

    @Override
    public int getCount() {
        return count;
    }

    /**
     * Show more views, or the bottom
     * @return true if the entire data set is being displayed, false otherwise
     */
    public boolean showMore(){
        if(count == values.size()) {
            return true;
        }else{
            count = Math.min(count + stepNumber, values.size()); //don't go past the end
            notifyDataSetChanged(); //the count size has changed, so notify the super of the change
            return endReached();
        }
    }

    /**
     * @return true if then entire data set is being displayed, false otherwise
     */
    public boolean endReached(){
        return count == values.size();
    }

    /**
     * Sets the ListView back to its initial count number
     */
    public void reset(){
        count = startCount;
        notifyDataSetChanged();
    }
}
