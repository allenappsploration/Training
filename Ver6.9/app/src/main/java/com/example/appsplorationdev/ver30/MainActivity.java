package com.example.appsplorationdev.ver30;

import android.app.ActionBar;
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
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends KeyConfigure {

    private ProgressDialog pDialog;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private ProgressBar progressBar;
    private Handler mHandler;
    private boolean hasCallback;
    private boolean isLoading = false;
    private boolean isEnd = false;

    private int previousItemCount = 0;
    private int currentPosition = 0;

    private int loadCounter = 0;

    ListView lv;
    View footer;

    ArrayList<HashMap<String, String>> NewsList;
    ArrayList<HashMap<String, String>> mDrawerItems;
    JSONArray JSONArray = null;
    LazyAdapter lazyAdapter, temp;
    DrawerAdapter drawerAdapter;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    // Flag for current page
    int current_page = 0;

    @Override
	public void onStart() {
        super.onStart();
		//---------- START: Set up main list and footer views
		
		footer = getLayoutInflater().inflate(R.layout.footer_progressbar, null);
		footer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});         // nothing happen when click on footer
		progressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
		progressBar.setIndeterminate(true);
			
		
		lv = (ListView) findViewById(R.id.list);			
		lazyAdapter = new LazyAdapter(MainActivity.this, NewsList, 30, 30);
		lv.addFooterView(footer);
		lv.setAdapter(lazyAdapter);
		lv.removeFooterView(footer);
		
		new loadMoreListView().execute();
	
		//---------- END: Set up main list and footer views
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {
            setContentView(R.layout.activity_main);
            //get the action bar
            ActionBar actionBar = getActionBar();

            // Enabling back navigation on Action Bar icon
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            mHandler = new Handler();

            mTitle = mDrawerTitle = getTitle();
            //mNewsTitles = getResources().getStringArray(R.array.titles_array);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);
//            mDrawerList.setExpanded(true);

            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mNewsTitles));
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            if (savedInstanceState == null) {
                selectItem(0);
            }

            mDrawerToggle.syncState();              // Sync the state of navigation drawer

            NewsList = new ArrayList<HashMap<String, String>>();
            mDrawerItems = new ArrayList<HashMap<String, String>>();

            new getNaviDrawerItem().execute();		

            handleSearchIntent(getIntent());

            /* -------------- Push Woosh Initialization ----------------*/

            //Register receivers for push notifications
            registerReceivers();

            //Create and start push manager
            PushManager pushManager = PushManager.getInstance(this);

            //Start push manager, this will count app open for Pushwoosh stats as well
            try {
                pushManager.onStartup(this);
            }
            catch(Exception e)
            {
                //push notifications are not available or AndroidManifest.xml is not configured properly
            }

            //Register for push!
            pushManager.registerForPushNotifications();

//            checkMessage(getIntent());

            /*----------- End of Push Woosh initialization -------------*/

        }else {
            showAlertDialog(MainActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }

    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new NewsFragment();
        Bundle args = new Bundle();
//        args.putInt(NewsFragment.ARG_TITLE_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mNewsTitles[position]);
        //mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class NewsFragment extends Fragment {
//        public static final String ARG_TITLE_NUMBER = "title_number";

        public NewsFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_listview, container, false);
