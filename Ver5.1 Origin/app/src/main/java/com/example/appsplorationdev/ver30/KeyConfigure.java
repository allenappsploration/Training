package com.example.appsplorationdev.ver30;

import android.app.Activity;

/**
 * Created by appsplorationdev on 8/22/14.
 */
public abstract class KeyConfigure extends Activity {

    // JSON Node names
    public static final String KEY_RESULTS = "results";
    public static final String KEY_ARTICLE_ID = "article_id";
    public static final String KEY_ARTICLE_URL = "article_url";
    public static final String KEY_ARTICLE_TITLE = "article_title";
    public static final String KEY_ARTICLE_PUBDATE = "article_pubdate";
    public static final String KEY_ARTICLE_HAS_THUMB = "article_has_thumb";
    public static final String KEY_ARTICLE_THUMB_URL = "article_thumb_url";
    public static final String KEY_CATEGORY_NAME = "category_name";

    // URL to get contacts JSON
    public static String URL1 = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=17&page_no=1";
    public static String URL2 = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=17&page_no=2";
    public static String URL3 = "http://hot.appsploration.com/hot/api.php?api=articles&site_id=8&category_id=17&page_no=3";
}
