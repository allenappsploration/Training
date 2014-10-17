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
import com.commonsware.cwac.merge.MergeAdapter;

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

    private ListView lv;
    private View footer;

    ArrayList<HashMap<String, String>> NewsList;
    ArrayList<HashMap<String, String>> mDrawerItems;
    JSONArray JSONArray = null;
    LazyAdapter lazyAdapter;
    DrawerAdapter drawerAdapter;

    MergeAdapter mergeAdapter;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    // Flag for current page
    int current_page = 0;

    @Override
	public void onStart() {
        super.onStart();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();        // true or false

        if (isInternetPresent) {
        // <editor-fold desc="Set up main list and footer views">

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

        // </editor-fold>
        }else {
            showAlertDialog(MainActivity.this, "Network Error", "No Internet Connection", false);
        }
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
            }

            //Register for push!
            pushManager.registerForPushNotifications();

            // </editor-fold Push Notification initialization>

        }else {
            showAlertDialog(MainActivity.this, "Network Error", "No Internet Connection", false);
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

            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View header1 = inflater.inflate(R.layout.drawer_banner, null);
            final View header2 = inflater.inflate(R.layout.drawer_home, null);
            // <editor-fold desc="header2 OnClickListener">
            header2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra(KEY_ID, "17");
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    }
            });
            // </editor-fold header 2>
            final View header3 = inflater.inflate(R.layout.drawer_header, null);
            final View header4 = inflater.inflate(R.layout.drawer_footer, null);
            final View header5 = inflater.inflate(R.layout.drawer_tutorial, null);
            // <editor-fold desc="header5 OnClickListener">
            header5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                }
            });
            // </editor-fold>

            mergeAdapter = new MergeAdapter();
            drawerAdapter = new DrawerAdapter(MainActivity.this, mDrawerItems);
            mergeAdapter.addView(header1);
            mergeAdapter.addView(header2);
            mergeAdapter.addView(header3);
            mergeAdapter.addAdapter(drawerAdapter);
            mergeAdapter.addView(header4);
            mergeAdapter.addView(header5);
            mDrawerList.setAdapter(mergeAdapter);
            drawerAdapter.notifyDataSetChanged();

            // <editor-fold desc="NavigationDrawerList OnClickListener">
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cd = new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();        // true or false

                    if (isInternetPresent) {

                        String hid_ID;
                        hid_ID = ((TextView) view.findViewById(R.id.hidID)).getText().toString();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra(KEY_ID, hid_ID);
                        startActivity(i);

                        overridePendingTransition(0, 0);
                    }else {
                        showAlertDialog(MainActivity.this, "Network Error", "No Internet Connection", false);
                    }
                }
            });
            // </editor-fold>

            // <editor-fold desc="NavigationDrawerList OnTouchListener">
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
            // </editor-fold>
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

            onTrimMemory(TRIM_MEMORY_RUNNING_LOW);
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

                            String substring = c.getString(KEY_ARTICLE_TITLE);

                            substring = replaceQuote(substring);

                            // adding each child node to HashMap key => value
                            map.put(KEY_ARTICLE_URL, c.getString(KEY_ARTICLE_URL));
                            map.put(KEY_ARTICLE_TITLE, substring);
                            map.put(KEY_CATEGORY_NAME, c.getString(KEY_CATEGORY_NAME));
                            map.put(KEY_ARTICLE_PUBDATE, convertDate(c.getString(KEY_ARTICLE_PUBDATE)));
                            map.put(KEY_SMALL_ARTICLE_THUMB_URL, c.getString(KEY_SMALL_ARTICLE_THUMB_URL));
                            map.put(KEY_ARTICLE_HAS_THUMB, c.getString(KEY_ARTICLE_HAS_THUMB));

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

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();        // true or false

            if (isInternetPresent) {
                // Dismiss the progress dialog
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                currentPosition = lv.getFirstVisiblePosition();
                lv.setSelectionFromTop(currentPosition, 0);
                MainActivity.this.lazyAdapter.notifyDataSetChanged();
                Log.i("lazyAdapter", "notifyDataSetChanged()");

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

                        cd = new ConnectionDetector(getApplicationContext());
                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
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
                        } else {
                            showAlertDialog(MainActivity.this, "Network Error", "No Internet Connection", false);
                        }
                    }
                });
            }else {
                showAlertDialog(MainActivity.this, "Network Error", "No Internet Connection", false);
            }
        }
    }

    private String replaceQuote(String params) {

        // <editor-fold desc="General">
        params = params.replace("&copy;" ,"©");
        params = params.replace("&reg;" ,"®");
        params = params.replace("&trade;" ,"™");
        params = params.replace("&amp;", "&");
        params = params.replace("&para;" ,"¶");
        params = params.replace("&ndash;" ,"–");
        params = params.replace("&mdash;" ,"—");
        params = params.replace("&not;" ,"¬");
        params = params.replace("&lsaquo;" ,"‹");
        params = params.replace("&rsaquo;" ,"›");
        params = params.replace("&laquo;" ,"«");
        params = params.replace("&raquo;" ,"»");
        params = params.replace("&larr;" ,"←");
        params = params.replace("&uarr;" ,"↑");
        params = params.replace("&rarr;" ,"→");
        params = params.replace("&darr;" ,"↓");
        params = params.replace("&spades;" ,"♠");
        params = params.replace("&clubs;" ,"♣");
        params = params.replace("&hearts;" ,"♥");
        params = params.replace("&diams;" ,"♦");
        params = params.replace("&nbsp;", " ");
        // </editor-fold>

        // <editor-fold desc="Accents">
        params = params.replace("&acute;" ,"´");
        params = params.replace("&macr;" ,"¯");
        params = params.replace("&uml;" ,"¨");
        params = params.replace("&cedil;" ,"¸");
        params = params.replace("&middot;" ,"·");
        params = params.replace("&ordm;" ,"º");
        params = params.replace("&ordf;" ,"ª");
        // </editor-fold>

        // <editor-fold desc="Letter A Accents">
        params = params.replace("&Agrave;" ,"À");
        params = params.replace("&Aacute;" ,"Á");
        params = params.replace("&Acirc;" ,"Â");
        params = params.replace("&Atilde;" ,"Ã");
        params = params.replace("&Auml;" ,"Ä");
        params = params.replace("&Aring;" ,"Å");
        params = params.replace("&agrave;" ,"à");
        params = params.replace("&aacute;" ,"á");
        params = params.replace("&acirc;" ,"â");
        params = params.replace("&atilde;" ,"ã");
        params = params.replace("&auml;" ,"ä");
        params = params.replace("&aring;" ,"å");
        // </editor-fold>

        // <editor-fold desc="Letter E Accents">
        params = params.replace("&Egrave;" ,"È");
        params = params.replace("&Eacute;" ,"É");
        params = params.replace("&Ecirc;" ,"Ê");
        params = params.replace("&Euml;" ,"Ë");
        params = params.replace("&egrave;" ,"è");
        params = params.replace("&eacute;" ,"é");
        params = params.replace("&ecirc;" ,"ê");
        params = params.replace("&euml;" ,"ë");
        // </editor-fold>

        // <editor-fold desc="Letter I Accents">
        params = params.replace("&Igrave;" ,"Ì");
        params = params.replace("&Iacute;" ,"Í");
        params = params.replace("&Icirc;" ,"Î");
        params = params.replace("&Iuml;" ,"Ï");
        params = params.replace("&igrave;" ,"ì");
        params = params.replace("&iacute;" ,"í");
        params = params.replace("&icirc;" ,"î");
        params = params.replace("&iuml;" ,"ï");
        // </editor-fold>

        // <editor-fold desc="Letter O Accents">
        params = params.replace("&Ograve;" ,"Ò");
        params = params.replace("&Oacute;" ,"Ó");
        params = params.replace("&Ocirc;" ,"Ô");
        params = params.replace("&Otilde;" ,"Õ");
        params = params.replace("&Ouml;" ,"Ö");
        params = params.replace("&Oslash;" ,"Ø");
        params = params.replace("&ograve;" ,"ò");
        params = params.replace("&oacute;" ,"ó");
        params = params.replace("&ocirc;" ,"ô");
        params = params.replace("&otilde;" ,"õ");
        params = params.replace("&ouml;" ,"ö");
        params = params.replace("&oslash;" ,"ø");
        // </editor-fold>

        // <editor-fold desc="Letter U Accents">
        params = params.replace("&Ugrave;" ,"Ù");
        params = params.replace("&Uacute;" ,"Ú");
        params = params.replace("&Ucirc;" ,"Û");
        params = params.replace("&Uuml;" ,"Ü");
        params = params.replace("&ugrave;" ,"ù");
        params = params.replace("&uacute;" ,"ú");
        params = params.replace("&ucirc;" ,"û");
        params = params.replace("&uuml;" ,"ü");
        // </editor-fold>

        // <editor-fold desc="Additional Accents">
        params = params.replace("&AElig;" ,"Æ");
        params = params.replace("&aelig;" ,"æ");
        params = params.replace("&Ccedil;" ,"Ç");
        params = params.replace("&ccedil;" ,"ç");
        params = params.replace("&Ntilde;" ,"Ñ");
        params = params.replace("&ntilde;" ,"ñ");
        params = params.replace("&Yacute;" ,"Ý");
        params = params.replace("&yacute;" ,"ý");
        params = params.replace("&yuml;" ,"ÿ");
        params = params.replace("&ETH;" ,"Ð");
        params = params.replace("&eth;" ,"ð");
        params = params.replace("&THORN;" ,"Þ");
        params = params.replace("&thorn;" ,"þ");
        params = params.replace("&szlig;" ,"ß");
        // </editor-fold>

        // <editor-fold desc="Quotes and Punctuation">
        params = params.replace("&iexcl;", "¡");
        params = params.replace("&iquest;" ,"¿");
        params = params.replace("&hellip;" ,"…");
        params = params.replace("&dagger;" ,"†");
        params = params.replace("&Dagger;" ,"‡");
        params = params.replace("&permil;" ,"‰");
        params = params.replace("&sup1;" ,"¹");
        params = params.replace("&sup2;" ,"²");
        params = params.replace("&sup3;" ,"³");
        params = params.replace("&frac14;" ,"¼");
        params = params.replace("&frac12;" ,"½");
        params = params.replace("&frac34;" ,"¾");
        params = params.replace("&oline;" ,"‾");
        params = params.replace("&lsquo;" ,"‘");
        params = params.replace("&rsquo;" ,"’");
        params = params.replace("&ldquo;" ,"“");
        params = params.replace("&rdquo;" ,"”");
        params = params.replace("&sbquo;" ,"‚");
        params = params.replace("&bdquo;" ,"„");
        params = params.replace("&quot;", "\"");
        // </editor-fold>

        // <editor-fold desc="Slashes and Brackets">
        params = params.replace("&frasl;", "/");
        params = params.replace("&brvbar;" ,"¦");
        // </editor-fold>

        // <editor-fold desc="Money and Math Symbols">
        params = params.replace("&euro;" ,"€");
        params = params.replace("&cent;" ,"¢");
        params = params.replace("&pound;" ,"£");
        params = params.replace("&curren;" ,"¤");
        params = params.replace("&yen;" ,"¥");
        params = params.replace("&times;" ,"×");
        params = params.replace("&divide;" ,"÷");
        params = params.replace("&plusmn;" ,"±");
        params = params.replace("&micro;" ,"µ");
        params = params.replace("&lt;", "<");
        params = params.replace("&gt;", ">");
        params = params.replace("&deg;" ,"°");
        params = params.replace("&sect;" ,"§");
        params = params.replace("&le;" ,"≤");
        params = params.replace("&ge;" ,"≥");
        params = params.replace("&sum;" ,"∑");
        params = params.replace("&minus;" ,"−");
        params = params.replace("&lowast;" ,"∗");
        params = params.replace("&radic;" ,"√");
        params = params.replace("&prop;" ,"∝");
        params = params.replace("&infin;" ,"∞");
        params = params.replace("&fnof;" ,"ƒ");
        params = params.replace("&circ;" ,"ˆ");
        params = params.replace("&tilde;" ,"˜");
        params = params.replace("&int;" ,"∫");
        params = params.replace("&there4;" ,"∴");
        params = params.replace("&sim;" ,"∼");
        params = params.replace("&cong;" ,"≅");
        params = params.replace("&asymp;" ,"≈");
        params = params.replace("&ne;" ,"≠");
        params = params.replace("&equiv;" ,"≡");
        params = params.replace("&lang;" ,"⟨");
        params = params.replace("&rang;" ,"⟩");
        params = params.replace("&lceil;" ,"⌈");
        params = params.replace("&rceil;" ,"⌉");
        params = params.replace("&lfloor;" ,"⌊");
        params = params.replace("&rfloor;" ,"⌋");
        params = params.replace("&oplus;" ,"⊕");
        params = params.replace("&otimes;" ,"⊗");
        params = params.replace("&part;" ,"∂");
        params = params.replace("&exist;" ,"∃");
        params = params.replace("&empty;" ,"∅");
        params = params.replace("&nabla;" ,"∇");
        params = params.replace("&isin;" ,"∈");
        params = params.replace("&notin;" ,"∉");
        params = params.replace("&ni;" ,"∋");
        params = params.replace("&prod;" ,"∏");
        params = params.replace("&ang;" ,"∠");
        params = params.replace("&and;" ,"∧");
        params = params.replace("&or;" ,"∨");
        params = params.replace("&cap;" ,"∩");
        params = params.replace("&cup;" ,"∪");
        params = params.replace("&sub;" ,"⊂");
        params = params.replace("&sup;" ,"⊃");
        params = params.replace("&nsub;" ,"⊄");
        params = params.replace("&sube;" ,"⊆");
        params = params.replace("&supe;" ,"⊇");
        params = params.replace("&perp;" ,"⊥");
        params = params.replace("&sdot;" ,"⋅");
        params = params.replace("&OElig;" ,"Œ");
        params = params.replace("&oelig;" ,"œ");
        params = params.replace("&Scaron;" ,"Š");
        params = params.replace("&scaron;" ,"š");
        params = params.replace("&Yuml;" ,"Ÿ");
        params = params.replace("&shy;" ,"    ");
        params = params.replace("&prime;" ,"′");
        params = params.replace("&Prime;" ,"″");
        params = params.replace("&crarr;" ,"↵");
        params = params.replace("&forall;" ,"∀");
        params = params.replace("&apos;", "'");
        params = params.replace("&bull;" ,"•");
        params = params.replace("&harr;" ,"↔");
        params = params.replace("&loz;" ,"◊");
        params = params.replace("&lArr;" ,"⇐");
        params = params.replace("&uArr;" ,"⇑");
        params = params.replace("&rArr;" ,"⇒");
        params = params.replace("&dArr;" ,"⇓");
        params = params.replace("&hArr;" ,"⇔");
        params = params.replace("&weierp;" ,"℘");
        params = params.replace("&image;" ,"ℑ");
        params = params.replace("&real;" ,"ℜ");
        params = params.replace("&alefsym;" ,"ℵ");
        // </editor-fold>

        // <editor-fold desc="Greek">
        params = params.replace("&Alpha;" ,"Α");
        params = params.replace("&Beta;" ,"Β");
        params = params.replace("&Gamma;" ,"Γ");
        params = params.replace("&Delta;" ,"Δ");
        params = params.replace("&Epsilon;" ,"Ε");
        params = params.replace("&Zeta;" ,"Ζ");
        params = params.replace("&Eta;" ,"Η");
        params = params.replace("&Theta;" ,"Θ");
        params = params.replace("&Iota;" ,"Ι");
        params = params.replace("&Kappa;" ,"Κ");
        params = params.replace("&Lambda;" ,"Λ");
        params = params.replace("&Mu;" ," Μ");
        params = params.replace("&Nu;" ,"Ν");
        params = params.replace("&Xi;" ,"Ξ");
        params = params.replace("&Omicron;" ,"Ο");
        params = params.replace("&Pi;" ,"Π");
        params = params.replace("&Rho;" ,"Ρ");
        params = params.replace("&Sigma;" ,"Σ");
        params = params.replace("&Tau;" ,"Τ");
        params = params.replace("&Upsilon;" ,"Υ");
        params = params.replace("&Phi;" ,"Φ");
        params = params.replace("&Chi;" ,"Χ");
        params = params.replace("&Psi;" ,"Ψ");
        params = params.replace("&Omega;" ,"Ω");
        params = params.replace("&alpha;" ,"α");
        params = params.replace("&beta;" ,"β");
        params = params.replace("&gamma;" ,"γ");
        params = params.replace("&delta;" ,"δ");
        params = params.replace("&epsilon;" ,"ε");
        params = params.replace("&zeta;" ,"ζ");
        params = params.replace("&eta;" ,"η");
        params = params.replace("&theta;" ,"θ");
        params = params.replace("&iota;" ,"ι");
        params = params.replace("&kappa;" ,"κ");
        params = params.replace("&lambda;" ,"λ");
        params = params.replace("&mu;" ,"μ");
        params = params.replace("&nu;" ,"ν");
        params = params.replace("&xi;" ,"ξ");
        params = params.replace("&omicron;" ,"ο");
        params = params.replace("&pi;" ,"π");
        params = params.replace("&rho;" ,"ρ");
        params = params.replace("&sigmaf;" ,"ς");
        params = params.replace("&sigma;" ,"σ");
        params = params.replace("&tau;" ,"τ");
        params = params.replace("&upsilon;" ,"υ");
        params = params.replace("&phi;" ,"φ");
        params = params.replace("&chi;" ,"χ");
        params = params.replace("&psi;" ,"ψ");
        params = params.replace("&omega;" ,"ω");
        params = params.replace("&thetasym;" ,"ϑ");
        params = params.replace("&upsih;" ,"ϒ");
        params = params.replace("&piv;" ,"ϖ");
        // </editor-fold>

        return params;
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
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
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
        }
    }

    private Runnable showMore = new Runnable(){
        public void run(){
            boolean noMoreToShow = lazyAdapter.showMore(); //show more views and find out if
            progressBar.setVisibility(noMoreToShow? View.GONE : View.VISIBLE);
            hasCallback = false;
        }
    };

    //   <editor-fold desc="Push Woosh implementation">
    BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver()  //Receiver registration
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
//            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
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

    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString("url");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
