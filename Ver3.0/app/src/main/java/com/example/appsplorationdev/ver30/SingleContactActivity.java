package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

        wvP1.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wvP1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        wvP1.getSettings().getLoadsImagesAutomatically();
        //wvP1.getSettings().setSupportZoom(true);
        //wvP1.getSettings().setBuiltInZoomControls(true);
        wvP1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_INSET);
        wvP1.setScrollbarFadingEnabled(true);
        wvP1.getSettings().setLoadsImagesAutomatically(true);

        wvP1.setWebViewClient(new WebViewClient());
        wvP1.getSettings().setJavaScriptEnabled(true);

        if (savedInstanceState == null) {
            wvP1.loadUrl(p2);
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        wvP1.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        wvP1.restoreState(savedInstanceState);
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

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                return true;

            case R.id.menu_item_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                //shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareIntent);
                // getting intent data
                Intent in = getIntent();

                // Get JSON values from previous intent
                String link = in.getStringExtra(KEY_LINK);
                shareIntent.putExtra(Intent.EXTRA_TEXT, link);
                //startActivity(shareIntent);
                startActivity(Intent.createChooser(shareIntent, "Share Via"));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
/*
    public void showPopUp(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.show();
    }*/
}
