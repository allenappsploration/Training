package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
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
import android.widget.ShareActionProvider;
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
public class SingleActivity extends KeyConfigure {

    private WebView wvP1, embedWeb;
    private ShareActionProvider mShareActionProvider;
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
            final String articleURL = in.getStringExtra(KEY_ARTICLE_URL);
            final String notificationURL = in.getStringExtra("title");             // Get URL from notification with TAG

            // Displaying all values on the screen
            wvP1 = (WebView) findViewById(R.id.webView);

            wvP1.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            wvP1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            wvP1.getSettings().getLoadsImagesAutomatically();
            wvP1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_INSET);
            wvP1.setScrollbarFadingEnabled(true);
            wvP1.getSettings().setLoadsImagesAutomatically(true);

//        wvP1.setWebViewClient(new WebViewClient());
            wvP1.getSettings().setJavaScriptEnabled(true);

            wvP1.setWebViewClient(new MyWebViewClient());

            if (savedInstanceState == null) {
//                wvP1.reload();
                if (isCameFromMain == true ) {
                    wvP1.loadUrl(articleURL);
                }else {
                    wvP1.loadUrl(notificationURL);
                }
            }

            final ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            ImageButton ib_share = (ImageButton) findViewById(R.id.iv_share);
            ImageButton ib_zoomIn = (ImageButton) findViewById(R.id.iv_zoomIn);
            ImageButton ib_zoomOut = (ImageButton) findViewById(R.id.iv_zoomOut);

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
//                        Toast.makeText(getApplicationContext(), " " + zoomSize, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Returned to default zoom size", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(getApplicationContext(), " " + zoomSize, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Reached maximum zoom size", Toast.LENGTH_SHORT).show();
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
            }

            //Register for push!
            pushManager.registerForPushNotifications();

//            checkMessage(getIntent());

            /*----------- End of Push Woosh initialization -------------*/
        }else {
            showAlertDialog(SingleActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            intent.setPackage("com.android.browser");
//            startActivity(intent);
//            LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//            View layout = layoutInflater.inflate(R.layout.web_view, null);
//            final PopupWindow popupWindow = new PopupWindow(
//                    layout,
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    true);
//
//            embedWeb = (WebView)layout.findViewById(R.id.webview);
//            embedWeb.getSettings().setJavaScriptEnabled(true);
//            embedWeb.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//            embedWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//            embedWeb.getSettings().getLoadsImagesAutomatically();
//            embedWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically (false);
//            embedWeb.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_INSET);
//            embedWeb.setScrollbarFadingEnabled(true);
//            embedWeb.getSettings().setLoadsImagesAutomatically(true);
//
//            embedWeb.loadUrl(url);
//            popupWindow.showAtLocation(embedWeb, Gravity.CENTER, 0, 0);
//            popupWindow.setOutsideTouchable(true);
//            Button close = (Button)layout.findViewById(R.id.btnClose);
//            close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    popupWindow.dismiss();
//                }
//            });

            Intent in = new Intent(getApplicationContext(), EmbeddedWebActivity.class);
            in.putExtra("embedURL", url);
            startActivity(in);

            return true;
        }
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

    BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver()
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
//            checkMessage(intent);
//            Intent noficationIntent = new Intent(getApplicationContext(),SingleActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, noficationIntent, 0);
//            startActivity(noficationIntent);
        }
    };

    private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent)
        {
            //JSON_DATA_KEY contains JSON payload of push notification.

        }
    };

    //Registration of the receivers
    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers()
    {
        //Unregister receivers on pause
        try
        {
            unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            // pass.
        }

        try
        {
            unregisterReceiver(mBroadcastReceiver);
        }
        catch (Exception e)
        {
            //pass through
        }
    }

    // Manage receivers registration in the onPause/onResume functions
    @Override
    public void onResume()
    {
        super.onResume();

        //Re-register receivers on resume
        registerReceivers();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //Unregister receivers on pause
        unregisterReceivers();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate menu resource file.
//        getMenuInflater().inflate(R.menu.main, menu);

        // Locate MEnuItem with ShareActionProvider
//        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
//        mShareActionProvider = (ShareActionProvider) item.getActionProvider();


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
        String thumburl = in.getStringExtra(KEY_SMALL_ARTICLE_THUMB_URL);

        urlToShare = urlToShare.replace("&appview=1","/");

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
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?t=" + subject + "%0A%0A&u=" + urlToShare;
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
        String thumburl = in.getStringExtra(KEY_SMALL_ARTICLE_THUMB_URL);

        urlToShare = urlToShare.replace("&appview=1","/");

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
            String sharerUrl = "https://twitter.com/intent/tweet?text="+ subject + "%0A%0A&url="+ urlToShare;
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
        String thumburl = in.getStringExtra(KEY_SMALL_ARTICLE_THUMB_URL);

        urlToShare = urlToShare.replace("&appview=1","/");

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
        String thumburl = in.getStringExtra(KEY_SMALL_ARTICLE_THUMB_URL);

        urlToShare = urlToShare.replace("&appview=1","/");

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
        String thumburl = in.getStringExtra(KEY_SMALL_ARTICLE_THUMB_URL);

        urlToShare = urlToShare.replace("&appview=1","/");

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
        String thumburl = in.getStringExtra(KEY_SMALL_ARTICLE_THUMB_URL);
        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);

        urlToShare = urlToShare.replace("&appview=1","/");

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

    private void copyLink(){
        Intent in = getIntent();

        String urlToShare = in.getStringExtra(KEY_ARTICLE_URL);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copy", urlToShare);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplicationContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    public void showSharer(){
        final Dialog d = new Dialog(this);
        d.setTitle("Share Via :");
        d.setContentView(R.layout.activity_share);

        String s[] = { "Email", "Facebook", "Google +", "Message", "Twitter", "WhatsApp", "Copy Link"  };
        int image[] = { R.drawable.email_icon, R.drawable.facebook_logo, R.drawable.google_plus_icon, R.drawable.message_icon, R.drawable.twitter_logo, R.drawable.whatsapp_icon, R.drawable.copy };

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

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.failed);
        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                recreate();
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);  // Method to close the application
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (isCameFromMain == true){
            super.onBackPressed();
        }
        else {

            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
        }
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);

        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
//                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivityForResult(myIntent, 0);
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
