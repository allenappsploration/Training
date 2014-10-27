package com.example.appsplorationdev.publisherapp;

import android.app.ActionBar;
import android.app.Activity;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by appsplorationdev on 9/25/14.
 */
public class Activity_Search extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONArray JSONArray = null;
    adapter_Search SearchAdapter;

    JSONObject jsonObj, jObj;
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> searchList;

    String hidItm, title, image, newURL, encodeQuery;

    public String searchKey;
    private Boolean hasResult = true;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    ListView lv;

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

            try {
                Intent myIntent = getIntent();

                searchKey = myIntent.getStringExtra("query");
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            searchList = new ArrayList<HashMap<String, String>>();

            new searchFunction().execute();
        }else {
            showAlertDialog(Activity_Search.this, "Network Error", "No Internet Connection");
        }

    }

    private class searchFunction extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Activity_Search.this);
            pDialog.setMessage("Searching...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            hasResult = true;

            encodeQuery = null;

            try {
                encodeQuery = URLEncoder.encode(StringEscapeUtils.unescapeHtml4(searchKey), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            newURL = "http://hot.appsploration.com/hot/api.php?api=search&q="+ encodeQuery +"&site_id=8";

            jsonObj = jParser.getJSONFromUrl(newURL);

            Log.i("Response: ", "> " + String.valueOf(jsonObj));

            if (jsonObj != null) {
                try {

                    JSONArray = jsonObj.getJSONArray(Key.KEY_RESULTS);

                    if ((JSONArray != null) && (JSONArray.length() > 0)) {
                        // looping through all json
                        for (int i = 0; i < JSONArray.length(); i++) {

                            // Storing each json item in variable
                            jObj = JSONArray.getJSONObject(i);

                            // creating new HashMap
                            final HashMap<String, String> article_row = new HashMap<String, String>();

                            if ((jObj.getString(Key.KEY_ARTICLE_URL)!= null)&&(jObj.getString(Key.KEY_ARTICLE_TITLE)!= null)) {
                                // adding each child node to HashMap key => value
                                article_row.put(Key.KEY_ARTICLE_URL, jObj.getString(Key.KEY_ARTICLE_URL));
                                article_row.put(Key.KEY_ARTICLE_TITLE,
                                        StringEscapeUtils.unescapeHtml4(jObj.getString(Key.KEY_ARTICLE_TITLE)));
                                article_row.put(Key.KEY_ARTICLE_PUBDATE, convertDate(jObj.getString(Key.KEY_ARTICLE_PUBDATE)));
                                article_row.put(Key.KEY_SMALL_ARTICLE_THUMB_URL, jObj.getString(Key.KEY_SMALL_ARTICLE_THUMB_URL));
                                article_row.put(Key.KEY_ARTICLE_HAS_THUMB, jObj.getString(Key.KEY_ARTICLE_HAS_THUMB));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // adding HashList to ArrayList
                                        searchList.add(article_row);
                                    }
                                });
                            }
                        }
                    } else {
                        return hasResult = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return true;
        }

        protected void onPostExecute(final Boolean result) {
            // dismiss the dialog after getting the related idioms
            pDialog.dismiss();

            try {
                lv = (ListView) findViewById(R.id.lv_search);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if (result) {
                        SearchAdapter = new adapter_Search(Activity_Search.this, searchList);
                        SearchAdapter.notifyDataSetChanged();

                        // updating listview
                        lv.setAdapter(SearchAdapter);
                    } else {

                        lv.setBackgroundResource(R.drawable.search_not_found);
                        lv.getLayoutParams().height = 1200;
                    }
                }
            });

            // ListView on item click listener
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    try {
                        // getting values from selected ListItem
                        title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                        image = ((TextView) view.findViewById(R.id.hidthumburl)).getText().toString();
                        hidItm = ((TextView) view.findViewById(R.id.hiditem)).getText().toString();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Starting single contact activity
                    Intent in = new Intent(getApplicationContext(),
                            Activity_SingleArticle.class);
                    in.putExtra(Key.KEY_ARTICLE_TITLE, title);
                    in.putExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL, image);
                    in.putExtra(Key.KEY_ARTICLE_URL, hidItm);
                    in.putExtra("fromMainActivity", true);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.enter_right_to_left);
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
