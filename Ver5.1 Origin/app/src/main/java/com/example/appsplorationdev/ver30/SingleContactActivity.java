package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by appsplorationdev on 8/11/14.
 */
public class SingleContactActivity extends KeyConfigure {

    private WebView wvP1;
    private ShareActionProvider mShareActionProvider;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        final String p2 = in.getStringExtra(KEY_ARTICLE_URL);

        // Displaying all values on the screen
        wvP1 = (WebView) findViewById(R.id.webView);

        wvP1.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wvP1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        wvP1.getSettings().getLoadsImagesAutomatically();
        wvP1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_INSET);
        wvP1.setScrollbarFadingEnabled(true);
        wvP1.getSettings().setLoadsImagesAutomatically(true);

        wvP1.setWebViewClient(new WebViewClient());
        wvP1.getSettings().setJavaScriptEnabled(true);

        if (savedInstanceState == null) {
            wvP1.loadUrl(p2);
        }

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final SeekBar seekbar = (SeekBar)findViewById(R.id.sbFontSize);
        seekbar.setMax(5);
        seekbar.setBackgroundDrawable(null);
        seekbar.setSecondaryProgress(10);
        seekbar.setScrollbarFadingEnabled(true);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int seekValue = wvP1.getSettings().getDefaultFixedFontSize();

                switch (progress){
                    case 0:
                        wvP1.getSettings().setTextZoom(100);
                        break;
                    case 1:
                        wvP1.getSettings().setTextZoom(110);
                        break;
                    case 2:
                        wvP1.getSettings().setTextZoom(120);
                        break;
                    case 3:
                        wvP1.getSettings().setTextZoom(130);
                        break;
                    case 4:
                        wvP1.getSettings().setTextZoom(140);
                        break;
                    case 5:
                        wvP1.getSettings().setTextZoom(150);
                        break;
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

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

    private void shareFacebook(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;  }}
        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            Toast.makeText(getApplicationContext(), "Facebook app not found", Toast.LENGTH_SHORT).show();
        }
            startActivity(intent);
    }

    private void shareTwitter(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        // See if official Facebook app is found
        boolean twitterAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter.android")) {
                intent.setPackage(info.activityInfo.packageName);
                twitterAppFound = true;
                break;  }}
        // As fallback, launch sharer.php in a browser
        if (!twitterAppFound) {
            String sharerUrl = "https://twitter.com/intent/tweet?url=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            Toast.makeText(getApplicationContext(), "Twitter app not found", Toast.LENGTH_SHORT).show();
            //WebView wb1 = (WebView)findViewById(R.id.webView);
            //wb1.loadUrl(sharerUrl);
        }
        //else
        //{
            startActivity(intent);
        //}
    }

    private void shareGoogle(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        // See if official Facebook app is found
        boolean googleAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.google.android.apps.plus")) {
                intent.setPackage(info.activityInfo.packageName);
                googleAppFound = true;
                break;  }}
        // As fallback, launch sharer.php in a browser
        if (!googleAppFound) {
            String sharerUrl = "https://plus.google.com/share?url=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            Toast.makeText(getApplicationContext(), "Google+ app not found", Toast.LENGTH_SHORT).show();
            //WebView wb1 = (WebView)findViewById(R.id.webView);
            //wb1.loadUrl(sharerUrl);
        }
        //else
        //{
            startActivity(intent);
        //}
    }

    private void shareEmail(){
        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        startActivity(intent);
    }


    public void showPopup(){
        final Dialog d = new Dialog(this);
        d.setTitle("Share Via :");
        d.setContentView(R.layout.about_dialog);

        String s[] = { "Facebook", "Twitter", "Google +", "Email" };
        int image[] = { R.drawable.facebook_logo, R.drawable.twitter_logo, R.drawable.google_plus_icon, R.drawable.email_icon };

        ArrayList<HashMap<String, String>> objArayList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < s.length; i++) {
            HashMap<String, String> listData = new HashMap<String, String>();
            listData.put("text", s[i]);
            listData.put("image", Integer.toString(image[i]));

            objArayList.add(listData);

        }

        String[] from = { "image", "text" };
        int[] to = { R.id.icon, R.id.label };

        SimpleAdapter listAdapter = new SimpleAdapter(this, objArayList,
                R.layout.row, from, to);
        ListView lst1 = (ListView) d.findViewById(R.id.lvCustomChooser);
        lst1.setItemsCanFocus(true);
        lst1.setAdapter(listAdapter);
        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        shareFacebook();
                        d.cancel();
                        break;

                    case 1:
                        shareTwitter();
                        d.cancel();
                        break;

                    case 2:
                        shareGoogle();
                        d.cancel();
                        break;

                    case 3:
                        shareEmail();
                        d.cancel();
                        break;

                    default:
                        return;
                        //Toast.makeText(getApplicationContext(), "Something has gone wrong, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        d.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                return true;

            case R.id.menu_item_share:
                showPopup();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
