package com.example.appsplorationdev.publisherapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.ArrayList;

public class Activity_Tutorial extends FragmentActivity implements
        OnClickListener, OnPageChangeListener {

	private Button btnSkip;
	private int position = 0;
    private int totalImage;
	private ViewPager viewPage;
	private ArrayList<Integer> itemData;
	private adapter_TutorialPager TutorialAdapter;
	private TutorialImages imageId;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {

            setContentView(R.layout.tutorial_imagepage);

            viewPage = (ViewPager) findViewById(R.id.viewPager);
            btnSkip = (Button) findViewById(R.id.btnSkip);
            imageId = new TutorialImages();
            itemData = imageId.getImageItem();
            totalImage = itemData.size();
            setPage(position);

            TutorialAdapter = new adapter_TutorialPager(getSupportFragmentManager(),
                    itemData);
            viewPage.setAdapter(TutorialAdapter);
            viewPage.setOnPageChangeListener(Activity_Tutorial.this);

            btnSkip.setOnClickListener(this);
        }else {
            showAlertDialog(Activity_Tutorial.this, "Network Error", "No Internet Connection");
        }
	}

	@Override
	public void onClick(View v) {
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {
            if (v == btnSkip) {
                new Handler().postDelayed(new Runnable() {
             /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
                    @Override
                    public void run() {

                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(Activity_Tutorial.this, Activity_Main.class);
                        startActivity(i);

                        // close this activity
                        finish();

                        overridePendingTransition(0, 0);
                    }
                }, 0);
            }
        }else {
            showAlertDialog(Activity_Tutorial.this, "Network Error", "No Internet Connection");
        }
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		this.position = position;
		setPage(position);
	}

	private void setPage(int page) {
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
    }
}
