package com.example.appsplorationdev.publisherapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by appsplorationdev on 8/11/14.
 */
public class Activity_SingleArticle extends Activity{

    private WebView wvP1;
    private int zoomSize;

    Boolean isCameFromMain;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {
            setContentView(R.layout.activity_single);

            if (getIntent().getExtras() != null) {
                Bundle b = getIntent().getExtras();
                isCameFromMain = b.getBoolean("fromMainActivity");
            }

            // getting intent data
            Intent in = getIntent();

            // Get JSON values from previous intent
            final String articleURL = in.getStringExtra(Key.KEY_ARTICLE_URL);
            final String notificationURL = in.getStringExtra("title");             // Get URL from notification with TAG

            wvP1 = (WebView) findViewById(R.id.webView);
            ImageButton ib_share = (ImageButton) findViewById(R.id.iv_share);
            ImageButton ib_zoomIn = (ImageButton) findViewById(R.id.iv_zoomIn);
            ImageButton ib_zoomOut = (ImageButton) findViewById(R.id.iv_zoomOut);


            wvP1.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            wvP1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            wvP1.getSettings().getLoadsImagesAutomatically();
            wvP1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_INSET);
            wvP1.setScrollbarFadingEnabled(true);
            wvP1.getSettings().setLoadsImagesAutomatically(true);

            wvP1.getSettings().setJavaScriptEnabled(true);

            wvP1.setWebViewClient(new MyWebViewClient());