//            int i = getArguments().getInt(ARG_TITLE_NUMBER);
            //String title = getResources().getStringArray(R.array.titles_array)[i];

            //getActivity().setTitle(title);
            return rootView;
        }
    }

    private class getNaviDrawerItem extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {

            JSONParser jParser = new JSONParser();

            JSONObject jsonObj = jParser.getJSONFromUrl(CATEGORY_URL);
            // Making a request to url and getting response

            Log.d("Response: ", "> " + String.valueOf(jsonObj));

            if (jsonObj != null) {

                try {
                    // Getting JSON Array node
                    JSONArray = jsonObj.getJSONArray(KEY_RESULTS);

                    for (int i = 0; i < JSONArray.length(); i++) {

                        JSONObject c = JSONArray.getJSONObject(i);

                        // creating new HashMap
                        HashMap<String, String> naviItems = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        naviItems.put(KEY_ID, c.getString(KEY_ID));
                        naviItems.put(KEY_NAME, c.getString(KEY_NAME));

                        // adding HashList to ArrayList
                        mDrawerItems.add(naviItems);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawerAdapter = new DrawerAdapter(MainActivity.this, mDrawerItems);
                    mDrawerList.setAdapter(drawerAdapter);
                    drawerAdapter.notifyDataSetChanged();
                }
            });

            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String hid_ID;
                    hid_ID = ((TextView) view.findViewById(R.id.hidID)).getText().toString();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra(KEY_ID, hid_ID);
                    startActivity(i);

                    overridePendingTransition(0, 0);
                }
            });

            mDrawerList.setOnTouchListener(new ListView.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    int action = event.getAction();
                    switch (action)
                    {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;

                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            // Showing progress dialog
            if (loadCounter == 0) {
                loadCounter += 1;
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.show();
            }else {

                if (isEnd) {
                    lv.removeFooterView(footer);
                } else {
                    if (lv.getFooterViewsCount() == 0) {
                        lv.addFooterView(footer);
                    }
                }
            }
        }

        protected Boolean doInBackground(Void... unused) {
            // increment current page
            current_page += 1;

            Intent in = getIntent();
            String CATEGORY_ID = in.getStringExtra(KEY_ID);

            if ((CATEGORY_ID == null) || (CATEGORY_ID.trim().length() < 1)) {
                CATEGORY_ID = "17";
            }

            // Next page request
            String newURL = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=" + CATEGORY_ID + "&page_no=" + current_page + "&appview=1";

            JSONParser jParser = new JSONParser();

            // Making a request to url and getting response
            JSONObject jsonObj = jParser.getJSONFromUrl(newURL);

            Log.d("Response: ", "> " + String.valueOf(jsonObj));

            if (jsonObj != null) {

                try {
                    // Getting JSON Array node
                    JSONArray = jsonObj.optJSONArray(KEY_RESULTS);

                    if ((JSONArray != null) && (JSONArray.length() > 0)) {
                        for (int i = 0; i < JSONArray.length(); i++) {
                            // creating new HashMap
                            final HashMap<String, String> map = new HashMap<String, String>();
                            //Element e = (Element) nl.item(i);
                            JSONObject c = JSONArray.getJSONObject(i);

                            // adding each child node to HashMap key => value
                            map.put(KEY_ARTICLE_URL, c.getString(KEY_ARTICLE_URL));
                            map.put(KEY_ARTICLE_TITLE, c.getString(KEY_ARTICLE_TITLE));
                            map.put(KEY_CATEGORY_NAME, c.getString(KEY_CATEGORY_NAME));
                            map.put(KEY_ARTICLE_PUBDATE, convertDate(c.getString(KEY_ARTICLE_PUBDATE)));
                            map.put(KEY_SMALL_ARTICLE_THUMB_URL, c.getString(KEY_SMALL_ARTICLE_THUMB_URL));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // adding HashList to ArrayList
                                    NewsList.add(map);
                                }
                            });
                        }
                    } else {
                        return isEnd = true;
//                        throw new JSONException("JSONArray is empty, no results.");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return (null);
        }

        protected void onPostExecute(Boolean unused) {
            // Dismiss the progress dialog
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }


            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        lv.setVisibility(View.GONE);
						currentPosition = lv.getFirstVisiblePosition();
                        MainActivity.this.lazyAdapter.notifyDataSetChanged();
                        Log.i("lazyAdapter", "notifyDataSetChanged()");
                        lv.setSelectionFromTop(currentPosition, 0);
//                        lv.setVisibility(View.VISIBLE);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    isLoading = false;
                    if (previousItemCount < totalItemCount) {
                        if (firstVisibleItem >= (totalItemCount - CONSTANT_TO_LOAD_MORE) && isLoading == false) {

                            new loadMoreListView().execute();
                            isLoading = true;
                            previousItemCount = totalItemCount;
                        }
                    }
                    if (isEnd) {
                        lv.removeFooterView(footer);
                    }
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
                    in.putExtra("fromMainActivity", true);
                    startActivity(in);

                    overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
                }
            });
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

    public void onBackPressed(){
        new AlertDialog.Builder(this);
            /*    .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Publisher App")
                .setMessage("Are you sure you want to close Publisher App?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {*/
                        Intent intent = new Intent(Intent.ACTION_MAIN);  // Method to close the application
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    /*}
                })
                .setNegativeButton("No", null)
                .show();*/
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

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

//        switch (item.getItemId()){
//            case R.id.menu_search:
//                onSearchRequested();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    // Get search intent passing value
    @Override
    protected void onNewIntent(Intent intent) {
        //handleSearchIntent(intent);

        setIntent(intent);

//        checkMessage(intent);
    }

    private void handleSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            Log.i("QUERY :", String.valueOf(query));

            Intent in = new Intent(getApplicationContext(),
                    SearchActivity.class);
            in.putExtra("query", query);
            startActivity(in);

            overridePendingTransition(0, 0);

//            searchList = new ArrayList<HashMap<String, String>>();
//            new searchFunction().execute();

        }
    }

    private Runnable showMore = new Runnable(){
        public void run(){
            boolean noMoreToShow = lazyAdapter.showMore(); //show more views and find out if
            progressBar.setVisibility(noMoreToShow? View.GONE : View.VISIBLE);
            hasCallback = false;
        }
    };

    //-------------------------------- Push Woosh implementation ----------------------------------
    //Registration receiver
    BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver()
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
            checkMessage(intent);
//            Intent noficationIntent = new Intent(getApplicationContext(),SingleActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, noficationIntent, 0);
//            startActivity(noficationIntent);
        }
    };

    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent)
        {
            //JSON_DATA_KEY contains JSON payload of push notification.
            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
        }
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

    // Manage receivers registration in the onPause/onResume functions
    @Override
    public void onResume() {
        super.onResume();

        //Re-register receivers on resume
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();

        //Unregister receivers on pause
        unregisterReceivers();
    }

    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString("url");
    }

    // Push woosh methods
    private void checkMessage(Intent intent){
        if (null != intent)
        {
//            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
//            {
//                showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
//            }
//            else if (intent.hasExtra(PushManager.REGISTER_EVENT))
//            {
//                showMessage("register");
//            }
//            else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
//            {
//                showMessage("unregister");
//            }
//            else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
//            {
//                showMessage("register error");
//            }
//            else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
//            {
//                showMessage("unregister error");
//            }
//
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

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onNewIntent(Intent intent)
//    {
//        super.onNewIntent(intent);
//        setIntent(intent);
//
//        checkMessage(intent);
//    }
}
