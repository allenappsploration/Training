package com.example.appsplorationdev.publisherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by appsplorationdev on 10/13/14.
 */
public class Activity_FirstRunCheck extends Activity {

    Boolean isInternetPresent = false;
    ConnectionDetector cd = new ConnectionDetector(Activity_FirstRunCheck.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {

            SharedPreferences prefs = getSharedPreferences(Key.PACKAGE_NAME, MODE_PRIVATE);
            boolean firstRun = prefs.getBoolean("firstRun", false);

            if (firstRun == false) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstRun", true);
                editor.commit();
                Intent i = new Intent(Activity_FirstRunCheck.this, Activity_Tutorial.class);
                startActivity(i);
                finish();
            } else {
                Intent a = new Intent(Activity_FirstRunCheck.this, Activity_Main.class);
                startActivity(a);
                finish();
            }
        }else {
            showAlertDialog(Activity_FirstRunCheck.this, "Network Error", "No Internet Connection");
        }
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
}
