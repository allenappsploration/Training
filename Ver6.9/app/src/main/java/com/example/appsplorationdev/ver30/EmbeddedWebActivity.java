package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by appsplorationdev on 10/7/14.
 */
public class EmbeddedWebActivity extends KeyConfigure {

    private WebView webView;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            setContentView(R.layout.embed_webview);

            final ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            Intent in = getIntent();

            final String url = in.getStringExtra("embedURL");

            Log.i("EMBED URL : ", String.valueOf(url));

            webView = (WebView)findViewById(R.id.wvEmbeddedBrowser);

            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.getSettings().getLoadsImagesAutomatically();
            webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_INSET);
            webView.setScrollbarFadingEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new MyNewEmbedWebClient());

            if (savedInstanceState == null){
                webView.loadUrl(url);
            }

        }else {
            showAlertDialog(EmbeddedWebActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }
    }

    private class MyNewEmbedWebClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){


            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);

            return true;
        }
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
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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
