package com.example.appsplorationdev.ver30;

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
public class FirstRunCheck extends Activity {

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {

            SharedPreferences prefs = getSharedPreferences("com.example.appsplorationdev.ver30", MODE_PRIVATE);
            boolean firstRun = prefs.getBoolean("firstRun", false);

            if (firstRun == false) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstRun", true);
                editor.commit();
                Intent i = new Intent(FirstRunCheck.this, TutorialActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent a = new Intent(FirstRunCheck.this, MainActivity.class);
                startActivity(a);
                finish();
            }
        }else {
            showAlertDialog(FirstRunCheck.this, "Network Error", "No Internet Connection", false);
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
}
