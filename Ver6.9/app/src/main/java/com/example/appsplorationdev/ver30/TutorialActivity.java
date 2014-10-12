package com.example.appsplorationdev.ver30;

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

public class TutorialActivity extends FragmentActivity implements
        OnClickListener, OnPageChangeListener {

	private Button btnSkip;
	private int position = 0, totalImage;
	private ViewPager viewPage;
	private ArrayList<Integer> itemData;
	private TutorialPagerAdapter adapter;
	private TutorialImages imageId;

    // Splash screen timer
    private static int TIME_OUT = 0;

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

            adapter = new TutorialPagerAdapter(getSupportFragmentManager(),
                    itemData);
            viewPage.setAdapter(adapter);
            viewPage.setOnPageChangeListener(TutorialActivity.this);

            btnSkip.setOnClickListener(this);
        }else {
            showAlertDialog(TutorialActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }
	}

	@Override
	public void onClick(View v) {
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
                    Intent i = new Intent(TutorialActivity.this, MainActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();

                    overridePendingTransition(0, 0);
                }
            }, TIME_OUT);
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
//		if (page == 0 && totalImage > 0) {
//			btnImageNext.setVisibility(View.VISIBLE);
//			btnImagePrevious.setVisibility(View.INVISIBLE);
//		} else if (page == totalImage - 1 && totalImage > 0) {
//			btnImageNext.setVisibility(View.INVISIBLE);
//			btnImagePrevious.setVisibility(View.VISIBLE);
//		} else {
//			btnImageNext.setVisibility(View.VISIBLE);
//			btnImagePrevious.setVisibility(View.VISIBLE);
//		}
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
}
