package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

/**
 * Created by appsplorationdev on 10/7/14.
 */
public class EmbeddedWebActivity extends KeyConfigure {

    private WebView webView;

    private ImageButton ib_back;
    private ImageButton ib_forward;

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
            ib_back = (ImageButton)findViewById(R.id.ib_back);
            ib_forward= (ImageButton)findViewById(R.id.ib_forward);

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

            ib_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                cd = new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    }
                }else {
                    showAlertDialog(EmbeddedWebActivity.this, "Network Error", "No Internet Connection", false);
                }
                }
            });

            ib_forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                cd = new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if (webView.canGoForward()) {
                        webView.goForward();
                    }
                }else {
                    showAlertDialog(EmbeddedWebActivity.this, "Network Error", "No Internet Connection", false);
                }
                }
            });

        }else {
            showAlertDialog(EmbeddedWebActivity.this, "Network Error", "No Internet Connection", false);
        }
    }

    private class MyNewEmbedWebClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            if (isInternetPresent) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);

            }else {
                showAlertDialog(EmbeddedWebActivity.this, "Network Error", "No Internet Connection", false);
            }
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

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            super.onBackPressed();

            overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);

            finish();
        }else {
            showAlertDialog(EmbeddedWebActivity.this, "Network Error", "No Internet Connection", false);
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
