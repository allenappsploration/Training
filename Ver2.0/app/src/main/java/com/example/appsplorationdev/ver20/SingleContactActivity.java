package com.example.appsplorationdev.ver20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by appsplorationdev on 8/11/14.
 */
public class SingleContactActivity extends Activity {

    // JSON node keys
    private static final String TAG_P1 = "title";
    private static final String TAG_P2 = "link";
    private static final String TAG_P3 = "comments";
    private static final String TAG_P4 = "pubDate";
    private static final String TAG_P5 = "creator";
    private static final String TAG_P6 = "category";
    private static final String TAG_P7 = "guid";
    private static final String TAG_P8 = "description";
    private static final String TAG_P9 = "commentRss";
    private static final String TAG_P10 = "thumbnail";
    private static final String TAG_P11 = "content";
    private static final String TAG_P12 = "twitter";




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String p1 = in.getStringExtra(TAG_P1);
        String p2 = in.getStringExtra(TAG_P2);
        String p3 = in.getStringExtra(TAG_P3);
        String p4 = in.getStringExtra(TAG_P4);
        String p5 = in.getStringExtra(TAG_P5);
        String p6 = in.getStringExtra(TAG_P6);
        String p7 = in.getStringExtra(TAG_P7);
        String p8 = in.getStringExtra(TAG_P8);
        String p9 = in.getStringExtra(TAG_P9);
        String p10 = in.getStringExtra(TAG_P10);
        String p11 = in.getStringExtra(TAG_P11);
        String p12 = in.getStringExtra(TAG_P12);

        // Displaying all values on the screen
        TextView lblP1 = (TextView) findViewById(R.id.itm1_label);
        TextView lblP2 = (TextView) findViewById(R.id.itm2_label);
        TextView lblP3 = (TextView) findViewById(R.id.itm3_label);
        TextView lblP4 = (TextView) findViewById(R.id.itm4_label);
        TextView lblP5 = (TextView) findViewById(R.id.itm5_label);
        TextView lblP6 = (TextView) findViewById(R.id.itm6_label);
        TextView lblP7 = (TextView) findViewById(R.id.itm7_label);
        TextView lblP8 = (TextView) findViewById(R.id.itm8_label);
        TextView lblP9 = (TextView) findViewById(R.id.itm9_label);
        TextView lblP10 = (TextView) findViewById(R.id.itm10_label);
        TextView lblP11 = (TextView) findViewById(R.id.itm11_label);
        TextView lblP12 = (TextView) findViewById(R.id.itm12_label);

        lblP1.setText(p1);
        lblP2.setText(p2);
        lblP3.setText(p3);
        lblP4.setText(p4);
        lblP5.setText(p5);
        lblP6.setText(p6);
        lblP7.setText(p7);
        lblP8.setText(p8);
        lblP9.setText(p9);
        lblP10.setText(p10);
        lblP11.setText(p11);
        lblP12.setText(p12);
    }
}
