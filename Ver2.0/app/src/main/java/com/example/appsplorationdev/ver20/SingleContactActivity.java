package com.example.appsplorationdev.ver20;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by appsplorationdev on 8/11/14.
 */
public class SingleContactActivity extends KeyConfiguration {

    private WebView wvP1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String p2 = in.getStringExtra(TAG_LINK);

        // Displaying all values on the screen
        wvP1 = (WebView) findViewById(R.id.webView);
        wvP1.getSettings().setJavaScriptEnabled(true);
        wvP1.loadUrl(p2);


    }
}
