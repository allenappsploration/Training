package com.example.appsplorationdev.publisherapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;
import com.commonsware.cwac.merge.MergeAdapter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Activity_Main extends Activity {

    ArrayList<HashMap<String, String>> NewsList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> mDrawerItems = new ArrayList<HashMap<String, String>>();
    JSONArray JSONArray = null;
    adapter_Lazy LazyAdapter;
    adapter_NaviDrawer NaviDrawerAdapter;
    MergeAdapter mergeAdapter = new MergeAdapter();
    Boolean isInternetPresent = false;
    ConnectionDetector cd = new ConnectionDetector(Activity_Main.this);
    JSONObject jsonObj, jObj;
    JSONParser jParser = new JSONParser();

    private ProgressDialog pDialog;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    ProgressBar progressBar;
    private boolean isLoading = false, isEnd = false, firstRun = false;
    private int previousItemCount = 0, currentPosition = 0, current_page = 0;
    private ListView lv;
    private View footer;

    private String hid_ID;

    @Override
    public void onStart() {
        super.onStart();

        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {
            // <editor-fold desc="Set up main list and footer views">

            footer = getLayoutInflater().inflate(R.layout.footer_progressbar, null);
            footer.setOnClickListener(null);        // nothing happen when click on footer
            progressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
            progressBar.setIndeterminate(true);

            lv = (ListView) findViewById(R.id.list);
            LazyAdapter = new adapter_Lazy(Activity_Main.this, NewsList, 30, 30);
            lv.addFooterView(footer);
            lv.setAdapter(LazyAdapter);
            lv.removeFooterView(footer);

            new loadMoreListView().execute();
            lv.removeFooterView(footer);

            // </editor-fold>
        }else {
            showAlertDialog(Activity_Main.this, "Network Error", "No Internet Connection");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {

            setContentView(R.layout.activity_main);

            //get the action bar
            ActionBar actionBar = getActionBar();

            // Enabling back navigation on Action Bar icon
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            try {
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerList = (ListView) findViewById(R.id.left_drawer);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            // ActionBarDrawerToggle ties together the the proper interactions
            // between the sliding drawer and the action bar app icon
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);

            if (savedInstanceState == null) {
                selectItem(0);
            }

            mDrawerToggle.syncState();              // Sync the state of navigation drawer

            new getNaviDrawerItem().execute();

            handleSearchIntent(getIntent());

            // <editor-fold desc="Push Notification initialization">

            registerReceivers();                // Register receivers for push notifications

            //Create and start push manager
            PushManager pushManager = PushManager.getInstance(this);

            //Start push manager, this will count app open for Pushwoosh stats as well
            try {
                pushManager.onStartup(this);
            }
            catch(Exception e)
            {
                //push notifications are not available or AndroidManifest.xml is not configured properly
                e.printStackTrace();
            }

            //Register for push!
            pushManager.registerForPushNotifications();

            // </editor-fold Push Notification initialization>

        }else {
            showAlertDialog(Activity_Main.this, "Network Error", "No Internet Connection");
        }

    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item
        mDrawerList.setItemChecked(position, true);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the main view
     */
    public static class NewsFragment extends Fragment {

        public NewsFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_listview, container, false);
            return rootView;
        }
    }

    private class getNaviDrawerItem extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {

            jsonObj = jParser.getJSONFromUrl(Key.CATEGORY_URL);
            // Making a request to url and getting response

            Log.d("Response: ", "> " + String.valueOf(jsonObj));

            if (jsonObj != null) {

                try {
                    // Getting JSON Array node
                    JSONArray = jsonObj.getJSONArray(Key.KEY_RESULTS);

                    for (int i = 0; i < JSONArray.length(); i++) {

                        jObj = JSONArray.getJSONObject(i);

                        // creating new HashMap
                        HashMap<String, String> naviItems = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        naviItems.put(Key.KEY_ID, jObj.getString(Key.KEY_ID));
                        naviItems.put(Key.KEY_NAME, jObj.getString(Key.KEY_NAME));

                        // adding HashList to ArrayList
                        mDrawerItems.add(naviItems);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View banner = inflater.inflate(R.layout.drawer_banner, null);
            final View home_row = inflater.inflate(R.layout.drawer_home, null);
            // <editor-fold desc="home_row OnClickListener">
            home_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                    i.putExtra(Key.KEY_ID, "17");
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            });
            // </editor-fold header 2>
            View cat_header = inflater.inflate(R.layout.drawer_header, null);
            View tutorial_header = inflater.inflate(R.layout.drawer_footer, null);
            View tutorial_row = inflater.inflate(R.layout.drawer_tutorial, null);
            // <editor-fold desc="tutorial_row OnClickListener">
            tutorial_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), Activity_Tutorial.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            });
            // </editor-fold>

            NaviDrawerAdapter = new adapter_NaviDrawer(Activity_Main.this, mDrawerItems);
            mergeAdapter.addView(banner);
            mergeAdapter.addView(home_row);
            mergeAdapter.addView(cat_header);
            mergeAdapter.addAdapter(NaviDrawerAdapter);
            mergeAdapter.addView(tutorial_header);
            mergeAdapter.addView(tutorial_row);
            mDrawerList.setAdapter(mergeAdapter);
            NaviDrawerAdapter.notifyDataSetChanged();

            // <editor-fold desc="NavigationDrawerList OnClickListener">
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    isInternetPresent = cd.isConnectingToInternet();        // true or false

                    if (isInternetPresent) {
                        hid_ID = ((TextView) view.findViewById(R.id.hidID)).getText().toString();

                        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                        i.putExtra(Key.KEY_ID, hid_ID);
                        startActivity(i);

                        overridePendingTransition(0, 0);
                    }else {
                        showAlertDialog(Activity_Main.this, "Network Error", "No Internet Connection");
                    }
                }
            });
            // </editor-fold>
        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog
            if (!firstRun) {
                firstRun = true;
                try {
                    pDialog = new ProgressDialog(Activity_Main.this);
                    pDialog.setMessage("Loading...");
                    pDialog.setCancelable(false);
                    pDialog.setIndeterminate(true);
                    pDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(30000);            // 30 seconds
                            pDialog.dismiss();
                            pDialog.cancel();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else {

                if (isEnd) {
                    lv.removeFooterView(footer);
                } else {
                    if (lv.getFooterViewsCount() == 0) {
                        lv.addFooterView(footer);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(5000);            // 5 seconds
                                    lv.removeFooterView(footer);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }

        protected Boolean doInBackground(Void... unused) {
            // increment current page
            current_page += 1;

            Intent in = getIntent();
            String CATEGORY_ID = in.getStringExtra(Key.KEY_ID);

            if ((CATEGORY_ID == null) || (CATEGORY_ID.trim().length() < 1)) {
                CATEGORY_ID = "17";
            }

            // Next page request
            String url = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=" + CATEGORY_ID + "&page_no=" + current_page + "&appview=1";

//            jParser = new JSONParser();

            // Making a request to url and getting response
            jsonObj = jParser.getJSONFromUrl(url);

            Log.d("Response: ", "> " + String.valueOf(jsonObj));

            if (jsonObj != null) {

                try {
                    // Getting JSON Array node
                    JSONArray = jsonObj.optJSONArray(Key.KEY_RESULTS);

                    if ((JSONArray != null) && (JSONArray.length() > 0)) {
                        for (int i = 0; i < JSONArray.length(); i++) {
                            // creating new HashMap
                            final HashMap<String, String> article_row = new HashMap<String, String>();

                            jObj = JSONArray.getJSONObject(i);

                            if ((jObj.getString(Key.KEY_ARTICLE_URL)!= null)&&(jObj.getString(Key.KEY_ARTICLE_TITLE)!= null)) {
                                // adding each child node to HashMap key => value
                                article_row.put(Key.KEY_ARTICLE_URL, jObj.getString(Key.KEY_ARTICLE_URL));
                                article_row.put(Key.KEY_ARTICLE_TITLE,
                                        StringEscapeUtils.unescapeHtml4(jObj.getString(Key.KEY_ARTICLE_TITLE)));
                                article_row.put(Key.KEY_CATEGORY_NAME, jObj.getString(Key.KEY_CATEGORY_NAME));
                                article_row.put(Key.KEY_ARTICLE_PUBDATE, convertDate(jObj.getString(Key.KEY_ARTICLE_PUBDATE)));
                                article_row.put(Key.KEY_SMALL_ARTICLE_THUMB_URL, jObj.getString(Key.KEY_SMALL_ARTICLE_THUMB_URL));
                                article_row.put(Key.KEY_ARTICLE_HAS_THUMB, jObj.getString(Key.KEY_ARTICLE_HAS_THUMB));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // adding HashList to ArrayList
                                        NewsList.add(article_row);
                                    }
                                });
                            }
                        }
                    } else {
                        return isEnd = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return (null);
        }

        protected void onPostExecute(Boolean unused) {

            isInternetPresent = cd.isConnectingToInternet();        // true or false

            if (isInternetPresent) {
                // Dismiss the progress dialog
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                        pDialog.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                currentPosition = lv.getFirstVisiblePosition();
                lv.setSelectionFromTop(currentPosition, 0);
                Activity_Main.this.LazyAdapter.notifyDataSetChanged();
                Log.i("lazyAdapter", "notifyDataSetChanged()");

                lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {}

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        isLoading = false;
                        if (previousItemCount < totalItemCount) {
                            if (firstVisibleItem >= (totalItemCount - Key.CONSTANT_TO_LOAD_MORE) && !isLoading) {
                                new loadMoreListView().execute();
                                isLoading = true;
                                previousItemCount = totalItemCount;
                            }
                        }
                    }
                });

                if (isEnd) {
                    lv.removeFooterView(footer);
                }

                // ListView on item click listener
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
                            // getting values from selected ListItem
                            String hidItm, title, image;
                            title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                            image = ((TextView) view.findViewById(R.id.hidthumburl)).getText().toString();
                            hidItm = ((TextView) view.findViewById(R.id.hiditem)).getText().toString();
                            // Starting single contact activity
                            Intent in = new Intent(getApplicationContext(),
                                    Activity_SingleArticle.class);
                            in.putExtra(Key.KEY_ARTICLE_TITLE, title);
                            in.putExtra(Key.KEY_SMALL_ARTICLE_THUMB_URL, image);
                            in.putExtra(Key.KEY_ARTICLE_URL, hidItm);
                            in.putExtra("fromMainActivity", true);
                            startActivity(in);

                            overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
                        } else {
                            showAlertDialog(Activity_Main.this, "Network Error", "No Internet Connection");
                        }
                    }
                });
            }else {
                showAlertDialog(Activity_Main.this, "Network Error", "No Internet Connection");
            }
        }
    }

    private String convertDate(String mDate) {
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
//                recreate();
                finish();
                startActivity(getIntent());
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

    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.innity_logo)
                .setTitle("Exit Publisher App")
                .setMessage("Are you sure you want to close Publisher App?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);  // Method to close the application
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

    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.search, menu);

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // set search intent passing value and check push notification intent
    @Override
    protected void onNewIntent(Intent intent) {

        setIntent(intent);

        checkMessage(intent);
    }

    private void handleSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            try {
                String query = intent.getStringExtra(SearchManager.QUERY);

                Intent in = new Intent(getApplicationContext(),
                        Activity_Search.class);
                in.putExtra("query", query);
                startActivity(in);

                overridePendingTransition(0, 0);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //   <editor-fold desc="Push Woosh implementation">
    BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver()  //Receiver registration
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
            checkMessage(intent);
        }
    };

    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent)
        {}
    };

    //Registration of the receivers
    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers() {
        //Unregister receivers on pause
        try
        {
            unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            // pass.
        }

        try
        {
            unregisterReceiver(mBroadcastReceiver);
        }
        catch (Exception e)
        {
            //pass through
        }
    }

    private void checkMessage(Intent intent){
        if (null != intent)
        {
            resetIntentValues();
        }
    }

    /**
     * Will check main Activity intent and if it contains any PushWoosh data, will clear it
     */
    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    //  </editor-fold>

    // Manage receivers registration in the onPause/onResume functions
    @Override
    public void onResume() {
        super.onResume();

        //Re-register receivers on resume
        registerReceivers();

        if (lv != null) {
            if (lv.getCount() > currentPosition){
                lv.setSelectionFromTop(currentPosition, 0);
            }else {
                lv.setSelectionFromTop(0,0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Unregister receivers on pause
        unregisterReceivers();

        currentPosition = lv.getFirstVisiblePosition();
    }
}
