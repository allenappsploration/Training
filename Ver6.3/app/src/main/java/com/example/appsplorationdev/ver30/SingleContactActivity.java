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
        setContentView(R.layout.activity_single);

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
        String subject = in.getStringExtra(KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(KEY_ARTICLE_THUMB_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, subject + "\n\n" + urlToShare);
        // Check if official Facebook app is found
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
            Toast.makeText(getApplicationContext(), "Facebook not installed", Toast.LENGTH_SHORT).show();
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareTwitter(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);
        String subject = in.getStringExtra(KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(KEY_ARTICLE_THUMB_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, subject + "\n\n" + urlToShare);
        // Check if official Twitter app is found
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
            Toast.makeText(getApplicationContext(), "Twitter not installed", Toast.LENGTH_SHORT).show();
            //WebView wb1 = (WebView)findViewById(R.id.webView);
            //wb1.loadUrl(sharerUrl);
        }
        //else
        //{
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        //}
    }

    private void shareGoogle(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);
        String subject = in.getStringExtra(KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(KEY_ARTICLE_THUMB_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, subject + "\n\n" + urlToShare);
        // Check if official Google+ app is found
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
            Toast.makeText(getApplicationContext(), "Google+ not installed", Toast.LENGTH_SHORT).show();
            //WebView wb1 = (WebView)findViewById(R.id.webView);
            //wb1.loadUrl(sharerUrl);
        }
        //else
        //{

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        //}
    }

    private void shareWhatsApp(){
        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);
        String subject = in.getStringExtra(KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(KEY_ARTICLE_THUMB_URL);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, subject + "\n\n" +urlToShare);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(thumburl));
        // Check if official WhatsApp is found
        boolean whatsAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.whatsapp")) {
                intent.setPackage(info.activityInfo.packageName);
                whatsAppFound = true;
                break;  }}
        // As fallback, launch sharer.php in a browser
        if (!whatsAppFound) {
//            String sharerUrl = "https://plus.google.com/share?url=" + urlToShare;
//            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            Toast.makeText(getApplicationContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            //WebView wb1 = (WebView)findViewById(R.id.webView);
            //wb1.loadUrl(sharerUrl);
        }
        //else
        //{

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        //}
    }

    private void shareSMS(){
        Intent in = getIntent();

        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);
        String subject = in.getStringExtra(KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(KEY_ARTICLE_THUMB_URL);

        Uri url = Uri.parse(thumburl);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setType(HTTP.PLAIN_TEXT_TYPE);
        intent.setData(Uri.parse("mmsto:"));
        intent.putExtra("sms_body", subject + "\n\n" + urlToShare);
        intent.putExtra(Intent.EXTRA_STREAM, url);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareEmail(){
        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String subject = in.getStringExtra(KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(KEY_ARTICLE_THUMB_URL);
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);

        // http://hot.appsploration.com/hot/thumbs/8/1960_b.jpg
        Uri url = Uri.parse(thumburl);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
//        intent.setType("message/rfc822");
//        intent.setType("application/image");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        intent.putExtra(Intent.EXTRA_STREAM, url);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void showPopup(){
        final Dialog d = new Dialog(this);
        d.setTitle("Share Via :");
        d.setContentView(R.layout.activity_share);

        String s[] = { "Email", "Facebook", "Google +", "Message", "Twitter", "WhatsApp"  };
        int image[] = { R.drawable.email_icon, R.drawable.facebook_logo, R.drawable.google_plus_icon, R.drawable.message_icon, R.drawable.twitter_logo, R.drawable.whatsapp_icon };

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
                R.layout.share_items, from, to);
        ListView lst1 = (ListView) d.findViewById(R.id.lvCustomChooser);
        lst1.setItemsCanFocus(true);
        lst1.setAdapter(listAdapter);
        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        shareEmail();
                        d.cancel();
                        break;

                    case 1:
                        shareFacebook();
                        d.cancel();
                        break;

                    case 2:
                        shareGoogle();
                        d.cancel();
                        break;

                    case 3:
                        shareSMS();
                        d.cancel();
                        break;

                    case 4:
                        shareTwitter();
                        d.cancel();
                        break;

                    case 5:
                        shareWhatsApp();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
//                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivityForResult(myIntent, 0);

                onBackPressed();
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
                return true;

            case R.id.menu_item_share:
                showPopup();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
