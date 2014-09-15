package com.example.appsplorationdev.imademo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;


public class MainActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ImageButton ibtn_zespri = (ImageButton)this.findViewById(R.id.img_zespri);
        ImageButton ibtn_left_umobile = (ImageButton)this.findViewById(R.id.img_left_suzuki);
        ImageButton ibtn_right_ikea = (ImageButton)this.findViewById(R.id.img_right_suzuki);
        ImageButton ibtn_inter_airasia = (ImageButton)this.findViewById(R.id.img_interstitial_airasia);
        ImageButton ibtn_tap_samsung = (ImageButton)this.findViewById(R.id.img_tap_clinique);
        ImageButton ibtn_native_ad = (ImageButton)this.findViewById(R.id.img_nativeAd);

        ibtn_zespri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PullUp_zespri.class);
                startActivity(intent);

                overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
            }
        });

        ibtn_left_umobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SwipeLeft_suzuki.class);
                startActivity(intent);

                overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
            }
        });

        ibtn_right_ikea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SwipeRight_suzuki.class);
                startActivity(intent);

                overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
            }
        });

        ibtn_inter_airasia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Interstitial_airasia.class);
                startActivity(intent);

                overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
            }
        });

        ibtn_tap_samsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Tap_clinique.class);
                startActivity(intent);

                overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
            }
        });

        ibtn_native_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MultipleItemsList.class);
                startActivity(intent);

                overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
            }
        });

    }

    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.app_icon)
                .setTitle("Confirm Exit")
                .setMessage("Exit IMADemo?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                getIntent().removeExtra("key");

                                finish();
                                System.exit(0);
                                System.exit(1);
                            }
                        })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
