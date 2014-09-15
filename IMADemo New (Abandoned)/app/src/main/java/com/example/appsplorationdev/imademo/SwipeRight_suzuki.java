package com.example.appsplorationdev.imademo;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.innity.imad.IMAd;
import com.innity.imad.IMAdTargetProperties;

/**
 * Created by appsplorationdev on 9/9/14.
 */
public class SwipeRight_suzuki extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.standard_bg);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Starts of ads
        IMAd banner = new IMAd(this, true);
        banner.TargetProperties.setProperty(IMAdTargetProperties.IMATargetPropKey_Age, "30")
                .setProperty(IMAdTargetProperties.IMATargetPropKey_Language, "en")
                .setProperty(IMAdTargetProperties.IMATargetPropKey_Gender, "f")
                .setProperty(IMAdTargetProperties.IMATargetPropKey_Birthday, "1988-12-31")
                .setProperty(IMAdTargetProperties.IMATargetPropKey_Latitude, "37.0625")
                .setProperty(IMAdTargetProperties.IMATargetPropKey_Longitude, "-95.677068")
                .setProperty("CustomProperty", "CustomValue");

        banner.loadAdWithZoneID("3");
        banner.setAdLoadListener(new IMAd.IMAdLoadListener() {
            @Override
            public void adDidLoad(IMAd banner) {
                banner.show();
            }

            @Override
            public void adDidFailLoad(IMAd banner) {

            }
        });

        ViewGroup container = (ViewGroup) (getWindow().getDecorView());
        container.addView(banner.getView());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
