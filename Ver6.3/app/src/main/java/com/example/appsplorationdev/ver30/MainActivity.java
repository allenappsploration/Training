package com.example.appsplorationdev.ver30;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.color.black;


public class MainActivity extends KeyConfigure {

    private ProgressDialog pDialog;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNewsTitles;

    private ProgressBar progressBar;
    private Handler mHandler;
    private boolean hasCallback;
    private boolean isLoading = false;

    private int previousItemCount = 0;

    ArrayList<HashMap<String, String>> NewsList;
    ArrayList<HashMap<String, String>> mDrawerItems;
    JSONArray JSONArray = null;
    LazyAdapter adapter;
    NaviDrawAdapter naviDrawAdapter;

    // Flag for current page
    int current_page = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        mTitle = mDrawerTitle = getTitle();
        //mNewsTitles = getResources().getStringArray(R.array.titles_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mNewsTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle (
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
        NewsList = new ArrayList<HashMap<String, String>>();
        mDrawerItems = new ArrayList<HashMap<String, String>>();

        new GetNaviDrawerItem().execute();
        new loadMoreListView().execute();
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            selectItem(position);
//            view.setBackgroundColor(getResources().getColor(android.R.color.black));
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt(NewsFragment.ARG_TITLE_NUMBER, position);
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
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class NewsFragment extends Fragment {
        public static final String ARG_TITLE_NUMBER = "title_number";

        public NewsFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_listview, container, false);
            int i = getArguments().getInt(ARG_TITLE_NUMBER);
            //String title = getResources().getStringArray(R.array.titles_array)[i];

            //getActivity().setTitle(title);
            return rootView;
        }
    }

    private class GetNaviDrawerItem extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonNaviStr = sh.makeServiceCall(CAT, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonNaviStr);

            if (jsonNaviStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonNaviStr);

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

            naviDrawAdapter = new NaviDrawAdapter(MainActivity.this, mDrawerItems);
            mDrawerList.setAdapter(naviDrawAdapter);

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
        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(MainActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        protected Boolean doInBackground(Void... unused) {
            // increment current page
            current_page += 1;

            Intent in = getIntent();
            String CATEGORY_ID = in.getStringExtra(KEY_ID);

            Log.i("Category ID : ", String.valueOf(CATEGORY_ID));

            if ((CATEGORY_ID == null)||(CATEGORY_ID.trim().length() < 1))
            {
                CATEGORY_ID = "17";
            }else {
                CATEGORY_ID = in.getStringExtra(KEY_ID);
            }

            // Next page request
            String newURL = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=" + CATEGORY_ID + "&page_no=" + current_page;

            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(newURL, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr.trim().length() > 0) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray = jsonObj.getJSONArray(KEY_RESULTS);

                    Log.d("JSONARRAY:", String.valueOf(JSONArray));

                    if ((JSONArray != null)||(JSONArray.toString().trim().length() > 0)) {
                        for (int i = 0; i < JSONArray.length(); i++) {
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();
                            //Element e = (Element) nl.item(i);
                            JSONObject c = JSONArray.getJSONObject(i);
                            // adding each child node to HashMap key => value
                            map.put(KEY_ARTICLE_URL, c.getString(KEY_ARTICLE_URL));
                            map.put(KEY_ARTICLE_TITLE, c.getString(KEY_ARTICLE_TITLE));
                            map.put(KEY_CATEGORY_NAME, c.getString(KEY_CATEGORY_NAME));
                            map.put(KEY_ARTICLE_PUBDATE, c.getString(KEY_ARTICLE_PUBDATE));
                            map.put(KEY_ARTICLE_THUMB_URL, c.getString(KEY_ARTICLE_THUMB_URL));

                            // adding HashList to ArrayList
                            NewsList.add(map);
                        }
                    }
                    else
                    {
                        ListView mainList = (ListView)findViewById(R.id.list);
                        mainList.setBackgroundColor(black);
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
//            if (pDialog.isShowing())
//                pDialog.dismiss();

            final ListView lv = (ListView) findViewById(R.id.list);

            int currentPosition = lv.getFirstVisiblePosition();

            adapter = new LazyAdapter(MainActivity.this, NewsList, 30, 30);
            lv.setAdapter(adapter);

            final View footer = getLayoutInflater().inflate(R.layout.footer_progressbar, null);
            progressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
            progressBar.setIndeterminate(true);
            lv.addFooterView(footer);

            lv.setSelectionFromTop(currentPosition, 0);

            lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //previousItemCount = totalItemCount;
                    //Log.i("First ", String.valueOf(firstVisibleItem) + " Total : " + String.valueOf(totalItemCount) + " condition : " + String.valueOf(totalItemCount - CONSTANT_TO_LOAD_MORE));
                    //Log.i("i", "Previous :" + String.valueOf(previousItemCount) + "Total :" + String.valueOf(totalItemCount));
                    if (previousItemCount < totalItemCount) {
                        isLoading = false;
                        if (firstVisibleItem >= (totalItemCount - CONSTANT_TO_LOAD_MORE) && isLoading == false) {
                            new loadMoreListView().execute();
                            isLoading = true;
                            previousItemCount = totalItemCount;
                        }
                    }
                    lv.removeFooterView(footer);
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
                            SingleContactActivity.class);
                    in.putExtra(KEY_ARTICLE_TITLE, title);
                    in.putExtra(KEY_ARTICLE_THUMB_URL, image);
                    in.putExtra(KEY_ARTICLE_URL, hidItm);
                    startActivity(in);

                    overridePendingTransition(R.anim.leave_left_to_right, R.anim.leave_right_to_left);
                }
            });
        }
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


    private Runnable showMore = new Runnable(){
        public void run(){
            boolean noMoreToShow = adapter.showMore(); //show more views and find out if
            progressBar.setVisibility(noMoreToShow? View.GONE : View.VISIBLE);
            hasCallback = false;
        }
    };
}