            if (savedInstanceState == null) {
                cd = new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    if (isCameFromMain == true) {
                        wvP1.loadUrl(articleURL);
                    } else {
                        wvP1.loadUrl(notificationURL);
                    }
                }else {
                    showAlertDialog(Activity_SingleArticle.this, "Network Error", "No Internet Connection");
                }
            }

            final ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);


            zoomSize = wvP1.getSettings().getTextZoom();

            ib_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSharer();
                }
            });

            ib_zoomOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomSize = wvP1.getSettings().getTextZoom();
                    zoomSize = zoomSize - 10;

                    if (zoomSize >= 100) {
                        wvP1.getSettings().setTextZoom(zoomSize);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.max_zoomOut, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ib_zoomIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomSize = wvP1.getSettings().getTextZoom();
                    zoomSize = zoomSize + 10;

                    if (zoomSize < 150) {
                        wvP1.getSettings().setTextZoom(zoomSize);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.max_zoomIn, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            /* -------------- Push Woosh Initialization ----------------*/

            //Register receivers for push notifications
            registerReceivers();

            //Create and start push manager
            PushManager pushManager = PushManager.getInstance(this);

            //Start push manager, this will count app open for Pushwoosh stats as well
            try {
                pushManager.onStartup(this);
            }
            catch(Exception e)
            {
                //push notifications are not available or AndroidManifest.xml is not configured properly
                e.printStackTrace();
            }

            //Register for push!
            pushManager.registerForPushNotifications();

            checkMessage(getIntent());

            /*----------- End of Push Woosh initialization -------------*/
        }else {
            showAlertDialog(Activity_SingleArticle.this, "Network Error", "No Internet Connection");
        }
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            if (isInternetPresent) {

                Intent in = new Intent(getApplicationContext(), Activity_EmbedBrowser.class);
                in.putExtra("embedURL", url);
                startActivity(in);

                overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
            }else {
                showAlertDialog(Activity_SingleArticle.this, "Network Error", "No Internet Connection");
            }
            return true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ){
        super.onSaveInstanceState(outState);
        wvP1.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        wvP1.restoreState(savedInstanceState);
    }

    BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver()
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
            checkMessage(intent);
        }
    };

    private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent) {}
    };

    //Registration of the receivers
    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers(){
        //Unregister receivers on pause
        try
        {
            unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            // pass.
            e.printStackTrace();
        }

        try
        {
            unregisterReceiver(mBroadcastReceiver);
        }
        catch (Exception e)
        {
            //pass through
            e.printStackTrace();
        }
    }

    private void checkMessage(Intent intent){
        if (null != intent)
        {
            resetIntentValues();
        }
    }

    /**
     * Will check main Activity intent and if it contains any PushWoosh data, will clear it
     */
    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    // Manage receivers registration in the onPause/onResume functions
    @Override
    public void onResume(){
        super.onResume();

        //Re-register receivers on resume
        registerReceivers();
    }

    @Override
    public void onPause(){
        super.onPause();

        //Unregister receivers on pause
        unregisterReceivers();
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        return true;
    }

    private void shareFacebook(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(Key.KEY_ARTICLE_URL);
        String subject = in.getStringExtra(Key.KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL);

//        urlToShare = urlToShare.replace("&appview=1","");
        urlToShare = urlToShare.substring(0, urlToShare.lastIndexOf('&'));

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

        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?t=" + subject + "%0A%0A&u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            Toast.makeText(getApplicationContext(), R.string.facebook_not_found, Toast.LENGTH_SHORT).show();
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareTwitter(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(Key.KEY_ARTICLE_URL);
        String subject = in.getStringExtra(Key.KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL);

//        urlToShare = urlToShare.replace("&appview=1","");
        urlToShare = urlToShare.substring(0, urlToShare.lastIndexOf('&'));

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

        if (!twitterAppFound) {
            String sharerUrl = "https://twitter.com/intent/tweet?text=" + subject + "%0A%0A&url=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            Toast.makeText(getApplicationContext(), R.string.twitter_not_found, Toast.LENGTH_SHORT).show();
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareGoogle(){

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(Key.KEY_ARTICLE_URL);
        String subject = in.getStringExtra(Key.KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL);

//        urlToShare = urlToShare.replace("&appview=1","");
        urlToShare = urlToShare.substring(0, urlToShare.lastIndexOf('&'));

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

        if (!googleAppFound) {
            String sharerUrl = "https://plus.google.com/share?url=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            Toast.makeText(getApplicationContext(), R.string.googlePlus_not_found, Toast.LENGTH_SHORT).show();
        }

        if (intent.resolveActivity(getPackageManager()) != null) {      // did not put in else because need to access intent
            startActivity(intent);                                      // since intent cannot be declared as final
        }
    }

    private void shareWhatsApp(){
        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String urlToShare = in.getStringExtra(Key.KEY_ARTICLE_URL);
        String subject = in.getStringExtra(Key.KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL);

//        urlToShare = urlToShare.replace("&appview=1","");
        urlToShare = urlToShare.substring(0, urlToShare.lastIndexOf('&'));

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
            Toast.makeText(getApplicationContext(), R.string.whatsapp_not_found, Toast.LENGTH_SHORT).show();
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareSMS(){
        Intent in = getIntent();

        String urlToShare = in.getStringExtra(Key.KEY_ARTICLE_URL);
        String subject = in.getStringExtra(Key.KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL);

//        urlToShare = urlToShare.replace("&appview=1","");
        urlToShare = urlToShare.substring(0, urlToShare.lastIndexOf('&'));

        Uri url = Uri.parse(thumburl);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
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
        String subject = in.getStringExtra(Key.KEY_ARTICLE_TITLE);
        String thumburl = in.getStringExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL);
        String urlToShare = in.getStringExtra(Key.KEY_ARTICLE_URL);

//        urlToShare = urlToShare.replace("&appview=1","");
        urlToShare = urlToShare.substring(0, urlToShare.lastIndexOf('&'));

        Uri url = Uri.parse(thumburl);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        intent.putExtra(Intent.EXTRA_STREAM, url);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void copyLink(){
        Intent in = getIntent();

        String urlToShare = in.getStringExtra(Key.KEY_ARTICLE_URL);

//        urlToShare = urlToShare.replace("&appview=1","");
        urlToShare = urlToShare.substring(0, urlToShare.lastIndexOf('&'));

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copy", urlToShare);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
    }

    public void showSharer(){
        final Dialog d = new Dialog(this);
        d.setTitle("Share Via :");
        d.setContentView(R.layout.activity_share);

        String s[] = { "Email", "Facebook", "Google +", "Message", "Twitter", "WhatsApp", "Copy Link"  };
        int image[] = { R.drawable.email_icon, R.drawable.facebook_logo, R.drawable.google_plus_icon, R.drawable.message_icon, R.drawable.twitter_logo, R.drawable.whatsapp_icon, R.drawable.copy };

        ArrayList<HashMap<String, String>> objArrayList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < s.length; i++) {
            HashMap<String, String> listData = new HashMap<String, String>();
            listData.put("text", s[i]);
            listData.put("image", Integer.toString(image[i]));

            objArrayList.add(listData);

        }

        String[] from = { "image", "text" };
        int[] to = { R.id.icon, R.id.label };

        SimpleAdapter listAdapter = new SimpleAdapter(this, objArrayList,
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

                case 6:
                    copyLink();
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

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.failed);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recreate();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            if (isCameFromMain == true) {
                super.onBackPressed();
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
            } else {

                Intent myIntent = new Intent(getApplicationContext(), Activity_Main.class);
                startActivityForResult(myIntent, 0);
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);

                finish();
            }
        }else {
            showAlertDialog(Activity_SingleArticle.this, "Network Error", "No Internet Connection");
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
