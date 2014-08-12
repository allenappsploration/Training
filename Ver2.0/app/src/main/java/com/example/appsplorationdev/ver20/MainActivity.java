package com.example.appsplorationdev.ver20;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    // JSON Node names
    private static final String TAG_MainHeader = "channel";
    private static final String TAG_SubHeader = "item";
    private static final String TAG_P1 = "title";
    private static final String TAG_P2 = "link";
    private static final String TAG_P3 = "comments";
    private static final String TAG_P4 = "pubDate";
    private static final String TAG_P5 = "creator";
    private static final String TAG_P6 = "category";
    private static final String TAG_P7 = "guid";
    private static final String TAG_P8 = "description";
    private static final String TAG_P9 = "commentRss";
    private static final String TAG_P10 = "thumbnail";
    private static final String TAG_P11 = "content";
    private static final String TAG_P12 = "twitter";

    // URL to get contacts JSON
    private static String url = "http://i.appsploration.com/so/json.php";
    // contacts JSONArray
    JSONArray contacts = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<HashMap<String, String>>();

        ListView lv = (ListView)findViewById(R.id.list);

        // ListView on item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String p1 = ((TextView) view.findViewById(R.id.itm1))
                        .getText().toString();
                String p2 = ((TextView) view.findViewById(R.id.itm2))
                        .getText().toString();
                String p3 = ((TextView) view.findViewById(R.id.itm3))
                        .getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        SingleContactActivity.class);
                in.putExtra(TAG_P1, p1);
                in.putExtra(TAG_P2, p2);
                in.putExtra(TAG_P3, p3);
                startActivity(in);

            }
        });

        // Calling async task to get json
        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject channelObj= jsonObj.getJSONObject(TAG_MainHeader);
                    contacts = channelObj.getJSONArray(TAG_SubHeader);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String p1 = c.getString(TAG_P1);
                        String p2 = c.getString(TAG_P2);
                        String p3 = c.getString(TAG_P3);
                        String p4 = c.getString(TAG_P4);
                        String p5 = c.getString(TAG_P5);
                        String p6 = c.getString(TAG_P6);
                        String p7 = c.getString(TAG_P7);
                        String p8 = c.getString(TAG_P8);
                        String p9 = c.getString(TAG_P9);
                        String p10 = c.getString(TAG_P10);
                        String p11 = c.getString(TAG_P11);
                        String p12 = c.getString(TAG_P12);

                        // Phone node is JSON Object
            /*            JSONObject phone = c.getJSONObject(TAG_PHONE);
                        String mobile = phone.getString(TAG_PHONE_MOBILE);
                        String home = phone.getString(TAG_PHONE_HOME);
                        String office = phone.getString(TAG_PHONE_OFFICE);
*/
                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_P1, p1);
                        contact.put(TAG_P2, p2);
                        contact.put(TAG_P3, p3);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{TAG_P1, TAG_P2,
                    TAG_P3}, new int[]{R.id.itm1,
                    R.id.itm2, R.id.itm3}
            );
            ListView lv = (ListView)findViewById(R.id.list);
            lv.setAdapter(adapter);
        }

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
