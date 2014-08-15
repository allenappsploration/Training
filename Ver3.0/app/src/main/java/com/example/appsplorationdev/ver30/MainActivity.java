package com.example.appsplorationdev.ver30;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends KeyConfiguration {

    ListView list;
    LazyAdapter adapter;
    JSONArray contacts = null;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> songsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsList = new ArrayList<HashMap<String, String>>();

        ListView lv = (ListView) findViewById(R.id.list);

        //list = (ListView)findViewById(R.id.list);

        // ListView on item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String hidItm;
                hidItm = ((TextView) view.findViewById(R.id.hiditem)).getText().toString();
                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        SingleContactActivity.class);
                in.putExtra(KEY_LINK, hidItm);
                startActivity(in);
            }
        });

        new GetContacts().execute();
    }


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

        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(URL, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if(jsonStr != null) {

                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject channelObj = jsonObj.getJSONObject(KEY_CHANNEL);
                    contacts = channelObj.getJSONArray(KEY_ITEM);

                    for (int i = 0; i < contacts.length(); i++) {
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        //Element e = (Element) nl.item(i);
                        JSONObject c = contacts.getJSONObject(i);
                        // adding each child node to HashMap key => value

                        String bDate = c.getString(KEY_PUBDATE);
                        String pubDate = bDate.replace("+0000", "");

                        String p10 = c.getString(KEY_THUMBNAIL);

                        String p9 = p10.substring(p10.indexOf(",")+1);
                        String p8 = p9.substring(p9.indexOf('h'));
                        String p7 = p8.replace('"',' ');
                        String pFinal = p7.replace("}]", "");
                        String image = pFinal.replace("\\/", "/");

                        map.put(KEY_LINK, c.getString(KEY_LINK));
                        map.put(KEY_TITLE, c.getString(KEY_TITLE));
                        map.put(KEY_CREATOR, c.getString(KEY_CREATOR));
                        map.put(KEY_PUBDATE, pubDate);
                        map.put(KEY_THUMBNAIL, image);

                        // adding HashList to ArrayList
                        songsList.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            // Getting adapter by passing xml data ArrayList
            adapter = new LazyAdapter(MainActivity.this, songsList);

            ListView lv = (ListView) findViewById(R.id.list);
            lv.setAdapter(adapter);
        }
    }

    public void onBackPressed(){
        new AlertDialog.Builder(this)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Publisher App")
                .setMessage("Are you sure you want to close Publisher App?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
