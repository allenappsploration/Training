package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

/**
 * Created by appsplorationdev on 8/11/14.
 */
public class SingleContactActivity extends KeyConfiguration {

    private WebView wvP1;
    private ShareActionProvider mShareActionProvider;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);


        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String p2 = in.getStringExtra(KEY_LINK);

        // Displaying all values on the screen
        wvP1 = (WebView) findViewById(R.id.webView);
        wvP1.getSettings().setJavaScriptEnabled(true);
        wvP1.loadUrl(p2);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);

        // Locate MEnuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
}
