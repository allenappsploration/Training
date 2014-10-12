package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by appsplorationdev on 9/25/14.
 */
public class SearchActivity extends KeyConfigure {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONArray JSONArray = null;
    LazyAdapter lazyAdapter;

    ArrayList<HashMap<String, String>> searchList;

    public String searchKey;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {
            setContentView(R.layout.activity_search);

            ActionBar actionBar = getActionBar();

            // Enabling back navigation on Action Bar icon
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            Intent myIntent = getIntent();

            searchKey = myIntent.getStringExtra("query");

            searchList = new ArrayList<HashMap<String, String>>();

            new searchFunction().execute();
        }else {
            showAlertDialog(SearchActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }

    }

    private class searchFunction extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Searching...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            JSONParser jParser = new JSONParser();

            String newURL = "http://hot.appsploration.com/hot/api.php?api=search&q="+ searchKey +"&site_id=8";

            JSONObject jsonObj = jParser.getJSONFromUrl(newURL);

            Log.i("Response: ", "> " + String.valueOf(jsonObj));

            if (jsonObj != null) {
                try {

                    JSONArray = jsonObj.getJSONArray(KEY_RESULTS);

                    // looping through All Products
                    for (int i = 0; i < JSONArray.length(); i++) {

                        JSONObject c = JSONArray.getJSONObject(i);

                        // Storing each json item in variable

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value

                        map.put(KEY_ARTICLE_URL, c.getString(KEY_ARTICLE_URL));
                        map.put(KEY_ARTICLE_TITLE, c.getString(KEY_ARTICLE_TITLE));
                        map.put(KEY_ARTICLE_PUBDATE, convertDate(c.getString(KEY_ARTICLE_PUBDATE)));
                        map.put(KEY_SMALL_ARTICLE_THUMB_URL, c.getString(KEY_SMALL_ARTICLE_THUMB_URL));

                        // adding HashList to ArrayList
                        searchList.add(map);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting the related idioms
            pDialog.dismiss();

            final ListView lv = (ListView)findViewById(R.id.lv_search);
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    lazyAdapter = new LazyAdapter(SearchActivity.this, searchList, 30, 30);
                    lazyAdapter.notifyDataSetChanged();

                    // updating listview
                    lv.setAdapter(lazyAdapter);
                }
            });

            // ListView on item click listener
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // getting values from selected ListItem
                    String hidItm, title, image;
                    title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                    image = ((TextView) view.findViewById(R.id.hidthumburl)).getText().toString();
                    hidItm = ((TextView) view.findViewById(R.id.hiditem)).getText().toString();
                    // Starting single contact activity
                    Intent in = new Intent(getApplicationContext(),
                            SingleActivity.class);
                    in.putExtra(KEY_ARTICLE_TITLE, title);
                    in.putExtra(KEY_SMALL_ARTICLE_THUMB_URL, image);
                    in.putExtra(KEY_ARTICLE_URL, hidItm);
                    startActivity(in);

                    overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
                }
            });
        }
    }

    private String convertDate(String mDate){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date d = dateFormat.parse(mDate);
            SimpleDateFormat serverFormat = new SimpleDateFormat("E d MMM yyyy h:mm a");
            return serverFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
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
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
//                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivityForResult(myIntent, 0);

                onBackPressed();
                return true;

//            case R.id.menu_item_share:
//                showSharer();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
